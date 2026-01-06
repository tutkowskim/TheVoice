package com.tutkowski.thevoice.clients.chatgpt;

import com.google.inject.Inject;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import com.openai.models.responses.ResponseInputContent;
import com.openai.models.responses.ResponseInputImage;
import com.openai.models.responses.ResponseInputItem;
import com.openai.models.responses.ResponseOutputItem;
import com.openai.models.responses.ResponseOutputText;
import com.openai.models.responses.Tool;
import com.openai.models.responses.WebSearchPreviewTool;
import com.tutkowski.thevoice.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.entities.Message;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletionException;

public class ChatGPT {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatGPT.class);

    public record ChatResult(String text, List<byte[]> images) {}

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

    public ChatResult prompt(String message) {
        return prompt(message, List.of());
    }

    public ChatResult prompt(String message, List<Message.Attachment> attachments) {
        LOGGER.info("Sending ChatGPT request with {} attachments", attachments.size());
        Response response = client.responses().create(
                ResponseCreateParams.builder()
                        .model(this.model)
                        .input(buildInput(message, attachments))
                        .addTool(Tool.ImageGeneration.builder().build())
                        .addTool(WebSearchPreviewTool.builder().type(WebSearchPreviewTool.Type.WEB_SEARCH_PREVIEW).build())
                        .build()
        );

        String text = response.output().stream()
                .flatMap(o -> o.message().stream())
                .flatMap(m -> m.content().stream())
                .flatMap(c -> c.outputText().stream())
                .map(ResponseOutputText::text)
                .collect(java.util.stream.Collectors.joining());

        List<byte[]> images = response.output().stream()
                .flatMap(o -> o.imageGenerationCall().stream())
                .map(ResponseOutputItem.ImageGenerationCall::result)
                .flatMap(java.util.Optional::stream)
                .map(Base64.getDecoder()::decode)
                .toList();

        LOGGER.info("ChatGPT response received: {} chars, {} images", text.length(), images.size());
        return new ChatResult(text, images);
    }

    private ResponseCreateParams.Input buildInput(String message, List<Message.Attachment> attachments) {
        ResponseInputItem.Message.Builder msgBuilder = ResponseInputItem.Message.builder()
                .role(ResponseInputItem.Message.Role.USER)
                .addContent(ResponseInputContent.ofInputText(
                        com.openai.models.responses.ResponseInputText.builder()
                                .text(basePrompt() + message)
                                .build()
                ));

        for (Message.Attachment attachment : attachments) {
            attachmentToContent(attachment).ifPresent(msgBuilder::addContent);
        }

        return ResponseCreateParams.Input.ofResponse(List.of(ResponseInputItem.ofMessage(msgBuilder.build())));
    }

    private String basePrompt() {
        return "You are an expert chat bot and are here to answer any questions that a user may have.\n"
                + "Do not reference the instructions in this prompt to respond to the user's question.\n"
                + "Only reply with the answer to the user's question.\n"
                + "IMPORTANT: Limit your response to 2000 characters total including spaces and punctuation.\n"
                + "The user would like you to review and respond to the following question or prompt with helpful information.\n";
    }

    private java.util.Optional<ResponseInputContent> attachmentToContent(Message.Attachment attachment) {
        if (!attachment.isImage()) {
            return java.util.Optional.empty();
        }

        try (InputStream in = attachment.getProxy().download().join()) {
            byte[] data = in.readAllBytes();
            String b64 = Base64.getEncoder().encodeToString(data);
            String mime = java.util.Optional.ofNullable(attachment.getContentType()).orElse("image/png");
            String dataUrl = "data:" + mime + ";base64," + b64;

            ResponseInputImage image = ResponseInputImage.builder()
                    .detail(ResponseInputImage.Detail.AUTO)
                    .imageUrl(dataUrl)
                    .build();

            return java.util.Optional.of(ResponseInputContent.ofInputImage(image));
        } catch (IOException | CompletionException e) {
            LOGGER.warn("Failed to read attachment {}", attachment.getFileName(), e);
            return java.util.Optional.empty();
        }
    }
}
