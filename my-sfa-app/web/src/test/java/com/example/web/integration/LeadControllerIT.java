package com.example.web.integration;

import com.example.core.domain.Lead;
import com.example.core.domain.User;
import com.example.infra.repository.LeadRepository;
import com.example.infra.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@Testcontainers
@ActiveProfiles("test")
@Transactional
class LeadControllerIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LeadRepository leadRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("test@example.com")
                .password("encoded_password")
                .firstName("Test")
                .lastName("User")
                .role(User.Role.SALES_REP)
                .active(true)
                .build();
        testUser = userRepository.save(testUser);
    }

    @Test
    @WithMockUser(roles = "SALES_REP")
    void shouldCreateLead() throws Exception {
        Lead lead = Lead.builder()
                .companyName("Test Company")
                .contactName("John Doe")
                .email("john@testcompany.com")
                .phone("+1-555-0123")
                .status(Lead.Status.NEW)
                .source(Lead.Source.WEBSITE)
                .owner(testUser)
                .build();

        mockMvc.perform(post("/api/v1/leads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lead)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.companyName").value("Test Company"))
                .andExpect(jsonPath("$.contactName").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@testcompany.com"));
    }

    @Test
    @WithMockUser(roles = "SALES_REP")
    void shouldGetAllLeads() throws Exception {
        Lead lead = Lead.builder()
                .companyName("Test Company")
                .contactName("John Doe")
                .email("john@testcompany.com")
                .status(Lead.Status.NEW)
                .owner(testUser)
                .build();
        leadRepository.save(lead);

        mockMvc.perform(get("/api/v1/leads"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].companyName").value("Test Company"));
    }

    @Test
    @WithMockUser(roles = "SALES_REP")
    void shouldGetLeadById() throws Exception {
        Lead lead = Lead.builder()
                .companyName("Test Company")
                .contactName("John Doe")
                .email("john@testcompany.com")
                .status(Lead.Status.NEW)
                .owner(testUser)
                .build();
        Lead savedLead = leadRepository.save(lead);

        mockMvc.perform(get("/api/v1/leads/" + savedLead.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedLead.getId()))
                .andExpect(jsonPath("$.companyName").value("Test Company"));
    }

    @Test
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/leads"))
                .andExpect(status().isUnauthorized());
    }
}