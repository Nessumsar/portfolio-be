package com.lkrs.portfoliobe.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lkrs.portfoliobe.model.CommitData;
import com.lkrs.portfoliobe.model.Platform;
import com.lkrs.portfoliobe.model.Repository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommitService {
    @Value("${github.commits.url}")
    private String GITHUB_URL;
    @Value("${gitlab.commits.url}")
    private String GITLAB_URL;

    private final ObjectMapper objectMapper;
    private final RepositoryService repositoryService;
    private final RestTemplate restTemplate;

    public CommitService(ObjectMapper objectMapper, RepositoryService repositoryService, RestTemplate restTemplate) {
        this.objectMapper = objectMapper;
        this.repositoryService = repositoryService;
        this.restTemplate = restTemplate;
    }

    public List<CommitData> getAllCommitData() {
        List<CommitData> allCommits = new ArrayList<>();
        List<Repository> repositories = repositoryService.getRepositoriesUpdatedWithinOneYear();
        for (Repository repository : repositories) {
            List<CommitData> commitData = fetchCommitsOnRepository(repository);
            allCommits.addAll(commitData);
        }
        return aggregateCommits(allCommits);
    }

    public List<CommitData> fetchCommitsOnRepository(Repository repository) {
        ResponseEntity<String> response = restTemplate.exchange(getUrl(repository), HttpMethod.GET, null, String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            log.warn("Failed to fetch events: {}", response.getStatusCode());
            return new ArrayList<>();
        }

        JsonNode eventsArray;
        try {
            eventsArray = objectMapper.readTree(response.getBody());
        } catch (Exception e) {
            log.warn("JsonProcessingException: ", e);
            return new ArrayList<>();
        }

        if (repository.getPlatform() == Platform.GITHUB) {
            return processGitHubCommits(eventsArray, repository);
        } else {
            return processGitLabEvent(eventsArray, repository);
        }
    }

    private String getUrl(Repository repository) {
        String url;
        if (repository.getPlatform().equals(Platform.GITHUB)) {
            url = GITHUB_URL;
            return url.replace(":repository", repository.getName());
        } else {
            url = GITLAB_URL;
            return url.replace(":id", String.valueOf(repository.getId()));
        }
    }

    private static List<CommitData> processGitLabEvent(JsonNode eventsArray, Repository repository) {
        List<CommitData> result = new ArrayList<>();
        for (JsonNode json : eventsArray) {
            if (json.get("title").asText().contains("Merge remote-tracking branch")) {
                continue;
            }

            String createdAt = json.get("authored_date").asText();
            LocalDate date = ZonedDateTime.parse(createdAt).toLocalDate();
            CommitData commitData = new CommitData(repository.getId(), date, repository.getPlatform());
            result.add(commitData);
        }
        return result;
    }

    private static List<CommitData> processGitHubCommits(JsonNode eventsArray, Repository repository) {
        List<CommitData> result = new ArrayList<>();
        for (JsonNode json : eventsArray) {
            JsonNode commit = json.get("commit");
            String createdAt = commit.get("author").get("date").asText();
            LocalDate date = ZonedDateTime.parse(createdAt).toLocalDate();
            if (date.isBefore(LocalDate.now().minusYears(1))) {
                continue;
            }
            CommitData commitData = new CommitData(repository.getId(), date, repository.getPlatform());
            result.add(commitData);
        }
        return result;
    }

    private static List<CommitData> aggregateCommits(List<CommitData> commits) {
        // Composite key [repoId, date, platform] and sum counts
        Map<List<?>, Integer> summed = commits.stream()
                .collect(Collectors.groupingBy(
                        c -> List.of(c.getRepositoryId(), c.getDate(), c.getPlatform()),
                        Collectors.summingInt(CommitData::getCount)
                ));

        // 2) Map each grouped entry back into a CommitData
        return summed.entrySet().stream()
                .map(entry -> {
                    List<?> key = entry.getKey();
                    int repoId     = (Integer) key.get(0);
                    LocalDate dt   = (LocalDate)  key.get(1);
                    Platform plat  = (Platform)   key.get(2);
                    CommitData cd  = new CommitData(repoId, dt, plat);
                    cd.setCount(entry.getValue());
                    return cd;
                })
                .toList();
    }
}
