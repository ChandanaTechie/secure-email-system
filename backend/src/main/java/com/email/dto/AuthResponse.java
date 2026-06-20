package com.email.dto;

public record AuthResponse(
        String token,
        UserResponse user
) {}
