package com.lkrs.portfoliobe.model;

import lombok.Data;

import java.util.Date;

@Data
public class Repository {
    int id;
    String name;
    String url;
    Date lastUpdated;
    String description;
    Platform platform;
}
