package com.lkrs.portfoliobe.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lkrs.portfoliobe.model.Platform;
import com.lkrs.portfoliobe.model.Repository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
public class RepositoryService {

    @Value("${github.repo.url}")
    private String GITHUB_URL;
    @Value("${gitlab.repo.url}")
    private String GITLAB_URL;
    @Value("${github.access.token}")
    private String GITHUB_TOKEN;
    @Value("${gitlab.access.token}")
    private String GITLAB_TOKEN;

    private final ObjectMapper objectMapper;

    public RepositoryService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<Repository> getLatestSixRepositories() {
        List<Repository> allRepos = getAllRepositories();
        return filterForLatestSixRepositories(allRepos);
    }

    public List<Repository> getRepositoriesUpdatedWithinOneYear() {
        List<Repository> allRepos = getAllRepositories();
        return filterForUpdatedWithinOneYear(allRepos);
    }


    public List<Repository>  getAllRepositories() {
        List<Repository> githubRepos = getRepositoriesFromHost(Platform.GITHUB);
        List<Repository> gitlabRepos = getRepositoriesFromHost(Platform.GITLAB);

        List<Repository> allRepos = new ArrayList<>();
        allRepos.addAll(githubRepos);
        allRepos.addAll(gitlabRepos);

        for (Repository repo : allRepos) {
            log.info("Repo {}", repo);
        }

        return allRepos;
    }

    private List<Repository> getRepositoriesFromHost(Platform platform) {
        List<Repository> repositories;
        RestTemplate restTemplate = new RestTemplate();

        String url = platform.equals(Platform.GITHUB) ? GITHUB_URL : GITLAB_URL;
        HttpEntity<String> request = new HttpEntity<>(createHeaders(platform));
        ResponseEntity<String> apiResponse = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
        if (apiResponse.getStatusCode() != HttpStatus.OK) {
            log.warn("ApiResponse code is not 200: {}", apiResponse.getStatusCode());
            return new ArrayList<>();
        }

        try {
            repositories = objectMapper.readValue(apiResponse.getBody(), new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            log.warn("JsonProcessingException: ", e);
            return new ArrayList<>();
        }

        if (repositories.isEmpty()) {
            log.warn("Repository is empty");
        }
        repositories.forEach(repo -> repo.setPlatform(platform));
        return repositories;
    }

    private List<Repository> filterForLatestSixRepositories(List<Repository> repositories) {
        return repositories.stream()
                .sorted(Comparator.comparing(Repository::getLastUpdated).reversed())
                .limit(6)
                .toList();
    }

    private List<Repository> filterForUpdatedWithinOneYear(List<Repository> repositories) {
        ZonedDateTime oneYearAgo = ZonedDateTime.now().minusDays(365);
        return repositories.stream()
                .sorted(Comparator.comparing(Repository::getLastUpdated).reversed())
                .filter(repository -> repository.getLastUpdated().isAfter(oneYearAgo))
                .toList();
    }

    private HttpHeaders createHeaders(Platform platform) {
        if (platform.equals(Platform.GITHUB)) {
            return new HttpHeaders() {{
                set("Content-Type", "application/json");
                set("Authorization", "Bearer "+GITHUB_TOKEN);
            }};
        } else if (platform.equals(Platform.GITLAB)) {
            return new HttpHeaders() {{
                set("Content-Type", "application/json");
                set("Authorization", "Bearer "+GITLAB_TOKEN);
            }};
        } else return new HttpHeaders();
    }
}
