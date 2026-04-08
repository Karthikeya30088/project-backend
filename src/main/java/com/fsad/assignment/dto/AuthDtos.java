package com.fsad.assignment.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AuthDtos {

    public record LoginRequest(
            @Email @NotBlank String email,
            @NotBlank String password
    ) {
    }

    public record UserResponse(
            String id,
            String role,
            String name,
            String email,
            String gradeLevel
    ) {
    }

    public record AuthResponse(UserResponse user) {
    }
}
