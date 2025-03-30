package com.example.demo.controller;




import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.entity.Charger;
import com.example.demo.service.ChargerService;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api/chargers")
public class ChargerController {
    private final ChargerService chargerService;
    private static final Logger logger = LoggerFactory.getLogger(ChargerController.class);

    public ChargerController(ChargerService chargerService) {
        this.chargerService = chargerService;
    }

    @GetMapping
    public ResponseEntity<List<Charger>> getAllChargers() {
        try {
            logger.info("Fetching all chargers");
            List<Charger> chargers = chargerService.getAllChargers();
            return ResponseEntity.ok(chargers);
        } catch (Exception e) {
            logger.error("Error fetching chargers");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

   
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Charger>> getChargersByStatus(@PathVariable String status) {
        try {
            logger.info("Fetching chargers with status: {}", status);
            List<Charger> chargers = chargerService.getChargersByStatus(status);
            return ResponseEntity.ok(chargers);
        } catch (Exception e) {
            logger.error("Error fetching chargers by status :", status);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{chargerId}/status")
    public ResponseEntity<String> updateChargerStatus(@PathVariable Long chargerId, @RequestBody Map<String, String> requestBody) {
        try {
            String status = requestBody.get("status");
            logger.info("Updating charger {} to status: {}", chargerId, status);
	Optional<Charger> chargerById = chargerService.getChargerById(chargerId);
            
            if (chargerById.isEmpty()) {
                logger.warn("Charger with ID {} not found", chargerId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Charger ID " + chargerId + " not found");
            }
            chargerService.updateChargerStatus(chargerId, status);
            return ResponseEntity.ok("Charger status updated successfully");
        } catch (NoSuchElementException e) {
            logger.warn("Charger with ID {} not found", chargerId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Charger not found");
        } catch (Exception e) {
            logger.error("Error updating charger {}: {}", chargerId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update charger status");
        }
    }

    @PostMapping("/{chargerId}/heartbeat")
    public ResponseEntity<String> receiveHeartbeat(@PathVariable Long chargerId) {
        try {
            logger.info("Received heartbeat for charger ID: {}", chargerId);
         
		Optional<Charger> chargerById = chargerService.getChargerById(chargerId);
            
            if (chargerById.isEmpty()) {
                logger.warn("Charger with ID {} not found", chargerId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Charger ID " + chargerId + " not found");
            }
            ChargerService chargerService2 = chargerService;
    		chargerService2.updateChargerHeartbeat(chargerId);
            return ResponseEntity.ok("Heartbeat received for charger ID: " + chargerId);
            
        } catch (Exception e) {
            logger.error("Error processing heartbeat for charger {}: {}", chargerId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process heartbeat for charger ID: " + chargerId);
        }
    }


    @PostMapping("/save")
    public ResponseEntity<Charger> saveCharger(@RequestBody Charger charger) {
        try {
            logger.info("Saving new charger: {}", charger);
            Charger savedCharger = chargerService.saveCharger(charger);
            return ResponseEntity.ok(savedCharger);
        } catch (Exception e) {
            logger.error("Error saving charger: {} Fill the required Field");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
