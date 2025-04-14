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
import java.util.List;

@Service
@Slf4j
public class RepositoryService {
    private final String GITHUB_URL = "https://api.github.com/users/Nessumsar/repos";
    private final String GITLAB_URL = "https://gitlab.com/api/v4/users/Nessumsar/projects";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Repository>  getRepositories() throws JsonProcessingException {
        List<Repository> githubRepos = getRepositoriesFromHost(Platform.GITHUB);
        List<Repository> gitlabRepos = getRepositoriesFromHost(Platform.GITLAB);

        List<Repository> allRepos = new ArrayList<>();
        allRepos.addAll(githubRepos);
        allRepos.addAll(gitlabRepos);

        //Sort by date
        return allRepos;
    }

    private List<Repository> getRepositoriesFromHost(Platform platform) throws JsonProcessingException {
        String url = platform.equals(Platform.GITHUB) ? GITHUB_URL : GITLAB_URL;

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> apiReponse = restTemplate.getForEntity(url, String.class);
        if (apiReponse.getStatusCode() != HttpStatus.OK) {
            //Throw something
            log.warn("ApiResponse code is not 200: {}", apiReponse.getStatusCode());
            return null;
        }

        List<Repository> repositories = objectMapper.readValue(apiReponse.getBody(), new TypeReference<List<Repository>>(){});
        if (repositories.isEmpty()) {
            //Throw something
            log.warn("Repository is empty");
        }

        repositories.forEach(repo -> repo.setPlatform(platform));

        log.warn("Repo 1: {}",repositories.getFirst().toString());
        return repositories;
    }


}
