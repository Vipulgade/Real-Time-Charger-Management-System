package com.example.demo.service;



import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Charger;
import com.example.demo.repository.ChargerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ChargerService {
    private final ChargerRepository chargerRepository;

    public ChargerService(ChargerRepository chargerRepository) {
        this.chargerRepository = chargerRepository;
    }

    public List<Charger> getAllChargers() {
        return chargerRepository.findAll();
    }

    public void updateChargerStatus(Long chargerId, String status) {
       /* Optional<Charger> charger = chargerRepository.findByChargerId(chargerId);
        charger.ifPresent(c -> {
            c.setStatus(status);
           // c.setLastHeartbeat(LocalDateTime.now());
            chargerRepository.save(c);
        });*/
    	
         Optional<Charger> chargerOptional = chargerRepository.findByChargerId(chargerId);
        Charger charger = chargerOptional.orElseGet(() -> {
            Charger newCharger = new Charger();
            newCharger.setChargerId(chargerId);
            newCharger.setLastHeartbeat(LocalDateTime.now());
          
            return newCharger;
        });

        charger.setLastHeartbeat(LocalDateTime.now());
        charger.setStatus(status);
        chargerRepository.save(charger);
    }
   
    public void updateChargerHeartbeat(Long chargerId) {
        Optional<Charger> chargerOptional = chargerRepository.findByChargerId(chargerId);
        Charger charger = chargerOptional.orElseGet(() -> {
            Charger newCharger = new Charger();
            newCharger.setChargerId(chargerId);
            newCharger.setStatus("Available"); 
            return newCharger;
        });

        charger.setLastHeartbeat(LocalDateTime.now());;
        charger.setStatus("Available"); 
        chargerRepository.save(charger);
    }
    
    @Scheduled(fixedRate = 300001) 
    @Transactional
    public void checkAndMarkUnavailableChargers() {
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);

        // Fetch chargers whose last heartbeat is older than 5 minutes and are NOT already "Unavailable"
        List<Charger> outdatedChargers = chargerRepository.findByLastHeartbeatBeforeAndStatusIn(fiveMinutesAgo, List.of("Charging", "Faulted", "Available"));
        outdatedChargers.forEach(System.out::println);
        if (!outdatedChargers.isEmpty()) {
            outdatedChargers.forEach(charger -> charger.setStatus("Unavailable"));
            chargerRepository.saveAll(outdatedChargers);
            System.out.println("Marked " + outdatedChargers.size() + " chargers as Unavailable due to no heartbeat.");
        }
    }
        
    public List<Charger> getChargersByStatus(String status) {
        return chargerRepository.findByStatus(status);
    }
    public Optional<Charger> getChargerById(Long chargerId) {
        return chargerRepository.findByChargerId(chargerId);
    }
    public Charger saveCharger(Charger charger) {
    	charger.setLastHeartbeat(LocalDateTime.now());
    	charger.setPowerRating(charger.getPowerRating());
        return chargerRepository.save(charger);
    }

}
