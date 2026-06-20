package com.chandana.email.dto;

public record AuthResponse(
        String token,
        UserResponse user
) {}
