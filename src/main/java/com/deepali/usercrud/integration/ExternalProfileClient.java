package com.deepali.usercrud.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ExternalProfileClient {
    private final RestClient client;
    public ExternalProfileClient(RestClient.Builder builder, @Value("${app.profile-service.base-url}") String baseUrl) {
        this.client = builder.baseUrl(baseUrl).build();
    }
    public ProfileSummary getProfile(Long userId) {
        return client.get().uri("/profiles/{id}", userId).retrieve().body(ProfileSummary.class);
    }
    public record ProfileSummary(Long userId, String tier, String source) {}
}