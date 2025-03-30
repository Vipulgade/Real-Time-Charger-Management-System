package com.example.demo.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.Charger;
import com.example.demo.entity.Transaction;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.service.TransactionService;
import com.example.demo.utils.TransactionFilterRequest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;



@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
	 private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);
    private final TransactionService transactionService;
    
    private final TransactionRepository transactionRepository;

    public TransactionController(TransactionService transactionService, TransactionRepository transactionRepository) {
        this.transactionService = transactionService;
		this.transactionRepository = transactionRepository;
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        logger.info("Fetching all transactions");
        List<Transaction> transactions = transactionService.getAllTransactions();
        if (transactions.isEmpty()) {
            logger.warn("No transactions found");
            return null;
        }
        return ResponseEntity.ok(transactions);
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<?> getTransactionById(@PathVariable Long id) {
        try {
            logger.info("Fetching transaction with ID: {}", id);
            Transaction transaction = transactionService.getTransactionById(id);
            return ResponseEntity.ok(transaction);
        } catch (NoSuchElementException e) {
            logger.warn("Transaction with ID {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transaction ID " + id + " not found");
        } catch (Exception e) {
            logger.error("Error retrieving transaction {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving transaction");
        }
    }


   
    @GetMapping("/charger/{chargerId}")
    public ResponseEntity<?> getTransactionsByCharger(@PathVariable Long chargerId) {
        logger.info("Fetching transactions for charger ID: {}", chargerId);
        List<Transaction> transactions = transactionService.getTransactionsByCharger(chargerId);
        if (transactions.isEmpty()) {
            logger.warn("No transactions found for charger ID {}", chargerId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No transactions found for charger ID " + chargerId);
        }
        return ResponseEntity.ok(transactions);
    }

    
    @PostMapping("/filter")
    public ResponseEntity<List<Transaction>> findByChargerAndTimeRange(@RequestBody TransactionFilterRequest filterRequest) {
        try {
            logger.info("Filtering transactions for Charger ID: {}, Start Date: {}, Start Time: {}, End Date: {}, End Time: {}", 
                    filterRequest.getChargerId(), 
                    filterRequest.getStartDate(), filterRequest.getStartTime(), 
                    filterRequest.getEndDate(), filterRequest.getEndTime());

            List<Transaction> transactions = transactionService.findByChargerAndTimeRange(
                    filterRequest.getChargerId(),
                    filterRequest.getStartDate(), filterRequest.getStartTime(),
                    filterRequest.getEndDate(), filterRequest.getEndTime()
            );

            if (transactions.isEmpty()) {
                logger.warn("No transactions found for Charger ID {} within the given time range", filterRequest.getChargerId());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(transactions);
            }

            logger.info("Found {} transactions for Charger ID {}", transactions.size(), filterRequest.getChargerId());
            return ResponseEntity.ok(transactions);

        } catch (Exception e) {
            logger.error("Error filtering transactions: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    
    @PostMapping("/{chargerId}/start")
    public ResponseEntity<String> startTransaction(@PathVariable Long chargerId, @RequestBody Map<String, String> requestBody) {
        try {
            String sessionId = requestBody.get("sessionId");
            logger.info("Starting transaction for charger ID: {} with session ID: {}", chargerId, sessionId);
            transactionService.saveTransaction(chargerId, sessionId);
            return ResponseEntity.ok("Transaction started");
        } catch (Exception e) {
            logger.error("Error starting transaction for charger {}: {}", chargerId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to start transaction");
        }
    }


    @PostMapping("/{transactionId}/end")
    public ResponseEntity<String> endTransaction1(@PathVariable Long transactionId) {
        try {
            logger.info("Ending transaction with ID: {}", transactionId);
            Optional<Transaction> transactionOpt = transactionService.getTransactionById1(transactionId);

            if (!transactionOpt.isPresent()) {
                logger.warn("Transaction with ID {} not found", transactionId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Transaction ID " + transactionId + " not found");
            }
            transactionService.endTransaction(transactionId);
            return ResponseEntity.ok("Transaction ended");
        } catch (Exception e) {
            logger.error("Error ending transaction {}: {}", transactionId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to end transaction");
        }
    
    
    }}