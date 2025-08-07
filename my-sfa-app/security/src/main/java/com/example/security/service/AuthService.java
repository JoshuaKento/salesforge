package com.example.security.service;

import com.example.core.domain.User;
import com.example.infra.repository.UserRepository;
import com.example.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Register a new user
     */
    public User registerUser(String firstName, String lastName, String email, String password) {
        log.info("Registering new user with email: {}", email);
        
        // Check if user already exists
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("User with email " + email + " already exists");
        }
        
        // Create new user
        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(User.Role.SALES_REP) // Default role
                .active(true)
                .build();
        
        User savedUser = userRepository.save(user);
        log.info("Successfully registered user with ID: {}", savedUser.getId());
        
        return savedUser;
    }

    /**
     * Authenticate user and generate JWT token
     */
    public String authenticateUser(String email, String password) {
        log.info("Authenticating user with email: {}", email);
        
        try {
            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
            );
            
            // Find user details
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            
            if (!user.getActive()) {
                throw new IllegalArgumentException("User account is deactivated");
            }
            
            // Generate JWT token
            String token = jwtTokenProvider.generateToken(authentication);
            log.info("Successfully authenticated user: {}", email);
            
            return token;
            
        } catch (AuthenticationException e) {
            log.error("Authentication failed for user: {} - {}", email, e.getMessage());
            throw new IllegalArgumentException("Invalid email or password");
        }
    }

    /**
     * Get user by email
     */
    @Transactional(readOnly = true)
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Get token expiration time
     */
    public LocalDateTime getTokenExpiration() {
        return LocalDateTime.now().plusSeconds(jwtTokenProvider.getJwtExpiration() / 1000);
    }

    /**
     * Validate password strength
     */
    public void validatePassword(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        
        if (password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        
        // Add more password validation rules as needed
        if (!password.matches(".*[A-Za-z].*")) {
            throw new IllegalArgumentException("Password must contain at least one letter");
        }
    }
}