package com.lkrs.portfoliobe.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommitData {
    private final int repositoryId;
    private final LocalDate date;
    private final Platform platform;
    private int count;

    public CommitData(int repositoryId, LocalDate date, Platform platform) {
        this.repositoryId = repositoryId;
        this.date = date;
        this.platform = platform;
        this.count = 1;
    }

}
