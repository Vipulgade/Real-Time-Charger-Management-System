package com.example.demo.entity;





import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;



@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "chargers")
public class Charger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private Long chargerId;
    private String status = "Available"; // Available, Charging, Faulted
    @Column(nullable = false)
    private LocalDateTime lastHeartbeat;
   
    private double powerRating; 
    
  

    }
