package com.example.infra.repository;

import com.example.core.domain.Account;
import com.example.core.domain.Opportunity;
import com.example.core.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface OpportunityRepository extends JpaRepository<Opportunity, Long> {
    
    Page<Opportunity> findByOwner(User owner, Pageable pageable);
    
    List<Opportunity> findByAccount(Account account);
    
    @Query("SELECT o FROM Opportunity o WHERE o.stage = :stage")
    List<Opportunity> findByStage(@Param("stage") Opportunity.Stage stage);
    
    @Query("SELECT o FROM Opportunity o WHERE o.owner = :owner AND o.stage = :stage")
    Page<Opportunity> findByOwnerAndStage(@Param("owner") User owner, 
                                        @Param("stage") Opportunity.Stage stage, 
                                        Pageable pageable);
    
    @Query("SELECT o FROM Opportunity o WHERE o.amount >= :minAmount")
    List<Opportunity> findByAmountGreaterThanEqual(@Param("minAmount") BigDecimal minAmount);
    
    @Query("SELECT o FROM Opportunity o WHERE o.closeDate BETWEEN :startDate AND :endDate")
    List<Opportunity> findByCloseDateBetween(@Param("startDate") LocalDate startDate, 
                                           @Param("endDate") LocalDate endDate);
    
    @Query("SELECT o FROM Opportunity o WHERE o.probability >= :minProbability")
    List<Opportunity> findByProbabilityGreaterThanEqual(@Param("minProbability") Integer minProbability);
    
    @Query("SELECT SUM(o.amount) FROM Opportunity o WHERE o.owner = :owner AND o.stage IN :stages")
    BigDecimal sumAmountByOwnerAndStages(@Param("owner") User owner, 
                                       @Param("stages") List<Opportunity.Stage> stages);
    
    @Query("SELECT COUNT(o) FROM Opportunity o WHERE o.owner = :owner AND o.stage = :stage")
    long countByOwnerAndStage(@Param("owner") User owner, @Param("stage") Opportunity.Stage stage);
}