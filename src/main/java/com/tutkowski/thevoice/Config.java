package com.tutkowski.thevoice;

public class Config {
    public String getToken() {
        return System.getenv("TOKEN");
    }

    public String getGeminiApiKey() {
        return System.getenv("GEMINI_API_KEY");
    }

    public int getPort() {
        String envString = System.getenv().getOrDefault("PORT", "4567");
        return Integer.parseInt(envString);
    }
}