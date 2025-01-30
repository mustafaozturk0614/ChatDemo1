package com.example.chat.entity;

import javax.persistence.*;
import lombok.*;
import java.time.LocalDate;
import com.example.chat.enums.BillStatus;

@Entity
@Table(name = "bills")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "bill_number", nullable = false, unique = true)
    private String billNumber;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private BillStatus status = BillStatus.UNPAID;
} 