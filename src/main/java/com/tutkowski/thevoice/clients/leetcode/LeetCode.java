package com.tutkowski.thevoice.clients.leetcode;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LeetCode {
    private static final Logger LOGGER = LoggerFactory.getLogger(LeetCode.class);
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    public LeetCodeQuestion getDailyQuestion() throws IOException, InterruptedException {
        String graphqlQuery = """
            {
              "query": "{ activeDailyCodingChallengeQuestion { date question { title titleSlug difficulty } } }"
            }
            """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://leetcode.com/graphql"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(graphqlQuery))
                .build();

        LOGGER.info("Requesting LeetCode daily question");
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        LOGGER.info("LeetCode response status {}", response.statusCode());
        JsonNode node = mapper.readTree(response.body());
        JsonNode question = node.at("/data/activeDailyCodingChallengeQuestion/question");

        String title = question.get("title").asText();
        String slug = question.get("titleSlug").asText();
        String difficulty = question.get("difficulty").asText();
        String url = "https://leetcode.com/problems/" + slug;

        return new LeetCodeQuestion(title, difficulty, url);
    }
}
