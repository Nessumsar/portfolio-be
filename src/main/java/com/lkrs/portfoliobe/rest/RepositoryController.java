package com.lkrs.portfoliobe.rest;

import com.lkrs.portfoliobe.model.Repository;
import com.lkrs.portfoliobe.service.RepositoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RepositoryController {

    private RepositoryService repositoryService;

    public RepositoryController(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    @GetMapping("/repository")
    public ResponseEntity<?> getAll() {
        List<Repository> result = repositoryService.getRepositories();
        if (result.isEmpty()) {
            return new ResponseEntity<>("No repositories found.", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(result);
    }

}
