package com.paperforge.dto;

public class InfoResponse {
    private String name;
    private String tagline;
    private String version;
    private String environment;

    public InfoResponse() {
    }

    public InfoResponse(String name, String tagline, String version, String environment) {
        this.name = name;
        this.tagline = tagline;
        this.version = version;
        this.environment = environment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }
}
