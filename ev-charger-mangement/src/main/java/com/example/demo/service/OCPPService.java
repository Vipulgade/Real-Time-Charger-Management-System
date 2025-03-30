package com.example.demo.service;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import com.example.demo.entity.Charger;
import com.example.demo.entity.Transaction;
import com.example.demo.repository.ChargerRepository;
import com.example.demo.repository.TransactionRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OCPPService {

    private final ChargerRepository chargerRepository;
    private final TransactionRepository transactionRepository;
    private static final Logger logger = LoggerFactory.getLogger(OCPPService.class);

    private static final Map<String, Long> lastHeartbeat = new ConcurrentHashMap<>();

    public OCPPService(ChargerRepository chargerRepository, TransactionRepository transactionRepository) {
        this.chargerRepository = chargerRepository;
        this.transactionRepository = transactionRepository;
    }

   
    public void processBootNotification(JSONObject request, WebSocketSession session) {
        try {
            Long chargerId = request.getLong("chargerId");
            logger.info("Received BootNotification from Charger ID: {}", chargerId);

            // Try to find the charger
            Optional<Charger> chargerOptional = chargerRepository.findByChargerId(chargerId);

            if (chargerOptional.isEmpty()) {
                // If charger is not found, throw an exception
                throw new RuntimeException("Charger ID " + chargerId + " not found in the system.");
            }

            Charger charger = chargerOptional.get();
            charger.setLastHeartbeat(LocalDateTime.now());
            chargerRepository.save(charger);

            logger.info("BootNotification processed successfully for Charger ID: {}", chargerId);
            System.out.println("BootNotification received from Charger ID: " + chargerId);
            
        } catch (RuntimeException e) {
            logger.error("Error processing BootNotification: {}", e.getMessage());
            System.err.println("Error processing BootNotification: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error processing BootNotification", e);
        }
    }



      

  
    public void processHeartbeat(JSONObject request, WebSocketSession session) {
    	 try {
             Long chargerId = request.getLong("chargerId");
             logger.info("Received Heartbeat from Charger ID: {}", chargerId);

             chargerRepository.findByChargerId(chargerId).ifPresentOrElse(charger -> {
                 charger.setLastHeartbeat(LocalDateTime.now());
                 chargerRepository.save(charger);
                 lastHeartbeat.put(chargerId.toString(), System.currentTimeMillis());

                 logger.info("Heartbeat updated for Charger ID: {}", chargerId);
             }, () -> logger.warn("Heartbeat received from unknown Charger ID: {}", chargerId));
         } catch (Exception e) {
             logger.error("Error processing Heartbeat: {}", e.getMessage(), e);
         }
    }
    public Map<String, Long> getLastHeartbeat() {
        return lastHeartbeat;
    }
  
    public void processStatusNotification(JSONObject request, WebSocketSession session) {
    	 try {
             Long chargerId = request.getLong("chargerId");
             String status = request.getString("status");
             logger.info("Received StatusNotification: Charger {} is now {}", chargerId, status);

             chargerRepository.findByChargerId(chargerId).ifPresentOrElse(charger -> {
                 charger.setStatus(status);
                 chargerRepository.save(charger);
                 logger.info("Charger ID {} status updated to {}", chargerId, status);
             }, () -> logger.warn("StatusNotification received for unknown Charger ID: {}", chargerId));
         } catch (Exception e) {
             logger.error("Error processing StatusNotification: {}", e.getMessage(), e);
         }
     }

   
    public void processStartTransaction(JSONObject request, WebSocketSession session) {
    	 try {
             Long chargerId = request.getLong("chargerId");
             double energyConsumed = request.getDouble("energyConsumed");
             logger.info("Received StartTransaction for Charger ID: {}", chargerId);

             Optional<Charger> chargerOptional = chargerRepository.findByChargerId(chargerId);
             if (chargerOptional.isEmpty()) {
                 logger.warn("StartTransaction failed: Charger not found for ID: {}", chargerId);
                 return;
             }

             Transaction transaction = new Transaction();
             transaction.setCharger(chargerOptional.get());
             transaction.setSessionId(session.getId());  
             transaction.setEnergyConsumed(energyConsumed);
             transaction.setStartTime(LocalTime.now());
             transaction.setStartDate(LocalDate.now());

             transactionRepository.save(transaction);
             logger.info("StartTransaction recorded for Charger ID: {} with Session ID: {}", chargerId, session.getId());
         } catch (Exception e) {
             logger.error("Error processing StartTransaction: {}", e.getMessage(), e);
         }
     }
    
    public void processStopTransaction(JSONObject request, WebSocketSession session) {
    	 try {
       String sessionId = request.getString("sessionId");
        logger.info("Received StopTransaction for Session ID: ", sessionId);
        Optional<Transaction> transactionOptional = transactionRepository.findBySessionId(sessionId);
        transactionRepository.findByChargerId(request.getLong("chargerId"));
        if (transactionOptional.isPresent()) {
            Transaction transaction = transactionOptional.get();
            transaction.setEndTime(LocalTime.now());
            transaction.setEndDate(LocalDate.now());
            transactionRepository.save(transaction);
            logger.info("StopTransaction recorded for Session ID: ", sessionId);
        } else {
            logger.warn("StopTransaction failed: Transaction not found for Session ID: ", sessionId);
        }
    	  } catch (Exception e) {
              logger.error("Error processing StopTransaction: {}", e.getMessage(), e);
          }
    }

    
    @Scheduled(fixedRate = 300000) 
    public void checkHeartbeat() {
        long currentTime = System.currentTimeMillis();
        
        lastHeartbeat.forEach((chargerId, lastTime) -> {
            System.out.println("Charger " + chargerId + " last heartbeat at: " + lastTime);

            if (currentTime - lastTime > 300000) { 
               // System.out.println("Charger " + chargerId + " is inactive, updating status...");
                logger.warn("Charger  is inactive, marking as unavailable...", chargerId);
                chargerRepository.findByChargerId(Long.parseLong(chargerId)).ifPresent(charger -> {
                    charger.setStatus("Unavailable");
                    chargerRepository.save(charger);
                    //System.out.println("Charger " + chargerId + " is now UNAVAILABLE");
                    logger.warn("Charger  is now UNAVAILABLE", chargerId);
                });
            }
        });
    }
    
    public void markChargerUnavailable(String ChargerId) {
    	 try {
             logger.info("Marking Charger {} as UNAVAILABLE", ChargerId);

             Charger charger = chargerRepository.findByChargerId(Long.parseLong(ChargerId))
                 .orElseThrow(() -> new RuntimeException("Charger not found"));

             charger.setStatus("UNAVAILABLE");
             chargerRepository.save(charger);
             logger.info("Charger  marked as UNAVAILABLE successfully", ChargerId);
         } catch (Exception e) {
             logger.error("Error marking Charger  as UNAVAILABLE: ", ChargerId, e.getMessage(), e);
         }
    }

}
