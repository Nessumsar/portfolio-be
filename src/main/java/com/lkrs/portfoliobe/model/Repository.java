package com.lkrs.portfoliobe.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Repository {
    int id;
    String name;
    @JsonAlias({"html_url", "web_url"})
    String url;
    @JsonAlias({"updated_at", "last_activity_at"})
    Date lastUpdated;
    String description;
    String language;
    Platform platform;
}
