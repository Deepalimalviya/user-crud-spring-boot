package com.deepali.usercrud.security;

import com.deepali.usercrud.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager; private final UserDetailsService userDetailsService; private final JwtService jwtService;
    public AuthController(AuthenticationManager authenticationManager, UserDetailsService userDetailsService, JwtService jwtService) {
        this.authenticationManager = authenticationManager; this.userDetailsService = userDetailsService; this.jwtService = jwtService;
    }
    @PostMapping("/login") @Operation(summary = "Authenticate and receive JWT")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        var user = userDetailsService.loadUserByUsername(request.username());
        return ApiResponse.ok("Login successful", new AuthResponse("Bearer", jwtService.generate(user), jwtService.expirationSeconds()));
    }
}