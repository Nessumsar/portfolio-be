package com.lkrs.portfoliobe.service;

import com.lkrs.portfoliobe.model.Platform;
import com.lkrs.portfoliobe.model.Repository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RepositoryService {
    private final String GITHUB_URL = "https://api.github.com/users/Nessumsar/repos";
    private final String GITLAB_URL = "https://gitlab.com/api/v4/users/Nessumsar/projects";

    public List<Repository>  getRepositories() {
        List<Repository> githubRepos = getRepositoriesFromHost(Platform.GITHUB);
        List<Repository> gitlabRepos = getRepositoriesFromHost(Platform.GITLAB);

        List<Repository> allRepos = new ArrayList<>();
        allRepos.addAll(githubRepos);
        allRepos.addAll(gitlabRepos);

        List<Repository> sortedRepos = allRepos.stream()
                .sorted(Comparator.comparing(Repository::getLastUpdated).reversed())
                .limit(5)
                .collect(Collectors.toList());

        return sortedRepos;
    }

    private List<Repository> getRepositoriesFromHost(Platform platform) {
        String url = platform.equals(Platform.GITHUB) ? GITHUB_URL : GITLAB_URL;

        return null;
    }


}
