package com.example.demo.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.example.demo.entity.Charger;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@EnableJpaRepositories
public interface ChargerRepository extends JpaRepository<Charger, Long> {
    Optional<Charger> findByChargerId(Long chargerIdSRNO);
    List<Charger> findByStatus(String status);
	List<Charger> findByLastHeartbeatBefore(LocalDateTime lastHeartBeat);
	List<Charger> findByLastHeartbeatBeforeAndStatusIn(LocalDateTime fiveMinutesAgo, List<String> of);
	
	
	
	  
}
