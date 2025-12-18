package com.tutkowski.thevoice.clients.chatgpt;

import com.google.inject.Inject;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import com.openai.models.responses.ResponseOutputText;
import com.openai.models.responses.WebSearchPreviewTool;
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
        ResponseCreateParams params =
                ResponseCreateParams.builder()
                        .model(this.model)
                        .input(message)
                        .addTool(WebSearchPreviewTool.builder().type(WebSearchPreviewTool.Type.WEB_SEARCH_PREVIEW).build())
                        .build();

        Response response = client.responses().create(params);
        return response.output().stream()
                .flatMap(o -> o.message().stream())
                .flatMap(m -> m.content().stream())
                .flatMap(c -> c.outputText().stream())
                .map(ResponseOutputText::text)
                .collect(java.util.stream.Collectors.joining());
    }
}
