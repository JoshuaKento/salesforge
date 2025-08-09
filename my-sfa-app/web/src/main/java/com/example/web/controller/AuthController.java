package com.example.web.controller;

import com.example.core.domain.User;
import com.example.security.service.AuthService;
import com.example.web.dto.LoginRequest;
import com.example.web.dto.LoginResponse;
import com.example.web.dto.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication operations for user management")
public class AuthController {

    private final AuthService authService;

    /**
     * User registration endpoint
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new user", 
               description = "Creates a new user account with email, password and basic information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User successfully registered"),
        @ApiResponse(responseCode = "400", description = "Invalid input or email already exists"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Registration attempt for email: {}", request.getEmail());
        
        try {
            // Validate password confirmation
            authService.validatePassword(request.getPassword(), request.getConfirmPassword());
            
            // Register user
            User user = authService.registerUser(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPassword()
            );
            
            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("userId", user.getId());
            response.put("email", user.getEmail());
            
            log.info("Successfully registered user: {}", user.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            log.error("Registration failed for email {}: {}", request.getEmail(), e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            log.error("Unexpected error during registration for email {}: {}", request.getEmail(), e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Registration failed. Please try again.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * User login endpoint
     */
    @PostMapping("/login")
    @Operation(summary = "User login", 
               description = "Authenticates user credentials and returns JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful, JWT token returned"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials"),
        @ApiResponse(responseCode = "400", description = "Invalid input format"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());
        
        try {
            // Authenticate and get token
            String token = authService.authenticateUser(request.getEmail(), request.getPassword());
            
            // Get user details
            User user = authService.findUserByEmail(request.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            
            // Get token expiration
            LocalDateTime expiresAt = authService.getTokenExpiration();
            
            // Build response
            LoginResponse response = LoginResponse.success(
                token,
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().toString(),
                expiresAt
            );
            
            log.info("Successfully authenticated user: {}", request.getEmail());
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.error("Login failed for email {}: {}", request.getEmail(), e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (Exception e) {
            log.error("Unexpected error during login for email {}: {}", request.getEmail(), e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Login failed. Please try again.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Logout endpoint
     */
    @PostMapping("/logout")
    @Operation(summary = "User logout", 
               description = "Clears user session and invalidates current authentication context")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Logout successful")
    })
    public ResponseEntity<?> logout() {
        SecurityContextHolder.clearContext();
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logged out successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Health check for auth service
     */
    @GetMapping("/health")
    @Operation(summary = "Authentication service health check", 
               description = "Returns the current status of the authentication service")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Service is healthy")
    })
    public ResponseEntity<?> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Authentication Service");
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    /**
     * Test login endpoint for debugging
     */
    @PostMapping("/test-login")
    public ResponseEntity<?> testLogin(@RequestBody LoginRequest request) {
        log.info("Test login attempt for email: {}", request.getEmail());
        
        try {
            // Check if user exists
            User user = authService.findUserByEmail(request.getEmail())
                    .orElse(null);
            
            Map<String, Object> response = new HashMap<>();
            if (user != null) {
                response.put("userFound", true);
                response.put("email", user.getEmail());
                response.put("active", user.getActive());
                response.put("role", user.getRole());
                response.put("hasPassword", user.getPassword() != null);
            } else {
                response.put("userFound", false);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}