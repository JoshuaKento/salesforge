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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
    private Lead testLead;

    @BeforeEach
    void setUp() {
        // Clean up existing data
        leadRepository.deleteAll();
        userRepository.deleteAll();
        
        testUser = User.builder()
                .email("test@example.com")
                .password("encoded_password")
                .firstName("Test")
                .lastName("User")
                .role(User.Role.SALES_REP)
                .active(true)
                .build();
        testUser = userRepository.save(testUser);
        
        testLead = Lead.builder()
                .companyName("Test Company")
                .contactName("John Doe")
                .email("john@testcompany.com")
                .phone("+1-555-0123")
                .status(Lead.Status.NEW)
                .source(Lead.Source.WEBSITE)
                .owner(testUser)
                .build();
        testLead = leadRepository.save(testLead);
    }

    @Test
    @WithMockUser(roles = "SALES_REP")
    void shouldCreateLead() throws Exception {
        Lead lead = Lead.builder()
                .companyName("New Test Company")
                .contactName("Jane Smith")
                .email("jane@newtestcompany.com")
                .phone("+1-555-0456")
                .status(Lead.Status.NEW)
                .source(Lead.Source.REFERRAL)
                .owner(testUser)
                .build();

        mockMvc.perform(post("/api/v1/leads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lead)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.companyName").value("New Test Company"))
                .andExpect(jsonPath("$.contactName").value("Jane Smith"))
                .andExpect(jsonPath("$.email").value("jane@newtestcompany.com"))
                .andExpect(jsonPath("$.status").value("NEW"))
                .andExpect(jsonPath("$.source").value("REFERRAL"));
    }

    @Test
    @WithMockUser(roles = "SALES_REP")
    void shouldValidateRequiredFieldsWhenCreating() throws Exception {
        Lead invalidLead = Lead.builder()
                .email("invalid@test.com")
                .build();

        mockMvc.perform(post("/api/v1/leads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidLead)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(containsString("Company name is required")));
    }

    @Test
    @WithMockUser(roles = "SALES_REP")
    void shouldGetAllLeads() throws Exception {
        mockMvc.perform(get("/api/v1/leads"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].companyName").value("Test Company"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @WithMockUser(roles = "SALES_REP")
    void shouldGetLeadById() throws Exception {
        mockMvc.perform(get("/api/v1/leads/" + testLead.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testLead.getId()))
                .andExpect(jsonPath("$.companyName").value("Test Company"))
                .andExpect(jsonPath("$.contactName").value("John Doe"));
    }

    @Test
    @WithMockUser(roles = "SALES_REP")
    void shouldReturn404WhenLeadNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/leads/99999"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "SALES_REP")
    void shouldUpdateLead() throws Exception {
        testLead.setStatus(Lead.Status.QUALIFIED);
        testLead.setContactName("John Updated Doe");

        mockMvc.perform(put("/api/v1/leads/" + testLead.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testLead)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("QUALIFIED"))
                .andExpect(jsonPath("$.contactName").value("John Updated Doe"));
    }

    @Test
    @WithMockUser(roles = "SALES_REP")
    void shouldPartiallyUpdateLead() throws Exception {
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "CONTACTED");
        updates.put("phone", "+1-555-9999");

        mockMvc.perform(patch("/api/v1/leads/" + testLead.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONTACTED"))
                .andExpect(jsonPath("$.phone").value("+1-555-9999"))
                .andExpect(jsonPath("$.companyName").value("Test Company")); // unchanged
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void shouldDeleteLead() throws Exception {
        mockMvc.perform(delete("/api/v1/leads/" + testLead.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/api/v1/leads/" + testLead.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "SALES_REP")
    void shouldDenyDeleteForSalesRep() throws Exception {
        mockMvc.perform(delete("/api/v1/leads/" + testLead.getId()))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "SALES_REP")
    void shouldFilterLeadsByStatus() throws Exception {
        // Create additional test lead with different status
        Lead qualifiedLead = Lead.builder()
                .companyName("Qualified Company")
                .contactName("Qualified Contact")
                .email("qualified@test.com")
                .status(Lead.Status.QUALIFIED)
                .source(Lead.Source.EMAIL)
                .owner(testUser)
                .build();
        leadRepository.save(qualifiedLead);

        mockMvc.perform(get("/api/v1/leads")
                        .param("status", "QUALIFIED")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].status").value("QUALIFIED"));
    }

    @Test
    @WithMockUser(roles = "SALES_REP")
    void shouldFilterLeadsBySource() throws Exception {
        mockMvc.perform(get("/api/v1/leads")
                        .param("source", "WEBSITE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].source").value("WEBSITE"));
    }

    @Test
    @WithMockUser(roles = "SALES_REP")
    void shouldSearchLeads() throws Exception {
        mockMvc.perform(get("/api/v1/leads/search")
                        .param("q", "Test Company")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.searchTerm").value("Test Company"))
                .andExpect(jsonPath("$.content[0].companyName").value("Test Company"));
    }

    @Test
    @WithMockUser(roles = "SALES_REP")
    void shouldRequireSearchTermForSearch() throws Exception {
        mockMvc.perform(get("/api/v1/leads/search")
                        .param("q", "")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(containsString("Search term 'q' is required")));
    }

    @Test
    @WithMockUser(roles = "SALES_REP")
    void shouldGetLeadsBySource() throws Exception {
        mockMvc.perform(get("/api/v1/leads/source/WEBSITE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].source").value("WEBSITE"));
    }

    @Test
    @WithMockUser(roles = "SALES_REP")
    void shouldGetLeadStatistics() throws Exception {
        mockMvc.perform(get("/api/v1/leads/stats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalLeads").value(1))
                .andExpect(jsonPath("$.statusBreakdown").exists())
                .andExpect(jsonPath("$.sourceBreakdown").exists())
                .andExpect(jsonPath("$.recentLeads").exists())
                .andExpect(jsonPath("$.generatedAt").exists());
    }

    @Test
    @WithMockUser(roles = "SALES_REP")
    void shouldGetLeadCounts() throws Exception {
        mockMvc.perform(get("/api/v1/leads/count")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.NEW").value(1))
                .andExpect(jsonPath("$.CONTACTED").value(0))
                .andExpect(jsonPath("$.QUALIFIED").value(0))
                .andExpect(jsonPath("$.LOST").value(0))
                .andExpect(jsonPath("$.CONVERTED").value(0));
    }

    @Test
    @WithMockUser(roles = "SALES_REP")
    void shouldValidateDateFormat() throws Exception {
        mockMvc.perform(get("/api/v1/leads")
                        .param("startDate", "invalid-date")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(containsString("Invalid start date format")));
    }

    @Test
    @WithMockUser(roles = "SALES_REP")
    void shouldValidateDateRange() throws Exception {
        String startDate = LocalDateTime.now().toString();
        String endDate = LocalDateTime.now().minusDays(1).toString();

        mockMvc.perform(get("/api/v1/leads")
                        .param("startDate", startDate)
                        .param("endDate", endDate)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(containsString("Start date cannot be after end date")));
    }

    @Test
    @WithMockUser(roles = "SALES_REP")
    void shouldFilterLeadsByDateRange() throws Exception {
        String startDate = LocalDateTime.now().minusDays(1).toString();
        String endDate = LocalDateTime.now().plusDays(1).toString();

        mockMvc.perform(get("/api/v1/leads")
                        .param("startDate", startDate)
                        .param("endDate", endDate)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    @WithMockUser(roles = "SALES_REP")
    void shouldCombineMultipleFilters() throws Exception {
        mockMvc.perform(get("/api/v1/leads")
                        .param("status", "NEW")
                        .param("source", "WEBSITE")
                        .param("search", "Test")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].companyName").value("Test Company"));
    }

    @Test
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/leads"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenInsufficientPermissions() throws Exception {
        mockMvc.perform(get("/api/v1/leads")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "SALES_REP")
    void shouldHandlePaginationCorrectly() throws Exception {
        // Create additional leads for pagination testing
        for (int i = 0; i < 5; i++) {
            Lead lead = Lead.builder()
                    .companyName("Company " + i)
                    .contactName("Contact " + i)
                    .email("contact" + i + "@test.com")
                    .status(Lead.Status.NEW)
                    .source(Lead.Source.WEBSITE)
                    .owner(testUser)
                    .build();
            leadRepository.save(lead);
        }

        mockMvc.perform(get("/api/v1/leads")
                        .param("page", "0")
                        .param("size", "3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.totalElements").value(6)) // 1 original + 5 new
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(false));
    }

    @Test
    @WithMockUser(roles = "SALES_REP")
    void shouldGetLeadsByStatusEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/leads/status/NEW")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status").value("NEW"));
    }
}