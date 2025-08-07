package com.example.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    
    private String accessToken;
    private String tokenType;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private LocalDateTime expiresAt;
    
    public static LoginResponse success(String token, String email, String firstName, String lastName, String role, LocalDateTime expiresAt) {
        return LoginResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .role(role)
                .expiresAt(expiresAt)
                .build();
    }
}