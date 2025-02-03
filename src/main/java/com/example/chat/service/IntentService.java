package com.example.chat.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.chat.exception.IntentServiceException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import lombok.RequiredArgsConstructor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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

    @Value("${azure.cognitive.endpoint}")
    private String cognitiveEndpoint;

    @Value("${azure.cognitive.key}")
    private String cognitiveKey;

    @Value("${azure.cognitive.translate.endpoint}")
    private String translateEndpoint;

    @Value("${azure.cognitive.translate.key}")
    private String translateKey;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public String detectIntent(String query) {
        try {
            String requestBody = createRequestBody(query);
            HttpRequest request = createHttpRequest(requestBody);

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response: " + response.body());
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
            String result=rootNode
                    .path("result")
                    .path("prediction")
                    .path("topIntent")
                    .asText("None");
            System.out.println("Top intent: " + result);
            return result;
        } catch (Exception e) {
            logger.error("Failed to extract top intent from response: {}", responseBody, e);
            throw new IntentServiceException("Failed to extract top intent", e);
        }
    }

    public String detectLanguage(String text) {
        try {
            String jsonRequest = String.format("{ \"documents\": [{ \"id\": \"1\", \"text\": \"%s\" }] }", text);
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(cognitiveEndpoint + "/text/analytics/v3.0/languages"))
                .header("Ocp-Apim-Subscription-Key", cognitiveKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());
                
            String responseBody = response.body();
            System.out.println("API Yanıtı: " + responseBody);

            if (responseBody != null && responseBody.startsWith("{")) {
                JSONObject jsonResponse = new JSONObject(responseBody);
                return jsonResponse.getJSONArray("documents")
                    .getJSONObject(0)
                    .getJSONObject("detectedLanguage")
                    .getString("iso6391Name");
            } else {
                System.err.println("Geçersiz JSON yanıt: " + responseBody);
                return "tr"; // Varsayılan olarak Türkçe
            }
        } catch (Exception e) {
            logger.error("Dil algılama hatası", e);
            return "tr"; // Varsayılan olarak Türkçe
        }
    }
    
    public String translateToEnglish2(String text, String sourceLang) {
        try {
           
            String jsonRequest = String.format("[{\"Text\": \"%s\"}]", text);
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(translateEndpoint))
                .header("Ocp-Apim-Subscription-Key", translateKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();


            HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());
                
            JSONObject jsonResponse = new JSONObject(response.body());
            return jsonResponse.getJSONArray("translations")
                .getJSONObject(0)
                .getString("text");
        } catch (Exception e) {
            logger.error("Çeviri hatası", e);
            return text;
        }
    }
    
    public String translateToEnglish(String text, String targetLang) {
        try {
            // Azure Cognitive Services Translator API uç noktası
            String endpoint = "https://api.cognitive.microsofttranslator.com/translate?api-version=3.0&to=" + targetLang;
            
            // JSON isteği oluştur
            String jsonRequest = String.format("[{\"Text\": \"%s\"}]", text);
            
            // OkHttpClient oluştur
            OkHttpClient client = new OkHttpClient();
            
            // MediaType ve RequestBody oluştur
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, jsonRequest);
            
            // Request oluştur
            Request request = new Request.Builder()
                .url(endpoint)
                .post(body)
                .addHeader("Ocp-Apim-Subscription-Key", translateKey) // Gizli anahtar
                .addHeader("Ocp-Apim-Subscription-Region", "westus2") // Bölge bilgisi
                .addHeader("Content-Type", "application/json")
                .build();
            
            // İsteği gönder ve yanıtı al
            Response response = client.newCall(request).execute();
            
            // Yanıtı kontrol et
            if (!response.isSuccessful()) {
                System.err.println("Hata: " + response.code() + " - " + response.message());
                return text; // Hata durumunda orijinal metni döndür
            }

            String responseBody = response.body().string();
            System.out.println("API Yanıtı: " + responseBody); // Yanıtı kontrol et

            if (responseBody != null && responseBody.startsWith("[")) {
                JsonParser parser = new JsonParser();
                JsonArray jsonResponse = parser.parse(responseBody).getAsJsonArray();
                return jsonResponse.get(0)
                    .getAsJsonObject()
                    .getAsJsonArray("translations")
                    .get(0)
                    .getAsJsonObject()
                    .get("text")
                    .getAsString();
            } else {
                System.err.println("Geçersiz JSON yanıt: " + responseBody);
                return text; // Hata durumunda orijinal metni döndür
            }
        } catch (IOException e) {
            System.err.println("Çeviri hatası: " + e.getMessage());
            return text; // Hata durumunda orijinal metni döndür
        }
    }
}
        
