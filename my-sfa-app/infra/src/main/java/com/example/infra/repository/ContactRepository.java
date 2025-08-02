package com.example.infra.repository;

import com.example.core.domain.Account;
import com.example.core.domain.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    
    List<Contact> findByAccount(Account account);
    
    @Query("SELECT c FROM Contact c WHERE c.account = :account AND c.isPrimary = true")
    Optional<Contact> findPrimaryContactByAccount(@Param("account") Account account);
    
    Optional<Contact> findByEmail(String email);
    
    @Query("SELECT c FROM Contact c WHERE c.firstName LIKE %:name% OR c.lastName LIKE %:name%")
    List<Contact> findByNameContaining(@Param("name") String name);
    
    @Query("SELECT c FROM Contact c WHERE c.account.id = :accountId")
    List<Contact> findByAccountId(@Param("accountId") Long accountId);
    
    @Query("SELECT COUNT(c) FROM Contact c WHERE c.account = :account")
    long countByAccount(@Param("account") Account account);
    
    boolean existsByEmailAndAccount(String email, Account account);
}