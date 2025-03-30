package com.example.demo.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.demo.entity.Charger;
import com.example.demo.entity.Transaction;
import com.example.demo.repository.ChargerRepository;
import com.example.demo.repository.TransactionRepository;

import jakarta.websocket.Session;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private ChargerRepository chargerRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public void run(String... args) throws Exception {
        Charger charger = new Charger();
        charger.setChargerId(1001L);
        charger.setStatus("Available");
        charger.setLastHeartbeat(LocalDateTime.now().minusMinutes(2)); 
        charger.setPowerRating(22.5);
        chargerRepository.save(charger);
        
    
    }
}
