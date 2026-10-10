package com.deepali.usercrud.integration;

import com.deepali.usercrud.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/users")
public class ProfileController {
    private final ProfileSummaryService service;
    public ProfileController(ProfileSummaryService service) { this.service = service; }
    @GetMapping("/{id}/profile-summary")
    @Operation(summary = "Get external profile summary (Circuit Breaker + fallback)")
    public ApiResponse<ExternalProfileClient.ProfileSummary> profile(@PathVariable Long id) {
        return ApiResponse.ok("Profile summary retrieved (may be fallback data)", service.getProfile(id));
    }
}