package com.lkrs.portfoliobe.model;

import lombok.Data;

@Data
public class CommitData {
    int repositoryId;
    String date;
    int count;
    Platform platform;
}
