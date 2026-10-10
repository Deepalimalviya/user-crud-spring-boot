package com.deepali.usercrud.security;
public record AuthResponse(String tokenType, String accessToken, long expiresInSeconds) {}