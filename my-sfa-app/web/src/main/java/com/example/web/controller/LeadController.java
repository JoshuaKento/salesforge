package com.example.web.controller;

import com.example.core.domain.Lead;
import com.example.infra.repository.LeadRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/leads")
@RequiredArgsConstructor
@Tag(name = "Leads", description = "Lead management operations")
public class LeadController {
    
    private final LeadRepository leadRepository;
    
    @GetMapping
    @Operation(summary = "Get all leads")
    @PreAuthorize("hasRole('SALES_REP') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<Page<Lead>> getAllLeads(Pageable pageable) {
        Page<Lead> leads = leadRepository.findAll(pageable);
        return ResponseEntity.ok(leads);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get lead by ID")
    @PreAuthorize("hasRole('SALES_REP') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<Lead> getLeadById(@PathVariable Long id) {
        Optional<Lead> lead = leadRepository.findById(id);
        return lead.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @Operation(summary = "Create a new lead")
    @PreAuthorize("hasRole('SALES_REP') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<Lead> createLead(@Valid @RequestBody Lead lead) {
        Lead savedLead = leadRepository.save(lead);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLead);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing lead")
    @PreAuthorize("hasRole('SALES_REP') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<Lead> updateLead(@PathVariable Long id, @Valid @RequestBody Lead lead) {
        if (!leadRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        lead.setId(id);
        Lead updatedLead = leadRepository.save(lead);
        return ResponseEntity.ok(updatedLead);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a lead")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteLead(@PathVariable Long id) {
        if (!leadRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        leadRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "Get leads by status")
    @PreAuthorize("hasRole('SALES_REP') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<java.util.List<Lead>> getLeadsByStatus(@PathVariable Lead.Status status) {
        java.util.List<Lead> leads = leadRepository.findByStatus(status);
        return ResponseEntity.ok(leads);
    }
}