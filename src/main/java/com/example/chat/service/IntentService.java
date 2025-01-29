package com.example.chat.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

import com.example.chat.exception.IntentServiceException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class IntentService {
    private static final Logger logger = LoggerFactory.getLogger(IntentService.class);

    @Value("${azure.luis.subscription-key}")
    private String subscriptionKey;

    @Value("${azure.luis.endpoint}")
    private String endpoint;

    @Value("${azure.luis.project-name}")
    private String projectName;

    @Value("${azure.luis.deployment-name}")
    private String deploymentName;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;



    public String detectIntent(String query) {
        try {
            String requestBody = createRequestBody(query);
            HttpRequest request = createHttpRequest(requestBody);

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return extractTopIntent(response.body());
        } catch (Exception e) {
            logger.error("Failed to detect intent for query: {}", query, e);
            throw new IntentServiceException("Failed to detect intent", e);
        }
    }

    private String createRequestBody(String query) {
        String participantId = UUID.randomUUID().toString();
        return String.format(
                "{\"kind\":\"Conversation\"," +
                "\"analysisInput\":{" +
                    "\"conversationItem\":{" +
                        "\"id\":\"%s\"," +
                        "\"text\":\"%s\"," +
                        "\"modality\":\"text\"," +
                        "\"language\":\"en\"," +
                        "\"participantId\":\"%s\"" +
                    "}" +
                "}," +
                "\"parameters\":{" +
                    "\"projectName\":\"%s\"," +
                    "\"verbose\":true," +
                    "\"deploymentName\":\"%s\"," +
                    "\"stringIndexType\":\"TextElement_V8\"" +
                "}}",
                participantId, query, participantId, projectName, deploymentName
        );
    }

    private HttpRequest createHttpRequest(String requestBody) {
        return HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Ocp-Apim-Subscription-Key", subscriptionKey)
                .header("Apim-Request-Id", UUID.randomUUID().toString())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
    }

    private String extractTopIntent(String responseBody) {
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            return rootNode
                    .path("result")
                    .path("prediction")
                    .path("topIntent")
                    .asText("None");
        } catch (Exception e) {
            logger.error("Failed to extract top intent from response: {}", responseBody, e);
            throw new IntentServiceException("Failed to extract top intent", e);
        }
    }
}

