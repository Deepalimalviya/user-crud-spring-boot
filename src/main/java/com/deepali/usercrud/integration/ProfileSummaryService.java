package com.deepali.usercrud.integration;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

@Service
public class ProfileSummaryService {
    private final ExternalProfileClient client;
    public ProfileSummaryService(ExternalProfileClient client) { this.client = client; }
    @CircuitBreaker(name = "profileService", fallbackMethod = "fallback")
    public ExternalProfileClient.ProfileSummary getProfile(Long userId) { return client.getProfile(userId); }
    public ExternalProfileClient.ProfileSummary fallback(Long userId, Throwable ex) {
        return new ExternalProfileClient.ProfileSummary(userId, "TEMPORARILY_UNAVAILABLE", "fallback");
    }
}