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

    private final ObjectMapper objectMapper;
    private RestTemplate restTemplate;

    public RepositoryService(ObjectMapper objectMapper, RestTemplate restTemplate) {
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
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
        String url = platform.equals(Platform.GITHUB) ? GITHUB_URL : GITLAB_URL;
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            log.warn("Response code is not 200: {}", response.getStatusCode());
            return new ArrayList<>();
        }

        List<Repository> repositories;
        try {
            repositories = objectMapper.readValue(response.getBody(), new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            log.warn("JsonProcessingException: ", e);
            return new ArrayList<>();
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
}
