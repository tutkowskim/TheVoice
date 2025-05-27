package com.tutkowski.thevoice.bot;

public class BotConfig {
    private final String token;

    public BotConfig() {
        this.token = System.getenv("TOKEN");
    }

    public String getToken() {
        return token;
    }
}