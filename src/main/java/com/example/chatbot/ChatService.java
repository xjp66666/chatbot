package com.example.chatbot;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ChatService {

    private final RestClient client;
    private final String endpoint;
    private final String model;
    private final String systemPrompt;

    public ChatService(
            @Value("${ai.endpoint}") String endpoint,
            @Value("${ai.api-key}") String apiKey,
            @Value("${ai.model}") String model,
            @Value("${ai.system-prompt}") String systemPrompt) {

        this.endpoint = endpoint;
        this.model = model;
        this.systemPrompt = systemPrompt;

        this.client = RestClient.builder().defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey).build();

    }

    public String getReply(String userMessage) {
        ProviderRequest request = new ProviderRequest(
                model,
                List.of(new ProviderMessage("system", systemPrompt),
                        new ProviderMessage("user", userMessage)));

        ProviderResponse response = client.post()
                .uri(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(ProviderResponse.class);

        if (response == null || response.choices() == null || response.choices().isEmpty()) {
            throw new IllegalStateException("The AI provider returned no response");
        }

        return response.choices().get(0).message().content();
    }

    private record ProviderRequest(
            String model,
            List<ProviderMessage> messages) {
    }

    private record ProviderMessage(
            String role,
            String content) {
    }

    private record ProviderResponse(
            List<Choice> choices) {
    }

    private record Choice(
            ProviderMessage message) {
    }
}