package com.deepali.usercrud.integration;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfileSummaryServiceTest {
    @Test void fallbackProvidesSafeDegradedResponse() {
        ExternalProfileClient client = mock(ExternalProfileClient.class);
        ProfileSummaryService service = new ProfileSummaryService(client);
        var response = service.fallback(42L, new RuntimeException("service down"));
        assertEquals(42L, response.userId());
        assertEquals("TEMPORARILY_UNAVAILABLE", response.tier());
        assertEquals("fallback", response.source());
    }
}