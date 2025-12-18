package com.tutkowski.thevoice;

public class Config {
    public String getToken() {
        return System.getenv("TOKEN");
    }

    public String getOpenAIApiKey() {
        return System.getenv("OPENAI_API_KEY");
    }

    public String getOpenAIModel() {
        return System.getenv().getOrDefault("OPENAI_MODEL", "gpt-5.2");
    }

    public int getPort() {
        String envString = System.getenv().getOrDefault("PORT", "4567");
        return Integer.parseInt(envString);
    }
}
