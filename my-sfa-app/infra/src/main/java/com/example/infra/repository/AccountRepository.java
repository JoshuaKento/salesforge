package com.example.infra.repository;

import com.example.core.domain.Account;
import com.example.core.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    Optional<Account> findByName(String name);
    
    Page<Account> findByOwner(User owner, Pageable pageable);
    
    @Query("SELECT a FROM Account a WHERE a.industry = :industry")
    List<Account> findByIndustry(@Param("industry") String industry);
    
    @Query("SELECT a FROM Account a WHERE a.annualRevenue >= :minRevenue")
    List<Account> findByAnnualRevenueGreaterThanEqual(@Param("minRevenue") BigDecimal minRevenue);
    
    @Query("SELECT a FROM Account a WHERE a.employeeCount BETWEEN :minEmployees AND :maxEmployees")
    List<Account> findByEmployeeCountBetween(@Param("minEmployees") Integer minEmployees, 
                                           @Param("maxEmployees") Integer maxEmployees);
    
    @Query("SELECT DISTINCT a.industry FROM Account a WHERE a.industry IS NOT NULL ORDER BY a.industry")
    List<String> findDistinctIndustries();
    
    boolean existsByName(String name);
}