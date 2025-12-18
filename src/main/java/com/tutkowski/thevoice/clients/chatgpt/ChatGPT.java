package com.tutkowski.thevoice.clients.chatgpt;

import com.google.inject.Inject;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.tutkowski.thevoice.Config;

public class ChatGPT {
    private final String model;
    private final OpenAIClient client;

    @Inject
    public ChatGPT(Config config) {
        this.model = config.getOpenAIModel();
        this.client = OpenAIOkHttpClient.builder()
                .apiKey(config.getOpenAIApiKey())
                .maxRetries(5)
                .build();
    }

    public String prompt(String message) {
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .addUserMessage(message)
                .model(this.model)
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
