package com.example.infra.repository;

import com.example.core.domain.Lead;
import com.example.core.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {
    
    Page<Lead> findByOwner(User owner, Pageable pageable);
    
    @Query("SELECT l FROM Lead l WHERE l.status = :status")
    List<Lead> findByStatus(@Param("status") Lead.Status status);
    
    @Query("SELECT l FROM Lead l WHERE l.status = :status")
    Page<Lead> findByStatusWithPagination(@Param("status") Lead.Status status, Pageable pageable);
    
    @Query("SELECT l FROM Lead l WHERE l.owner = :owner AND l.status = :status")
    Page<Lead> findByOwnerAndStatus(@Param("owner") User owner, 
                                   @Param("status") Lead.Status status, 
                                   Pageable pageable);
    
    @Query("SELECT l FROM Lead l WHERE l.source = :source")
    List<Lead> findBySource(@Param("source") Lead.Source source);
    
    @Query("SELECT l FROM Lead l WHERE l.source = :source")
    Page<Lead> findBySourceWithPagination(@Param("source") Lead.Source source, Pageable pageable);
    
    @Query("SELECT l FROM Lead l WHERE l.createdAt BETWEEN :startDate AND :endDate")
    List<Lead> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                     @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT l FROM Lead l WHERE l.createdAt BETWEEN :startDate AND :endDate")
    Page<Lead> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                     @Param("endDate") LocalDateTime endDate,
                                     Pageable pageable);
    
    @Query("SELECT COUNT(l) FROM Lead l WHERE l.owner = :owner AND l.status = :status")
    long countByOwnerAndStatus(@Param("owner") User owner, @Param("status") Lead.Status status);
    
    // Search by company name (case insensitive)
    @Query("SELECT l FROM Lead l WHERE LOWER(l.companyName) LIKE LOWER(CONCAT('%', :companyName, '%'))")
    Page<Lead> findByCompanyNameContainingIgnoreCase(@Param("companyName") String companyName, Pageable pageable);
    
    // Search by contact name (case insensitive)  
    @Query("SELECT l FROM Lead l WHERE LOWER(l.contactName) LIKE LOWER(CONCAT('%', :contactName, '%'))")
    Page<Lead> findByContactNameContainingIgnoreCase(@Param("contactName") String contactName, Pageable pageable);
    
    // Search by email (case insensitive)
    @Query("SELECT l FROM Lead l WHERE LOWER(l.email) LIKE LOWER(CONCAT('%', :email, '%'))")
    Page<Lead> findByEmailContainingIgnoreCase(@Param("email") String email, Pageable pageable);
    
    // Combined search across company name, contact name, and email
    @Query("SELECT l FROM Lead l WHERE " +
           "LOWER(l.companyName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(l.contactName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(l.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Lead> searchLeads(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    // Advanced filter with multiple criteria
    @Query("SELECT l FROM Lead l WHERE " +
           "(:status IS NULL OR l.status = :status) AND " +
           "(:source IS NULL OR l.source = :source) AND " +
           "(:startDate IS NULL OR l.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR l.createdAt <= :endDate) AND " +
           "(:searchTerm IS NULL OR " +
           "  LOWER(l.companyName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "  LOWER(l.contactName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "  LOWER(l.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))" +
           ")")
    Page<Lead> findLeadsWithFilters(
        @Param("status") Lead.Status status,
        @Param("source") Lead.Source source,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("searchTerm") String searchTerm,
        Pageable pageable);
        
    // Count leads with filters
    @Query("SELECT COUNT(l) FROM Lead l WHERE " +
           "(:status IS NULL OR l.status = :status) AND " +
           "(:source IS NULL OR l.source = :source) AND " +
           "(:startDate IS NULL OR l.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR l.createdAt <= :endDate)")
    long countLeadsWithFilters(
        @Param("status") Lead.Status status,
        @Param("source") Lead.Source source,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate);
}