package com.team_7.moment_film.global.dto;

public enum S3Prefix {
    PROFILE("profile/"),
    POST("post/"),
    FRAME("frame/");

    private final String directory;

    S3Prefix(String directory){
        this.directory = directory;
    }

    public String getDirectory() {
        return directory;
    }
}