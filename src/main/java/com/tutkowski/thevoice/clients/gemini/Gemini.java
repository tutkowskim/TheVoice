package com.tutkowski.thevoice.clients.gemini;

import com.google.inject.Inject;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.tutkowski.thevoice.Config;

public class Gemini {
    private static final String baseUrl = "https://generativelanguage.googleapis.com/v1beta/openai";
    private static final String model = "gemini-2.5-pro";

    private final OpenAIClient client;

    @Inject
    public Gemini(Config config) {
        this.client = OpenAIOkHttpClient.builder()
                .baseUrl(Gemini.baseUrl)
                .apiKey(config.getGeminiApiKey())
                .maxRetries(5)
                .build();
    }

    public String prompt(String message) {
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .addUserMessage(message)
                .model(Gemini.model)
                .build();

        ChatCompletion response = this.client.chat()
                .completions()
                .create(params);

        return response.choices()
                .getFirst()
                .message()
                .content()
                .orElse("(no response)");
    }
}
