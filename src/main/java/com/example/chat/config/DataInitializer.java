package com.example.chat.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.chat.entity.Bill;
import com.example.chat.entity.EnergyConsumption;
import com.example.chat.entity.EnergySavingTip;
import com.example.chat.entity.Payment;
import com.example.chat.entity.Subscription;
import com.example.chat.entity.SupportRequest;
import com.example.chat.entity.User;
import com.example.chat.enums.BillStatus;
import com.example.chat.enums.PaymentMethod;
import com.example.chat.enums.SubscriptionStatus;
import com.example.chat.enums.SupportRequestStatus;
import com.example.chat.enums.UserStatus;
import com.example.chat.repository.BillRepository;
import com.example.chat.repository.EnergyConsumptionRepository;
import com.example.chat.repository.EnergySavingTipRepository;
import com.example.chat.repository.PaymentRepository;
import com.example.chat.repository.SubscriptionRepository;
import com.example.chat.repository.SupportRequestRepository;
import com.example.chat.repository.UserRepository;

@Component
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private EnergySavingTipRepository energySavingTipRepository;

    @Autowired
    private EnergyConsumptionRepository energyConsumptionRepository;

    @Autowired
    private SupportRequestRepository supportRequestRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        // Veritabanında veri yoksa initialize edelim.
        if (userRepository.count() == 0) {

            // --- Kullanıcılar ---
            User u1 = User.builder()
                    .firstName("Ahmet")
                    .lastName("Yılmaz")
                    .email("ahmet.yilmaz@example.com")
                    .password("password123")
                    .status(UserStatus.ACTIVE)
                    .build();

            User u2 = User.builder()
                    .firstName("Ayşe")
                    .lastName("Demir")
                    .email("ayse.demir@example.com")
                    .password("password123")
                    .status(UserStatus.ACTIVE)
                    .build();

            User u3 = User.builder()
                    .firstName("Mehmet")
                    .lastName("Kaya")
                    .email("mehmet.kaya@example.com")
                    .password("password123")
                    .status(UserStatus.ACTIVE)
                    .build();

            u1 = userRepository.save(u1);
            u2 = userRepository.save(u2);
            u3 = userRepository.save(u3);

            // --- Faturalar ---
            // 2023 Faturaları
            Bill b1 = Bill.builder()
                    .userId(u1.getId())
                    .billNumber("BIL-2023-001")
                    .amount(150.75)
                    .dueDate(LocalDate.parse("2023-12-01"))
                    .status(BillStatus.UNPAID)
                    .build();
            Bill b2 = Bill.builder()
                    .userId(u1.getId())
                    .billNumber("BIL-2023-002")
                    .amount(200.50)
                    .dueDate(LocalDate.parse("2023-11-15"))
                    .status(BillStatus.PAID)
                    .build();
            Bill b3 = Bill.builder()
                    .userId(u1.getId())
                    .billNumber("BIL-2023-003")
                    .amount(300.00)
                    .dueDate(LocalDate.parse("2023-10-30"))
                    .status(BillStatus.UNPAID)
                    .build();
            Bill b4 = Bill.builder()
                    .userId(u1.getId())
                    .billNumber("BIL-2023-009")
                    .amount(175.00)
                    .dueDate(LocalDate.parse("2023-09-15"))
                    .status(BillStatus.PAID)
                    .build();
            Bill b5 = Bill.builder()
                    .userId(u1.getId())
                    .billNumber("BIL-2023-010")
                    .amount(220.00)
                    .dueDate(LocalDate.parse("2023-08-20"))
                    .status(BillStatus.UNPAID)
                    .build();

            Bill b6 = Bill.builder()
                    .userId(u2.getId())
                    .billNumber("BIL-2023-004")
                    .amount(120.00)
                    .dueDate(LocalDate.parse("2023-12-05"))
                    .status(BillStatus.UNPAID)
                    .build();
            Bill b7 = Bill.builder()
                    .userId(u2.getId())
                    .billNumber("BIL-2023-005")
                    .amount(250.00)
                    .dueDate(LocalDate.parse("2023-11-20"))
                    .status(BillStatus.PAID)
                    .build();
            Bill b8 = Bill.builder()
                    .userId(u2.getId())
                    .billNumber("BIL-2023-011")
                    .amount(180.00)
                    .dueDate(LocalDate.parse("2023-10-10"))
                    .status(BillStatus.UNPAID)
                    .build();
            Bill b9 = Bill.builder()
                    .userId(u2.getId())
                    .billNumber("BIL-2023-012")
                    .amount(300.00)
                    .dueDate(LocalDate.parse("2023-09-25"))
                    .status(BillStatus.PAID)
                    .build();

            Bill b10 = Bill.builder()
                    .userId(u3.getId())
                    .billNumber("BIL-2023-006")
                    .amount(180.25)
                    .dueDate(LocalDate.parse("2023-12-10"))
                    .status(BillStatus.UNPAID)
                    .build();
            Bill b11 = Bill.builder()
                    .userId(u3.getId())
                    .billNumber("BIL-2023-007")
                    .amount(220.00)
                    .dueDate(LocalDate.parse("2023-11-25"))
                    .status(BillStatus.PAID)
                    .build();
            Bill b12 = Bill.builder()
                    .userId(u3.getId())
                    .billNumber("BIL-2023-008")
                    .amount(90.00)
                    .dueDate(LocalDate.parse("2023-10-15"))
                    .status(BillStatus.PAID)
                    .build();
            Bill b13 = Bill.builder()
                    .userId(u3.getId())
                    .billNumber("BIL-2023-013")
                    .amount(250.00)
                    .dueDate(LocalDate.parse("2023-09-05"))
                    .status(BillStatus.UNPAID)
                    .build();
            Bill b14 = Bill.builder()
                    .userId(u3.getId())
                    .billNumber("BIL-2023-014")
                    .amount(300.00)
                    .dueDate(LocalDate.parse("2023-08-15"))
                    .status(BillStatus.PAID)
                    .build();

            // 2024 Faturaları
            Bill b15 = Bill.builder()
                    .userId(u1.getId())
                    .billNumber("BIL-2024-001")
                    .amount(160.00)
                    .dueDate(LocalDate.parse("2024-01-01"))
                    .status(BillStatus.UNPAID)
                    .build();
            Bill b16 = Bill.builder()
                    .userId(u1.getId())
                    .billNumber("BIL-2024-002")
                    .amount(210.00)
                    .dueDate(LocalDate.parse("2024-02-01"))
                    .status(BillStatus.UNPAID)
                    .build();
            Bill b17 = Bill.builder()
                    .userId(u2.getId())
                    .billNumber("BIL-2024-003")
                    .amount(130.00)
                    .dueDate(LocalDate.parse("2024-01-05"))
                    .status(BillStatus.UNPAID)
                    .build();
            Bill b18 = Bill.builder()
                    .userId(u2.getId())
                    .billNumber("BIL-2024-004")
                    .amount(240.00)
                    .dueDate(LocalDate.parse("2024-02-05"))
                    .status(BillStatus.UNPAID)
                    .build();
            Bill b19 = Bill.builder()
                    .userId(u3.getId())
                    .billNumber("BIL-2024-005")
                    .amount(190.00)
                    .dueDate(LocalDate.parse("2024-01-10"))
                    .status(BillStatus.UNPAID)
                    .build();
            Bill b20 = Bill.builder()
                    .userId(u3.getId())
                    .billNumber("BIL-2024-006")
                    .amount(230.00)
                    .dueDate(LocalDate.parse("2024-02-10"))
                    .status(BillStatus.UNPAID)
                    .build();

            // 2025 Faturaları
            Bill b21 = Bill.builder()
                    .userId(u1.getId())
                    .billNumber("BIL-2025-001")
                    .amount(170.00)
                    .dueDate(LocalDate.parse("2025-01-01"))
                    .status(BillStatus.UNPAID)
                    .build();
            Bill b22 = Bill.builder()
                    .userId(u1.getId())
                    .billNumber("BIL-2025-002")
                    .amount(220.00)
                    .dueDate(LocalDate.parse("2025-02-01"))
                    .status(BillStatus.UNPAID)
                    .build();
            Bill b23 = Bill.builder()
                    .userId(u2.getId())
                    .billNumber("BIL-2025-003")
                    .amount(140.00)
                    .dueDate(LocalDate.parse("2025-01-05"))
                    .status(BillStatus.UNPAID)
                    .build();
            Bill b24 = Bill.builder()
                    .userId(u2.getId())
                    .billNumber("BIL-2025-004")
                    .amount(250.00)
                    .dueDate(LocalDate.parse("2025-02-05"))
                    .status(BillStatus.UNPAID)
                    .build();
            Bill b25 = Bill.builder()
                    .userId(u3.getId())
                    .billNumber("BIL-2025-005")
                    .amount(200.00)
                    .dueDate(LocalDate.parse("2025-01-10"))
                    .status(BillStatus.UNPAID)
                    .build();
            Bill b26 = Bill.builder()
                    .userId(u3.getId())
                    .billNumber("BIL-2025-006")
                    .amount(240.00)
                    .dueDate(LocalDate.parse("2025-02-10"))
                    .status(BillStatus.UNPAID)
                    .build();

            List<Bill> bills = Arrays.asList(
                    b1, b2, b3, b4, b5,
                    b6, b7, b8, b9,
                    b10, b11, b12, b13, b14,
                    b15, b16, b17, b18, b19, b20,
                    b21, b22, b23, b24, b25, b26
            );
            billRepository.saveAll(bills);

            // --- Ödemeler ---
            // 2023 Ödemeleri
            Payment p1 = Payment.builder()
                    .billId(b2.getId())
                    .userId(u1.getId())
                    .amountPaid(200.50)
                    .paymentDate(LocalDate.parse("2023-11-10").atStartOfDay())
                    .paymentMethod(PaymentMethod.CREDIT_CARD)
                    .build();
            Payment p2 = Payment.builder()
                    .billId(b5.getId())
                    .userId(u2.getId())
                    .amountPaid(250.00)
                    .paymentDate(LocalDate.parse("2023-11-15").atStartOfDay())
                    .paymentMethod(PaymentMethod.BANK_TRANSFER)
                    .build();
            Payment p3 = Payment.builder()
                    .billId(b7.getId())
                    .userId(u3.getId())
                    .amountPaid(220.00)
                    .paymentDate(LocalDate.parse("2023-11-20").atStartOfDay())
                    .paymentMethod(PaymentMethod.CASH)
                    .build();
            Payment p4 = Payment.builder()
                    .billId(b1.getId())
                    .userId(u1.getId())
                    .amountPaid(150.75)
                    .paymentDate(LocalDate.parse("2023-12-01").atStartOfDay())
                    .paymentMethod(PaymentMethod.CREDIT_CARD)
                    .build();
            Payment p5 = Payment.builder()
                    .billId(b4.getId())
                    .userId(u1.getId())
                    .amountPaid(175.00)
                    .paymentDate(LocalDate.parse("2023-09-15").atStartOfDay())
                    .paymentMethod(PaymentMethod.DEBIT_CARD)
                    .build();
            Payment p6 = Payment.builder()
                    .billId(b8.getId())
                    .userId(u2.getId())
                    .amountPaid(300.00)
                    .paymentDate(LocalDate.parse("2023-09-25").atStartOfDay())
                    .paymentMethod(PaymentMethod.BANK_TRANSFER)
                    .build();
            Payment p7 = Payment.builder()
                    .billId(b10.getId())
                    .userId(u3.getId())
                    .amountPaid(300.00)
                    .paymentDate(LocalDate.parse("2023-08-15").atStartOfDay())
                    .paymentMethod(PaymentMethod.CASH)
                    .build();

            // 2024 Ödemeleri
            Payment p8 = Payment.builder()
                    .billId(b15.getId())
                    .userId(u1.getId())
                    .amountPaid(160.00)
                    .paymentDate(LocalDate.parse("2024-01-01").atStartOfDay())
                    .paymentMethod(PaymentMethod.CREDIT_CARD)
                    .build();
            Payment p9 = Payment.builder()
                    .billId(b16.getId())
                    .userId(u1.getId())
                    .amountPaid(210.00)
                    .paymentDate(LocalDate.parse("2024-02-01").atStartOfDay())
                    .paymentMethod(PaymentMethod.DEBIT_CARD)
                    .build();
            Payment p10 = Payment.builder()
                    .billId(b17.getId())
                    .userId(u2.getId())
                    .amountPaid(130.00)
                    .paymentDate(LocalDate.parse("2024-01-05").atStartOfDay())
                    .paymentMethod(PaymentMethod.BANK_TRANSFER)
                    .build();
            Payment p11 = Payment.builder()
                    .billId(b18.getId())
                    .userId(u2.getId())
                    .amountPaid(240.00)
                    .paymentDate(LocalDate.parse("2024-02-05").atStartOfDay())
                    .paymentMethod(PaymentMethod.CASH)
                    .build();
            Payment p12 = Payment.builder()
                    .billId(b19.getId())
                    .userId(u3.getId())
                    .amountPaid(190.00)
                    .paymentDate(LocalDate.parse("2024-01-10").atStartOfDay())
                    .paymentMethod(PaymentMethod.CREDIT_CARD)
                    .build();
            Payment p13 = Payment.builder()
                    .billId(b20.getId())
                    .userId(u3.getId())
                    .amountPaid(230.00)
                    .paymentDate(LocalDate.parse("2024-02-10").atStartOfDay())
                    .paymentMethod(PaymentMethod.DEBIT_CARD)
                    .build();

            // 2025 Ödemeleri
            Payment p14 = Payment.builder()
                    .billId(b21.getId())
                    .userId(u1.getId())
                    .amountPaid(170.00)
                    .paymentDate(LocalDate.parse("2025-01-01").atStartOfDay())
                    .paymentMethod(PaymentMethod.CREDIT_CARD)
                    .build();
            Payment p15 = Payment.builder()
                    .billId(b22.getId())
                    .userId(u1.getId())
                    .amountPaid(220.00)
                    .paymentDate(LocalDate.parse("2025-02-01").atStartOfDay())
                    .paymentMethod(PaymentMethod.DEBIT_CARD)
                    .build();
            Payment p16 = Payment.builder()
                    .billId(b23.getId())
                    .userId(u2.getId())
                    .amountPaid(140.00)
                    .paymentDate(LocalDate.parse("2025-01-05").atStartOfDay())
                    .paymentMethod(PaymentMethod.BANK_TRANSFER)
                    .build();
            Payment p17 = Payment.builder()
                    .billId(b24.getId())
                    .userId(u2.getId())
                    .amountPaid(250.00)
                    .paymentDate(LocalDate.parse("2025-02-05").atStartOfDay())
                    .paymentMethod(PaymentMethod.CASH)
                    .build();
            Payment p18 = Payment.builder()
                    .billId(b25.getId())
                    .userId(u3.getId())
                    .amountPaid(200.00)
                    .paymentDate(LocalDate.parse("2025-01-10").atStartOfDay())
                    .paymentMethod(PaymentMethod.CREDIT_CARD)
                    .build();
            Payment p19 = Payment.builder()
                    .billId(b26.getId())
                    .userId(u3.getId())
                    .amountPaid(240.00)
                    .paymentDate(LocalDate.parse("2025-02-10").atStartOfDay())
                    .paymentMethod(PaymentMethod.DEBIT_CARD)
                    .build();

            List<Payment> payments = Arrays.asList(
                    p1, p2, p3, p4, p5, p6, p7,
                    p8, p9, p10, p11, p12, p13,
                    p14, p15, p16, p17, p18, p19
            );
            paymentRepository.saveAll(payments);

            // --- Abonelikler ---
            Subscription s1 = Subscription.builder()
                    .userId(u1.getId())
                    .subscriptionType("Basic Plan")
                    .startDate(LocalDate.parse("2023-01-01"))
                    .endDate(LocalDate.parse("2024-01-01"))
                    .status(SubscriptionStatus.ACTIVE)
                    .build();
            Subscription s2 = Subscription.builder()
                    .userId(u2.getId())
                    .subscriptionType("Premium Plan")
                    .startDate(LocalDate.parse("2023-02-01"))
                    .endDate(LocalDate.parse("2024-02-01"))
                    .status(SubscriptionStatus.ACTIVE)
                    .build();
            Subscription s3 = Subscription.builder()
                    .userId(u3.getId())
                    .subscriptionType("Standard Plan")
                    .startDate(LocalDate.parse("2023-03-01"))
                    .endDate(LocalDate.parse("2024-03-01"))
                    .status(SubscriptionStatus.ACTIVE)
                    .build();
            Subscription s4 = Subscription.builder()
                    .userId(u1.getId())
                    .subscriptionType("Basic Plan")
                    .startDate(LocalDate.parse("2024-01-01"))
                    .endDate(LocalDate.parse("2025-01-01"))
                    .status(SubscriptionStatus.ACTIVE)
                    .build();
            Subscription s5 = Subscription.builder()
                    .userId(u2.getId())
                    .subscriptionType("Premium Plan")
                    .startDate(LocalDate.parse("2024-02-01"))
                    .endDate(LocalDate.parse("2025-02-01"))
                    .status(SubscriptionStatus.ACTIVE)
                    .build();
            Subscription s6 = Subscription.builder()
                    .userId(u3.getId())
                    .subscriptionType("Standard Plan")
                    .startDate(LocalDate.parse("2024-03-01"))
                    .endDate(LocalDate.parse("2025-03-01"))
                    .status(SubscriptionStatus.ACTIVE)
                    .build();
            Subscription s7 = Subscription.builder()
                    .userId(u1.getId())
                    .subscriptionType("Basic Plan")
                    .startDate(LocalDate.parse("2025-01-01"))
                    .endDate(LocalDate.parse("2026-01-01"))
                    .status(SubscriptionStatus.ACTIVE)
                    .build();
            Subscription s8 = Subscription.builder()
                    .userId(u2.getId())
                    .subscriptionType("Premium Plan")
                    .startDate(LocalDate.parse("2025-02-01"))
                    .endDate(LocalDate.parse("2026-02-01"))
                    .status(SubscriptionStatus.ACTIVE)
                    .build();
            Subscription s9 = Subscription.builder()
                    .userId(u3.getId())
                    .subscriptionType("Standard Plan")
                    .startDate(LocalDate.parse("2025-03-01"))
                    .endDate(LocalDate.parse("2026-03-01"))
                    .status(SubscriptionStatus.ACTIVE)
                    .build();

            List<Subscription> subscriptions = Arrays.asList(s1, s2, s3, s4, s5, s6, s7, s8, s9);
            subscriptionRepository.saveAll(subscriptions);

            // --- Destek Talepleri ---
            SupportRequest sr1 = SupportRequest.builder()
                    .userId(u1.getId())
                    .requestType("Billing Issue")
                    .description("I have a problem with my last bill.")
                    .status(SupportRequestStatus.OPEN)
                    .createdAt(LocalDateTime.now())
                    .build();
            SupportRequest sr2 = SupportRequest.builder()
                    .userId(u2.getId())
                    .requestType("Technical Support")
                    .description("I need help with my internet connection.")
                    .status(SupportRequestStatus.OPEN)
                    .createdAt(LocalDateTime.now())
                    .build();
            SupportRequest sr3 = SupportRequest.builder()
                    .userId(u3.getId())
                    .requestType("General Inquiry")
                    .description("Can you provide information about my subscription?")
                    .status(SupportRequestStatus.CLOSED)
                    .createdAt(LocalDateTime.now())
                    .build();
            SupportRequest sr4 = SupportRequest.builder()
                    .userId(u1.getId())
                    .requestType("Service Issue")
                    .description("My service is down.")
                    .status(SupportRequestStatus.OPEN)
                    .createdAt(LocalDateTime.now())
                    .build();
            SupportRequest sr5 = SupportRequest.builder()
                    .userId(u2.getId())
                    .requestType("Feedback")
                    .description("I would like to give feedback on my experience.")
                    .status(SupportRequestStatus.CLOSED)
                    .createdAt(LocalDateTime.now())
                    .build();
            SupportRequest sr6 = SupportRequest.builder()
                    .userId(u3.getId())
                    .requestType("Technical Support")
                    .description("I need help with my app.")
                    .status(SupportRequestStatus.OPEN)
                    .createdAt(LocalDateTime.now())
                    .build();
            SupportRequest sr7 = SupportRequest.builder()
                    .userId(u1.getId())
                    .requestType("Billing Inquiry")
                    .description("Can you explain my last bill?")
                    .status(SupportRequestStatus.OPEN)
                    .createdAt(LocalDateTime.now())
                    .build();
            SupportRequest sr8 = SupportRequest.builder()
                    .userId(u2.getId())
                    .requestType("Service Feedback")
                    .description("I want to provide feedback on the service.")
                    .status(SupportRequestStatus.CLOSED)
                    .createdAt(LocalDateTime.now())
                    .build();
            SupportRequest sr9 = SupportRequest.builder()
                    .userId(u3.getId())
                    .requestType("Subscription Inquiry")
                    .description("What are the benefits of my subscription?")
                    .status(SupportRequestStatus.OPEN)
                    .createdAt(LocalDateTime.now())
                    .build();

            List<SupportRequest> supportRequests = Arrays.asList(sr1, sr2, sr3, sr4, sr5, sr6, sr7, sr8, sr9);
            supportRequestRepository.saveAll(supportRequests);

            // --- Enerji Tüketimleri ---
            // 2023 Enerji Tüketimleri
            EnergyConsumption ec1 = EnergyConsumption.builder()
                    .userId(u1.getId())
                    .consumptionAmount(350.5)
                    .period("2023-01")
                    .createdAt(LocalDate.parse("2023-01-01"))
                    .build();
            EnergyConsumption ec2 = EnergyConsumption.builder()
                    .userId(u1.getId())
                    .consumptionAmount(400.0)
                    .period("2023-02")
                    .createdAt(LocalDate.parse("2023-02-01"))
                    .build();
            EnergyConsumption ec3 = EnergyConsumption.builder()
                    .userId(u1.getId())
                    .consumptionAmount(450.0)
                    .period("2023-03")
                    .createdAt(LocalDate.parse("2023-03-01"))
                    .build();
            EnergyConsumption ec4 = EnergyConsumption.builder()
                    .userId(u2.getId())
                    .consumptionAmount(300.0)
                    .period("2023-01")
                    .createdAt(LocalDate.parse("2023-01-01"))
                    .build();
            EnergyConsumption ec5 = EnergyConsumption.builder()
                    .userId(u2.getId())
                    .consumptionAmount(320.0)
                    .period("2023-02")
                    .createdAt(LocalDate.parse("2023-02-01"))
                    .build();
            EnergyConsumption ec6 = EnergyConsumption.builder()
                    .userId(u2.getId())
                    .consumptionAmount(310.0)
                    .period("2023-03")
                    .createdAt(LocalDate.parse("2023-03-01"))
                    .build();
            EnergyConsumption ec7 = EnergyConsumption.builder()
                    .userId(u3.getId())
                    .consumptionAmount(280.0)
                    .period("2023-01")
                    .createdAt(LocalDate.parse("2023-01-01"))
                    .build();
            EnergyConsumption ec8 = EnergyConsumption.builder()
                    .userId(u3.getId())
                    .consumptionAmount(290.0)
                    .period("2023-02")
                    .createdAt(LocalDate.parse("2023-02-01"))
                    .build();
            EnergyConsumption ec9 = EnergyConsumption.builder()
                    .userId(u3.getId())
                    .consumptionAmount(300.0)
                    .period("2023-03")
                    .createdAt(LocalDate.parse("2023-03-01"))
                    .build();

            // 2024 Enerji Tüketimleri
            EnergyConsumption ec10 = EnergyConsumption.builder()
                    .userId(u1.getId())
                    .consumptionAmount(360.0)
                    .period("2024-01")
                    .createdAt(LocalDate.parse("2024-01-01"))
                    .build();
            EnergyConsumption ec11 = EnergyConsumption.builder()
                    .userId(u1.getId())
                    .consumptionAmount(370.0)
                    .period("2024-02")
                    .createdAt(LocalDate.parse("2024-02-01"))
                    .build();
            EnergyConsumption ec12 = EnergyConsumption.builder()
                    .userId(u2.getId())
                    .consumptionAmount(310.0)
                    .period("2024-01")
                    .createdAt(LocalDate.parse("2024-01-01"))
                    .build();
            EnergyConsumption ec13 = EnergyConsumption.builder()
                    .userId(u2.getId())
                    .consumptionAmount(330.0)
                    .period("2024-02")
                    .createdAt(LocalDate.parse("2024-02-01"))
                    .build();
            EnergyConsumption ec14 = EnergyConsumption.builder()
                    .userId(u3.getId())
                    .consumptionAmount(295.0)
                    .period("2024-01")
                    .createdAt(LocalDate.parse("2024-01-01"))
                    .build();
            EnergyConsumption ec15 = EnergyConsumption.builder()
                    .userId(u3.getId())
                    .consumptionAmount(305.0)
                    .period("2024-02")
                    .createdAt(LocalDate.parse("2024-02-01"))
                    .build();

            // 2025 Enerji Tüketimleri
            EnergyConsumption ec16 = EnergyConsumption.builder()
                    .userId(u1.getId())
                    .consumptionAmount(380.0)
                    .period("2025-01")
                    .createdAt(LocalDate.parse("2025-01-01"))
                    .build();
            EnergyConsumption ec17 = EnergyConsumption.builder()
                    .userId(u1.getId())
                    .consumptionAmount(390.0)
                    .period("2025-02")
                    .createdAt(LocalDate.parse("2025-02-01"))
                    .build();
            EnergyConsumption ec18 = EnergyConsumption.builder()
                    .userId(u2.getId())
                    .consumptionAmount(320.0)
                    .period("2025-01")
                    .createdAt(LocalDate.parse("2025-01-01"))
                    .build();
            EnergyConsumption ec19 = EnergyConsumption.builder()
                    .userId(u2.getId())
                    .consumptionAmount(340.0)
                    .period("2025-02")
                    .createdAt(LocalDate.parse("2025-02-01"))
                    .build();
            EnergyConsumption ec20 = EnergyConsumption.builder()
                    .userId(u3.getId())
                    .consumptionAmount(300.0)
                    .period("2025-01")
                    .createdAt(LocalDate.parse("2025-01-01"))
                    .build();
            EnergyConsumption ec21 = EnergyConsumption.builder()
                    .userId(u3.getId())
                    .consumptionAmount(310.0)
                    .period("2025-02")
                    .createdAt(LocalDate.parse("2025-02-01"))
                    .build();

            List<EnergyConsumption> consumptions = Arrays.asList(
                    ec1, ec2, ec3, ec4, ec5, ec6, ec7, ec8, ec9,
                    ec10, ec11, ec12, ec13, ec14, ec15,
                    ec16, ec17, ec18, ec19, ec20, ec21
            );
            energyConsumptionRepository.saveAll(consumptions);

            // --- Enerji Tasarruf İpuçları ---
            // (SQL'de mevcut olmasa da örnek olarak ekleyelim)
            EnergySavingTip tip1 = EnergySavingTip.builder()
                    .title("Enerji Tasarrufu Sağlayın")
                    .description("Elektrikli cihazlarınızı kullanmadığınız zaman kapatın.")
                    .category("Genel")
                    .createdAt(LocalDateTime.now())
                    .build();
            EnergySavingTip tip2 = EnergySavingTip.builder()
                    .title("LED Ampul Kullanın")
                    .description("Geleneksel ampuller yerine LED ampuller kullanın, daha az enerji tüketir.")
                    .category("Aydınlatma")
                    .createdAt(LocalDateTime.now())
                    .build();
            energySavingTipRepository.saveAll(Arrays.asList(tip1, tip2));

            // Veritabanı initialize işlemi tamamlandı.
        }
    }
} 