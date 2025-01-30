package com.example.chat.entity;

import javax.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.example.chat.enums.SupportRequestStatus;

@Entity
@Table(name = "support_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupportRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "request_type", nullable = false)
    private String requestType;

    @Column(name = "description", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private SupportRequestStatus status = SupportRequestStatus.OPEN;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
} 