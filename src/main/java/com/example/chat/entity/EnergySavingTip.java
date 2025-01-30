package com.example.chat.entity;

import javax.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "energy_saving_tips")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnergySavingTip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
} 