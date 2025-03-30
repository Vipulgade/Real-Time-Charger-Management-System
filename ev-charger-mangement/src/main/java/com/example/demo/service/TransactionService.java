package com.example.demo.service;


import org.springframework.stereotype.Service;

import com.example.demo.entity.Charger;
import com.example.demo.entity.Transaction;
import com.example.demo.repository.ChargerRepository;
import com.example.demo.repository.TransactionRepository;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.util.List;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final ChargerRepository chargerRepository;
    
    public TransactionService(TransactionRepository transactionRepository, ChargerRepository chargerRepository) {
        this.transactionRepository = transactionRepository;
        this.chargerRepository = chargerRepository;
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Transaction getTransactionById(Long id) {
        Optional<Transaction> transaction = transactionRepository.findById(id);
        return transaction.orElseThrow(() -> new RuntimeException("Transaction not found!"));
    }
    public Optional<Transaction> getTransactionById1(Long id) {
        Optional<Transaction> transaction = transactionRepository.findById(id);
        return transaction;
    }
    public List<Transaction> getTransactionsByCharger(Long chargerId) {
        return transactionRepository.findByChargerId(chargerId);
    }

  
    public void saveTransaction(long chargerId, String sessionId) {
        Charger charger = chargerRepository.findByChargerId(chargerId)
                .orElseThrow(() -> new RuntimeException("Charger not found"));

        Transaction transaction = new Transaction();
        transaction.setCharger(charger);
        transaction.setSessionId(sessionId);
        transaction.setStartTime(LocalTime.now().minusMinutes(3));
       
        transaction.setStartDate(LocalDate.now());
        
       
       
        	
           
       charger.setStatus("Charging");
       chargerRepository.save(charger);
       

        transactionRepository.save(transaction);
    }

    public void endTransaction(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        transaction.setEndTime(LocalTime.now());
        transaction.setEndDate(LocalDate.now());

        Long chargerId = transaction.getCharger().getChargerId();
        Charger charger = chargerRepository.findByChargerId(chargerId)
                .orElseThrow(() -> new RuntimeException("Charger not found"));

        if (transaction.getStartTime() != null && transaction.getEndTime() != null &&
            charger != null && charger.getLastHeartbeat() != null &&
            transaction.getStartDate() != null && transaction.getEndDate() != null) {

            
            Duration duration = Duration.between(transaction.getStartTime(), transaction.getEndTime());
            
            double millis = duration.toMillis() ; 
            double hours=millis/(1000.0 * 60 * 60);
          
           
            System.out.println("Duration (Minutes): " + duration.toMinutes());
            System.out.println("Duration (Hours): " + hours);
            System.out.println("Charger Power Rating: " + charger.getPowerRating());
          
            transaction.setEnergyConsumed((double) (Math.round((charger.getPowerRating() * hours) * 100.0) / 100.0));  // kW * Hours

        } else {
            System.out.println("Missing Data! Defaulting energy to 1000 kWh.");
            transaction.setEnergyConsumed(100);
        }
        charger.setStatus("Available");
        chargerRepository.save(charger);
        transactionRepository.save(transaction);
    }


    public List<Transaction> findByChargerAndTimeRange(Long chargerId,
            LocalDate startDate, LocalTime startTime,
            LocalDate endDate, LocalTime endTime) {
    	startTime = normalizeTime(startTime);
        endTime = normalizeTime(endTime);
List<Transaction> transactions = transactionRepository.findByChargerAndTimeRange(
chargerId, startDate, startTime, endDate, endTime);

if (transactions.isEmpty()) {
System.out.println("No transactions found for charger ID: " + chargerId);
} else {
System.out.println("Transactions found: " + transactions.size());
}

return transactions;
}
    private LocalTime normalizeTime(LocalTime time) {
        return time.truncatedTo(ChronoUnit.SECONDS);  // Removes millisecond precision issues
    }

	
}
