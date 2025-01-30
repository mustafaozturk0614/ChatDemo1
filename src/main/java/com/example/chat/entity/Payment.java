package com.example.chat.entity;

import javax.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.example.chat.enums.PaymentMethod;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bill_id", nullable = false)
    private Long billId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "amount_paid", nullable = false)
    private Double amountPaid;

    @Column(name = "payment_date", nullable = false)
    @Builder.Default
    private LocalDateTime paymentDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    @Builder.Default
    private PaymentMethod paymentMethod = PaymentMethod.CASH; // Varsayılan ödeme yöntemi
} 