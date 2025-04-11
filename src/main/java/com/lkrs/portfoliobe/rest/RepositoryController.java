package com.lkrs.portfoliobe.rest;

import com.lkrs.portfoliobe.model.Repository;
import com.lkrs.portfoliobe.service.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RepositoryController {

    @Autowired
    private RepositoryService repositoryService;

    @GetMapping("/repository")
    public List<Repository> getAll() {


        return null;
    }

}
