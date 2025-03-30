package com.example.demo.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.Transaction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
@EnableJpaRepositories
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByChargerId(Long chargerId);
    List<Transaction> findByChargerIdAndStartTimeBetween(Long chargerId, LocalDateTime start, LocalDateTime end);
	Optional<Transaction> findBySessionId(String sessionId);
	@Query("SELECT t FROM Transaction t WHERE t.charger.chargerId = :chargerId " +
		       "AND t.startDate BETWEEN :startDate AND :endDate " +
		       "AND  t.startTime >= :startTime " +
		       "AND t.endTime <= :endTime " )
	    List<Transaction> findByChargerAndTimeRange(
	            @Param("chargerId") Long chargerId,
	            @Param("startDate") LocalDate startDate, 
	            @Param("startTime") LocalTime startTime,
	            @Param("endDate") LocalDate endDate, 
	            @Param("endTime") LocalTime endTime);
	}
