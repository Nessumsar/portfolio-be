package com.lkrs.portfoliobe.rest;

import com.lkrs.portfoliobe.model.CommitData;
import com.lkrs.portfoliobe.service.CommitService;
import com.lkrs.portfoliobe.service.RepositoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CommitController {
    private CommitService commitService;
    private RepositoryService repositoryService;

    public CommitController(CommitService commitService, RepositoryService repositoryService) {
        this.commitService = commitService;
        this.repositoryService = repositoryService;
    }

    @GetMapping("/commit")
    public ResponseEntity<?> getAll() {
        List<CommitData> result = commitService.getAllCommitData();
        if (result.isEmpty()) {
            return new ResponseEntity<>("No commits found.", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(result);
    }

}
