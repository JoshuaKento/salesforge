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
    
    @Query("SELECT l FROM Lead l WHERE l.owner = :owner AND l.status = :status")
    Page<Lead> findByOwnerAndStatus(@Param("owner") User owner, 
                                   @Param("status") Lead.Status status, 
                                   Pageable pageable);
    
    @Query("SELECT l FROM Lead l WHERE l.source = :source")
    List<Lead> findBySource(@Param("source") Lead.Source source);
    
    @Query("SELECT l FROM Lead l WHERE l.createdAt BETWEEN :startDate AND :endDate")
    List<Lead> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                     @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(l) FROM Lead l WHERE l.owner = :owner AND l.status = :status")
    long countByOwnerAndStatus(@Param("owner") User owner, @Param("status") Lead.Status status);
}