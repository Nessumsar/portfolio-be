package com.lkrs.portfoliobe.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lkrs.portfoliobe.model.Repository;
import com.lkrs.portfoliobe.service.RepositoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RepositoryController {

    private RepositoryService repositoryService;

    public RepositoryController(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    @GetMapping("/repository")
    public List<Repository> getAll() throws JsonProcessingException {


        return repositoryService.getRepositories();
    }

}
