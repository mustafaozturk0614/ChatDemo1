-- Kullanıcılar
INSERT INTO users (first_name, last_name, email, password, created_at, updated_at, status) VALUES
                                                                                               ('Ahmet', 'Yılmaz', 'ahmet.yilmaz@example.com', 'password123', NOW(), NOW(), 'ACTIVE'),
                                                                                               ('Ayşe', 'Demir', 'ayse.demir@example.com', 'password123', NOW(), NOW(), 'ACTIVE'),
                                                                                               ('Mehmet', 'Kaya', 'mehmet.kaya@example.com', 'password123', NOW(), NOW(), 'ACTIVE');

-- Faturalar
INSERT INTO bills (user_id, bill_number, amount, due_date, status) VALUES
                                                                       (1, 'BIL-2023-001', 150.75, '2023-12-01', 'UNPAID'),
                                                                       (1, 'BIL-2023-002', 200.50, '2023-11-15', 'PAID'),
                                                                       (1, 'BIL-2023-003', 300.00, '2023-10-30', 'UNPAID'),
                                                                       (1, 'BIL-2023-009', 175.00, '2023-09-15', 'PAID'),
                                                                       (1, 'BIL-2023-010', 220.00, '2023-08-20', 'UNPAID'),
                                                                       (2, 'BIL-2023-004', 120.00, '2023-12-05', 'UNPAID'),
                                                                       (2, 'BIL-2023-005', 250.00, '2023-11-20', 'PAID'),
                                                                       (2, 'BIL-2023-011', 180.00, '2023-10-10', 'UNPAID'),
                                                                       (2, 'BIL-2023-012', 300.00, '2023-09-25', 'PAID'),
                                                                       (3, 'BIL-2023-006', 180.25, '2023-12-10', 'UNPAID'),
                                                                       (3, 'BIL-2023-007', 220.00, '2023-11-25', 'PAID'),
                                                                       (3, 'BIL-2023-008', 90.00, '2023-10-15', 'PAID'),
                                                                       (3, 'BIL-2023-013', 250.00, '2023-09-05', 'UNPAID'),
                                                                       (3, 'BIL-2023-014', 300.00, '2023-08-15', 'PAID'),

-- 2024 Faturaları
                                                                       (1, 'BIL-2024-001', 160.00, '2024-01-01', 'UNPAID'),
                                                                       (1, 'BIL-2024-002', 210.00, '2024-02-01', 'UNPAID'),
                                                                       (2, 'BIL-2024-003', 130.00, '2024-01-05', 'UNPAID'),
                                                                       (2, 'BIL-2024-004', 240.00, '2024-02-05', 'UNPAID'),
                                                                       (3, 'BIL-2024-005', 190.00, '2024-01-10', 'UNPAID'),
                                                                       (3, 'BIL-2024-006', 230.00, '2024-02-10', 'UNPAID'),

-- 2025 Faturaları
                                                                       (1, 'BIL-2025-001', 170.00, '2025-01-01', 'UNPAID'),
                                                                       (1, 'BIL-2025-002', 220.00, '2025-02-01', 'UNPAID'),
                                                                       (2, 'BIL-2025-003', 140.00, '2025-01-05', 'UNPAID'),
                                                                       (2, 'BIL-2025-004', 250.00, '2025-02-05', 'UNPAID'),
                                                                       (3, 'BIL-2025-005', 200.00, '2025-01-10', 'UNPAID'),
                                                                       (3, 'BIL-2025-006', 240.00, '2025-02-10', 'UNPAID');

-- Ödemeler
INSERT INTO payments (bill_id, user_id, amount_paid, payment_date, payment_method) VALUES
                                                                                       (2, 1, 200.50, '2023-11-10', 'CREDIT_CARD'),
                                                                                       (5, 2, 250.00, '2023-11-15', 'BANK_TRANSFER'),
                                                                                       (7, 3, 220.00, '2023-11-20', 'CASH'),
                                                                                       (1, 1, 150.75, '2023-12-01', 'CREDIT_CARD'),
                                                                                       (4, 1, 175.00, '2023-09-15', 'DEBIT_CARD'),
                                                                                       (8, 2, 300.00, '2023-09-25', 'BANK_TRANSFER'),
                                                                                       (10, 3, 300.00, '2023-08-15', 'CASH'),

-- 2024 Ödemeleri
                                                                                       (1, 1, 160.00, '2024-01-01', 'CREDIT_CARD'),
                                                                                       (2, 1, 210.00, '2024-02-01', 'DEBIT_CARD'),
                                                                                       (3, 2, 130.00, '2024-01-05', 'BANK_TRANSFER'),
                                                                                       (4, 2, 240.00, '2024-02-05', 'CASH'),
                                                                                       (5, 3, 190.00, '2024-01-10', 'CREDIT_CARD'),
                                                                                       (6, 3, 230.00, '2024-02-10', 'DEBIT_CARD'),

-- 2025 Ödemeleri
                                                                                       (1, 1, 170.00, '2025-01-01', 'CREDIT_CARD'),
                                                                                       (2, 1, 220.00, '2025-02-01', 'DEBIT_CARD'),
                                                                                       (3, 2, 140.00, '2025-01-05', 'BANK_TRANSFER'),
                                                                                       (4, 2, 250.00, '2025-02-05', 'CASH'),
                                                                                       (5, 3, 200.00, '2025-01-10', 'CREDIT_CARD'),
                                                                                       (6, 3, 240.00, '2025-02-10', 'DEBIT_CARD');

-- Abonelikler
INSERT INTO subscriptions (user_id, subscription_type, start_date, end_date, status) VALUES
                                                                                         (1, 'Basic Plan', '2023-01-01', '2024-01-01', 'ACTIVE'),
                                                                                         (2, 'Premium Plan', '2023-02-01', '2024-02-01', 'ACTIVE'),
                                                                                         (3, 'Standard Plan', '2023-03-01', '2024-03-01', 'ACTIVE'),
                                                                                         (1, 'Basic Plan', '2024-01-01', '2025-01-01', 'ACTIVE'),
                                                                                         (2, 'Premium Plan', '2024-02-01', '2025-02-01', 'ACTIVE'),
                                                                                         (3, 'Standard Plan', '2024-03-01', '2025-03-01', 'ACTIVE'),

-- 2025 Abonelikleri
                                                                                         (1, 'Basic Plan', '2025-01-01', '2026-01-01', 'ACTIVE'),
                                                                                         (2, 'Premium Plan', '2025-02-01', '2026-02-01', 'ACTIVE'),
                                                                                         (3, 'Standard Plan', '2025-03-01', '2026-03-01', 'ACTIVE');

-- Destek Talepleri
INSERT INTO support_requests (user_id, request_type, description, status, created_at) VALUES
                                                                                          (1, 'Billing Issue', 'I have a problem with my last bill.', 'OPEN', NOW()),
                                                                                          (2, 'Technical Support', 'I need help with my internet connection.', 'OPEN', NOW()),
                                                                                          (3, 'General Inquiry', 'Can you provide information about my subscription?', 'CLOSED', NOW()),
                                                                                          (1, 'Service Issue', 'My service is down.', 'OPEN', NOW()),
                                                                                          (2, 'Feedback', 'I would like to give feedback on my experience.', 'CLOSED', NOW()),
                                                                                          (3, 'Technical Support', 'I need help with my app.', 'OPEN', NOW()),
                                                                                          (1, 'Billing Inquiry', 'Can you explain my last bill?', 'OPEN', NOW()),
                                                                                          (2, 'Service Feedback', 'I want to provide feedback on the service.', 'CLOSED', NOW()),
                                                                                          (3, 'Subscription Inquiry', 'What are the benefits of my subscription?', 'OPEN', NOW());

-- Enerji Tüketimleri
INSERT INTO energy_consumptions (user_id, consumption_amount, period, created_at) VALUES
                                                                                      (1, 350.5, '2023-01', NOW()),
                                                                                      (1, 400.0, '2023-02', NOW()),
                                                                                      (1, 450.0, '2023-03', NOW()),
                                                                                      (2, 300.0, '2023-01', NOW()),
                                                                                      (2, 320.0, '2023-02', NOW()),
                                                                                      (2, 310.0, '2023-03', NOW()),
                                                                                      (3, 280.0, '2023-01', NOW()),
                                                                                      (3, 290.0, '2023-02', NOW()),
                                                                                      (3, 300.0, '2023-03', NOW()),

-- 2024 Enerji Tüketimleri
                                                                                      (1, 360.0, '2024-01', NOW()),
                                                                                      (1, 370.0, '2024-02', NOW()),
                                                                                      (2, 310.0, '2024-01', NOW()),
                                                                                      (2, 330.0, '2024-02', NOW()),
                                                                                      (3, 295.0, '2024-01', NOW()),
                                                                                      (3, 305.0, '2024-02', NOW()),

-- 2025 Enerji Tüketimleri
                                                                                      (1, 380.0, '2025-01', NOW()),
                                                                                      (1, 390.0, '2025-02', NOW()),
                                                                                      (2, 320.0, '2025-01', NOW()),
                                                                                      (2, 340.0, '2025-02', NOW()),
                                                                                      (3, 300.0, '2025-01', NOW()),
                                                                                      (3, 310.0, '2025-02', NOW());