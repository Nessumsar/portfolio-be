package com.lkrs.portfoliobe.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Repository {
    private final int id;
    private final String name;
    @JsonAlias({"html_url", "web_url"})
    private final String htmlUrl;
    @JsonAlias({"updated_at", "last_activity_at"})
    private final ZonedDateTime lastUpdated;
    private final String description;
    private final String language;
    private Platform platform;
}
