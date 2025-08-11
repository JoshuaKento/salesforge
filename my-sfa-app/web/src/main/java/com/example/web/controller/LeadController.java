package com.example.web.controller;

import com.example.core.domain.Lead;
import com.example.core.domain.User;
import com.example.infra.repository.LeadRepository;
import com.example.security.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/leads")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Leads", description = "Lead management operations for CRM system")
public class LeadController {
    
    private final LeadRepository leadRepository;
    private final AuthService authService;
    
    @GetMapping
    @Operation(summary = "Get all leads", 
               description = "Retrieves a paginated list of all leads with advanced filtering and search capabilities")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved leads"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions")
    })
    @PreAuthorize("hasRole('SALES_REP') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> getAllLeads(
            @Parameter(description = "Pagination parameters") Pageable pageable,
            @Parameter(description = "Filter by status") @RequestParam(required = false) Lead.Status status,
            @Parameter(description = "Filter by source") @RequestParam(required = false) Lead.Source source,
            @Parameter(description = "Search term for company name, contact name, or email") @RequestParam(required = false) String search,
            @Parameter(description = "Start date for filtering (ISO format: 2025-08-01T00:00:00)") @RequestParam(required = false) String startDate,
            @Parameter(description = "End date for filtering (ISO format: 2025-08-31T23:59:59)") @RequestParam(required = false) String endDate) {
        
        log.info("Fetching leads with filters - status: {}, source: {}, search: '{}', startDate: {}, endDate: {}", 
                status, source, search, startDate, endDate);
        
        try {
            LocalDateTime startDateTime = null;
            LocalDateTime endDateTime = null;
            
            // Parse date parameters if provided
            if (startDate != null && !startDate.trim().isEmpty()) {
                try {
                    startDateTime = LocalDateTime.parse(startDate);
                } catch (Exception e) {
                    log.warn("Invalid start date format: {}", startDate);
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Invalid start date format. Use ISO format: 2025-08-01T00:00:00");
                    return ResponseEntity.badRequest().body(error);
                }
            }
            
            if (endDate != null && !endDate.trim().isEmpty()) {
                try {
                    endDateTime = LocalDateTime.parse(endDate);
                } catch (Exception e) {
                    log.warn("Invalid end date format: {}", endDate);
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Invalid end date format. Use ISO format: 2025-08-31T23:59:59");
                    return ResponseEntity.badRequest().body(error);
                }
            }
            
            // Validate date range
            if (startDateTime != null && endDateTime != null && startDateTime.isAfter(endDateTime)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Start date cannot be after end date");
                return ResponseEntity.badRequest().body(error);
            }
            
            Page<Lead> leads;
            
            // Use advanced filtering if any filter is applied
            if (status != null || source != null || search != null || startDateTime != null || endDateTime != null) {
                leads = leadRepository.findLeadsWithFilters(
                    status, 
                    source, 
                    startDateTime, 
                    endDateTime, 
                    search, 
                    pageable
                );
            } else {
                leads = leadRepository.findAll(pageable);
            }
            
            log.info("Successfully retrieved {} leads out of {} total", leads.getNumberOfElements(), leads.getTotalElements());
            
            // Add metadata to response
            Map<String, Object> response = new HashMap<>();
            response.put("content", leads.getContent());
            response.put("pageable", Map.of(
                "pageNumber", leads.getNumber(),
                "pageSize", leads.getSize(),
                "sort", leads.getSort().toString()
            ));
            response.put("totalElements", leads.getTotalElements());
            response.put("totalPages", leads.getTotalPages());
            response.put("first", leads.isFirst());
            response.put("last", leads.isLast());
            response.put("empty", leads.isEmpty());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error retrieving leads: {}", e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to retrieve leads: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get lead by ID", 
               description = "Retrieves a specific lead by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lead found and returned"),
        @ApiResponse(responseCode = "404", description = "Lead not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions")
    })
    @PreAuthorize("hasRole('SALES_REP') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> getLeadById(
            @Parameter(description = "Lead ID", required = true) @PathVariable Long id) {
        
        log.info("Fetching lead with ID: {}", id);
        
        try {
            Optional<Lead> lead = leadRepository.findById(id);
            
            if (lead.isPresent()) {
                log.info("Successfully found lead with ID: {}", id);
                return ResponseEntity.ok(lead.get());
            } else {
                log.warn("Lead not found with ID: {}", id);
                Map<String, String> error = new HashMap<>();
                error.put("error", "Lead not found with ID: " + id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error retrieving lead with ID {}: {}", id, e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to retrieve lead");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    @PostMapping
    @Operation(summary = "Create a new lead", 
               description = "Creates a new lead in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Lead successfully created"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions")
    })
    @PreAuthorize("hasRole('SALES_REP') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> createLead(
            @Parameter(description = "Lead data", required = true) @Valid @RequestBody Lead lead) {
        
        log.info("Creating new lead for company: {}", lead.getCompanyName());
        
        try {
            // Validate required fields
            if (lead.getCompanyName() == null || lead.getCompanyName().trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Company name is required");
                return ResponseEntity.badRequest().body(error);
            }
            
            if (lead.getContactName() == null || lead.getContactName().trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Contact name is required");
                return ResponseEntity.badRequest().body(error);
            }
            
            // Set default status if not provided
            if (lead.getStatus() == null) {
                lead.setStatus(Lead.Status.NEW);
            }
            
            // Set default source if not provided
            if (lead.getSource() == null) {
                lead.setSource(Lead.Source.OTHER);
            }
            
            // Set owner to current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUserEmail = authentication.getName();
            User currentUser = authService.findUserByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
            lead.setOwner(currentUser);
            
            Lead savedLead = leadRepository.save(lead);
            log.info("Successfully created lead with ID: {}", savedLead.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedLead);
            
        } catch (ConstraintViolationException e) {
            log.error("Validation error creating lead: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Validation failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            log.error("Error creating lead: {}", e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create lead");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing lead", 
               description = "Updates all fields of an existing lead")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lead successfully updated"),
        @ApiResponse(responseCode = "404", description = "Lead not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions")
    })
    @PreAuthorize("hasRole('SALES_REP') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateLead(
            @Parameter(description = "Lead ID", required = true) @PathVariable Long id, 
            @Parameter(description = "Updated lead data", required = true) @Valid @RequestBody Lead lead) {
        
        log.info("Updating lead with ID: {}", id);
        
        try {
            if (!leadRepository.existsById(id)) {
                log.warn("Lead not found for update with ID: {}", id);
                Map<String, String> error = new HashMap<>();
                error.put("error", "Lead not found with ID: " + id);
                return ResponseEntity.notFound().build();
            }
            
            // Validate required fields
            if (lead.getCompanyName() == null || lead.getCompanyName().trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Company name is required");
                return ResponseEntity.badRequest().body(error);
            }
            
            if (lead.getContactName() == null || lead.getContactName().trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Contact name is required");
                return ResponseEntity.badRequest().body(error);
            }
            
            lead.setId(id);
            Lead updatedLead = leadRepository.save(lead);
            log.info("Successfully updated lead with ID: {}", id);
            return ResponseEntity.ok(updatedLead);
            
        } catch (ConstraintViolationException e) {
            log.error("Validation error updating lead {}: {}", id, e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Validation failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            log.error("Error updating lead {}: {}", id, e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to update lead");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    @PatchMapping("/{id}")
    @Operation(summary = "Partially update a lead", 
               description = "Updates specific fields of an existing lead")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lead successfully updated"),
        @ApiResponse(responseCode = "404", description = "Lead not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions")
    })
    @PreAuthorize("hasRole('SALES_REP') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> partialUpdateLead(
            @Parameter(description = "Lead ID", required = true) @PathVariable Long id,
            @Parameter(description = "Partial lead data", required = true) @RequestBody Map<String, Object> updates) {
        
        log.info("Partially updating lead with ID: {}", id);
        
        try {
            Optional<Lead> optionalLead = leadRepository.findById(id);
            if (!optionalLead.isPresent()) {
                log.warn("Lead not found for partial update with ID: {}", id);
                Map<String, String> error = new HashMap<>();
                error.put("error", "Lead not found with ID: " + id);
                return ResponseEntity.notFound().build();
            }
            
            Lead existingLead = optionalLead.get();
            
            // Apply updates
            updates.forEach((key, value) -> {
                switch (key.toLowerCase()) {
                    case "companyname":
                        existingLead.setCompanyName((String) value);
                        break;
                    case "contactname":
                        existingLead.setContactName((String) value);
                        break;
                    case "email":
                        existingLead.setEmail((String) value);
                        break;
                    case "phone":
                        existingLead.setPhone((String) value);
                        break;
                    case "status":
                        existingLead.setStatus(Lead.Status.valueOf((String) value));
                        break;
                    case "source":
                        existingLead.setSource(Lead.Source.valueOf((String) value));
                        break;
                }
            });
            
            Lead updatedLead = leadRepository.save(existingLead);
            log.info("Successfully partially updated lead with ID: {}", id);
            return ResponseEntity.ok(updatedLead);
            
        } catch (IllegalArgumentException e) {
            log.error("Invalid enum value in partial update for lead {}: {}", id, e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid value provided: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            log.error("Error partially updating lead {}: {}", id, e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to update lead");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a lead", 
               description = "Permanently removes a lead from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Lead successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Lead not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions")
    })
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteLead(
            @Parameter(description = "Lead ID", required = true) @PathVariable Long id) {
        
        log.info("Deleting lead with ID: {}", id);
        
        try {
            if (!leadRepository.existsById(id)) {
                log.warn("Lead not found for deletion with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
            
            leadRepository.deleteById(id);
            log.info("Successfully deleted lead with ID: {}", id);
            return ResponseEntity.noContent().build();
            
        } catch (Exception e) {
            log.error("Error deleting lead {}: {}", id, e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to delete lead");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "Get leads by status", 
               description = "Retrieves all leads with a specific status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved leads by status"),
        @ApiResponse(responseCode = "400", description = "Invalid status parameter"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions")
    })
    @PreAuthorize("hasRole('SALES_REP') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> getLeadsByStatus(
            @Parameter(description = "Lead status", required = true) @PathVariable Lead.Status status) {
        
        log.info("Fetching leads with status: {}", status);
        
        try {
            List<Lead> leads = leadRepository.findByStatus(status);
            log.info("Successfully retrieved {} leads with status: {}", leads.size(), status);
            return ResponseEntity.ok(leads);
        } catch (Exception e) {
            log.error("Error retrieving leads by status {}: {}", status, e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to retrieve leads by status");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search leads", 
               description = "Search leads by company name, contact name, or email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved search results"),
        @ApiResponse(responseCode = "400", description = "Invalid search parameters"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions")
    })
    @PreAuthorize("hasRole('SALES_REP') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> searchLeads(
            @Parameter(description = "Search term", required = true) @RequestParam String q,
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        
        if (q == null || q.trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Search term 'q' is required and cannot be empty");
            return ResponseEntity.badRequest().body(error);
        }
        
        log.info("Searching leads with term: '{}'", q);
        
        try {
            Page<Lead> leads = leadRepository.searchLeads(q.trim(), pageable);
            log.info("Search returned {} results for term: '{}'", leads.getTotalElements(), q);
            
            Map<String, Object> response = new HashMap<>();
            response.put("content", leads.getContent());
            response.put("searchTerm", q);
            response.put("totalResults", leads.getTotalElements());
            response.put("pageable", Map.of(
                "pageNumber", leads.getNumber(),
                "pageSize", leads.getSize()
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error searching leads with term '{}': {}", q, e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Search failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    @GetMapping("/source/{source}")
    @Operation(summary = "Get leads by source", 
               description = "Retrieves leads filtered by their source")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved leads by source"),
        @ApiResponse(responseCode = "400", description = "Invalid source parameter"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions")
    })
    @PreAuthorize("hasRole('SALES_REP') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> getLeadsBySource(
            @Parameter(description = "Lead source", required = true) @PathVariable Lead.Source source,
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        
        log.info("Fetching leads with source: {}", source);
        
        try {
            Page<Lead> leads = leadRepository.findBySourceWithPagination(source, pageable);
            log.info("Successfully retrieved {} leads with source: {}", leads.getTotalElements(), source);
            return ResponseEntity.ok(leads);
        } catch (Exception e) {
            log.error("Error retrieving leads by source {}: {}", source, e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to retrieve leads by source");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    @GetMapping("/stats")
    @Operation(summary = "Get lead statistics", 
               description = "Returns detailed statistics about leads including counts by status, source, and date ranges")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved lead statistics"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions")
    })
    @PreAuthorize("hasRole('SALES_REP') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> getLeadStatistics() {
        log.info("Fetching lead statistics");
        
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // Count by status
            Map<String, Long> statusCounts = new HashMap<>();
            for (Lead.Status status : Lead.Status.values()) {
                List<Lead> leads = leadRepository.findByStatus(status);
                statusCounts.put(status.name(), (long) leads.size());
            }
            
            // Count by source
            Map<String, Long> sourceCounts = new HashMap<>();
            for (Lead.Source source : Lead.Source.values()) {
                List<Lead> leads = leadRepository.findBySource(source);
                sourceCounts.put(source.name(), (long) leads.size());
            }
            
            // Total counts
            long totalLeads = leadRepository.count();
            
            // Recent activity (last 30 days)
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
            List<Lead> recentLeads = leadRepository.findByCreatedAtBetween(
                thirtyDaysAgo, LocalDateTime.now()
            );
            
            stats.put("totalLeads", totalLeads);
            stats.put("statusBreakdown", statusCounts);
            stats.put("sourceBreakdown", sourceCounts);
            stats.put("recentLeads", recentLeads.size());
            stats.put("generatedAt", LocalDateTime.now());
            
            log.info("Successfully retrieved lead statistics - total: {}, recent: {}", 
                    totalLeads, recentLeads.size());
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error retrieving lead statistics: {}", e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to retrieve lead statistics");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/count")
    @Operation(summary = "Get lead counts by status", 
               description = "Returns count of leads grouped by status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved lead counts"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions")
    })
    @PreAuthorize("hasRole('SALES_REP') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> getLeadCounts() {
        log.info("Fetching lead counts by status");
        
        try {
            Map<String, Long> counts = new HashMap<>();
            
            for (Lead.Status status : Lead.Status.values()) {
                List<Lead> leads = leadRepository.findByStatus(status);
                counts.put(status.name(), (long) leads.size());
            }
            
            log.info("Successfully retrieved lead counts: {}", counts);
            return ResponseEntity.ok(counts);
        } catch (Exception e) {
            log.error("Error retrieving lead counts: {}", e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to retrieve lead counts");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}