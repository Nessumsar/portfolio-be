package com.lkrs.portfoliobe.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Repository {
    private int id;
    private String name;
    @JsonAlias({"html_url", "web_url"})
    private String htmlUrl;
    @JsonAlias({"updated_at", "last_activity_at"})
    private Date lastUpdated;
    private String description;
    private String language;
    private Platform platform;
}
