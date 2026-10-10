package com.deepali.usercrud.user;

import java.time.Instant;

public record UserResponse(Long id, String name, String email, String phone,
                           Instant createdAt, Instant updatedAt) {
    public static UserResponse from(User u) {
        return new UserResponse(u.getId(), u.getName(), u.getEmail(), u.getPhone(), u.getCreatedAt(), u.getUpdatedAt());
    }
}