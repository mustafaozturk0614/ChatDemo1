package com.example.chat.entity;

import javax.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "energy_consumptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnergyConsumption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "consumption_amount", nullable = false)
    private Double consumptionAmount;

    @Column(name = "period", nullable = false)
    private String period;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDate createdAt = LocalDate.now();
} 