package com.lkrs.portfoliobe.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lkrs.portfoliobe.model.Platform;
import com.lkrs.portfoliobe.model.Repository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
public class RepositoryService {
    private final String GITHUB_URL = "https://api.github.com/users/Nessumsar/repos";
    private final String GITLAB_URL = "https://gitlab.com/api/v4/users/Nessumsar/projects";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Repository>  getRepositories() {
        List<Repository> githubRepos = getRepositoriesFromHost(Platform.GITHUB);
        List<Repository> gitlabRepos = getRepositoriesFromHost(Platform.GITLAB);

        List<Repository> allRepos = new ArrayList<>();
        allRepos.addAll(githubRepos);
        allRepos.addAll(gitlabRepos);

        return allRepos.stream().sorted(Comparator.comparing(Repository::getLastUpdated).reversed()).toList();
    }

    private List<Repository> getRepositoriesFromHost(Platform platform) {
        List<Repository> repositories;
        RestTemplate restTemplate = new RestTemplate();

        String url = platform.equals(Platform.GITHUB) ? GITHUB_URL : GITLAB_URL;
        ResponseEntity<String> apiResponse = restTemplate.getForEntity(url, String.class);
        if (apiResponse.getStatusCode() != HttpStatus.OK) {
            log.warn("ApiResponse code is not 200: {}", apiResponse.getStatusCode());
            return new ArrayList<>();
        }

        try {
            repositories = objectMapper.readValue(apiResponse.getBody(), new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            log.warn("JsonProcessingException: {}", e);
            return new ArrayList<>();
        }

        if (repositories.isEmpty()) {
            log.warn("Repository is empty");
        }
        repositories.forEach(repo -> repo.setPlatform(platform));
        return repositories;
    }

}
