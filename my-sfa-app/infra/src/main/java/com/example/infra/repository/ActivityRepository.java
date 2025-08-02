package com.example.infra.repository;

import com.example.core.domain.Activity;
import com.example.core.domain.Contact;
import com.example.core.domain.Lead;
import com.example.core.domain.Opportunity;
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
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    
    Page<Activity> findByUser(User user, Pageable pageable);
    
    List<Activity> findByLead(Lead lead);
    
    List<Activity> findByContact(Contact contact);
    
    List<Activity> findByOpportunity(Opportunity opportunity);
    
    @Query("SELECT a FROM Activity a WHERE a.type = :type")
    List<Activity> findByType(@Param("type") Activity.Type type);
    
    @Query("SELECT a FROM Activity a WHERE a.status = :status")
    List<Activity> findByStatus(@Param("status") Activity.Status status);
    
    @Query("SELECT a FROM Activity a WHERE a.user = :user AND a.status = :status")
    Page<Activity> findByUserAndStatus(@Param("user") User user, 
                                     @Param("status") Activity.Status status, 
                                     Pageable pageable);
    
    @Query("SELECT a FROM Activity a WHERE a.activityDate BETWEEN :startDate AND :endDate")
    List<Activity> findByActivityDateBetween(@Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT a FROM Activity a WHERE a.user = :user AND a.activityDate >= :fromDate ORDER BY a.activityDate ASC")
    List<Activity> findUpcomingActivitiesByUser(@Param("user") User user, 
                                              @Param("fromDate") LocalDateTime fromDate);
    
    @Query("SELECT COUNT(a) FROM Activity a WHERE a.user = :user AND a.status = :status")
    long countByUserAndStatus(@Param("user") User user, @Param("status") Activity.Status status);
}