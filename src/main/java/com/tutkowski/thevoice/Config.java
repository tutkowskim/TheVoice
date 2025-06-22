package com.tutkowski.thevoice;

public class Config {
    private final String token;

    public Config() {
        this.token = System.getenv("TOKEN");
    }

    public String getToken() {
        return token;
    }

    public int getPort() {
        String envString = System.getenv().getOrDefault("PORT", "4567");
        return Integer.parseInt(envString);
    }
}