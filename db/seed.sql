-- ============================================================
-- Zim Mobile Money - Flyway Seed Migration
-- Version: V2
-- Run after: V1__init.sql
-- All seeded users have PIN: 1234
-- ============================================================

-- ---------------------------------------------------------------
-- 1. USERS
-- ---------------------------------------------------------------
INSERT INTO users (user_id, phone_number, pin_hash, first_name, last_name, national_id, kyc_level, user_type, status, created_at, updated_at) VALUES
('1cc789c1-c2e4-48dc-b6e2-9f5ad8ed01f3', '0772000000', '$2b$12$u79IFG5Uj9Q0x41OCIivGeJwi9uNoyi4DlGoLm5wB468ZtiVG1Ylm', 'Admin', 'User', '00-000000A00', 3, 'ADMIN', 'ACTIVE', '2026-07-28 06:00:00', '2026-07-28 06:00:00'),
('a8d7a411-c219-4b23-a247-392a0b600a4f', '0772000001', '$2b$12$u79IFG5Uj9Q0x41OCIivGeJwi9uNoyi4DlGoLm5wB468ZtiVG1Ylm', 'John', 'Moyo', '63-1234567A89', 2, 'AGENT', 'ACTIVE', '2026-07-28 06:05:00', '2026-07-28 06:05:00'),
('c20c7c3d-68a2-4568-8cd0-f345bd23dfc5', '0772000002', '$2b$12$u79IFG5Uj9Q0x41OCIivGeJwi9uNoyi4DlGoLm5wB468ZtiVG1Ylm', 'Sarah', 'Dube', '08-9876543B21', 2, 'AGENT', 'ACTIVE', '2026-07-28 06:10:00', '2026-07-28 06:10:00'),
('26de6380-a95b-414f-9cbd-51462b80120f', '0772123456', '$2b$12$u79IFG5Uj9Q0x41OCIivGeJwi9uNoyi4DlGoLm5wB468ZtiVG1Ylm', 'Tendai', 'Chiwenga', '75-4567890C12', 1, 'CUSTOMER', 'ACTIVE', '2026-07-28 06:15:00', '2026-07-28 06:15:00'),
('528f2ce5-6f94-4083-804a-3b9c1460805c', '0772987654', '$2b$12$u79IFG5Uj9Q0x41OCIivGeJwi9uNoyi4DlGoLm5wB468ZtiVG1Ylm', 'Privilege', 'Mutasa', '42-1122334D56', 1, 'CUSTOMER', 'ACTIVE', '2026-07-28 06:20:00', '2026-07-28 06:20:00'),
('0e9ac940-0829-4294-9637-6ea5324a61b6', '0772333444', '$2b$12$u79IFG5Uj9Q0x41OCIivGeJwi9uNoyi4DlGoLm5wB468ZtiVG1Ylm', 'GreenGrocer', 'Pvt Ltd', '12-9998887E55', 2, 'MERCHANT', 'ACTIVE', '2026-07-28 06:25:00', '2026-07-28 06:25:00');


-- ---------------------------------------------------------------
-- 2. WALLETS (final balances reflect seeded transaction history)
-- ---------------------------------------------------------------
INSERT INTO wallets (wallet_id, user_id, currency_code, balance, daily_limit, monthly_limit, is_active, created_at, version) VALUES
-- Admin
('87ac0365-9d0e-4ff9-943e-b286066bd1aa', '1cc789c1-c2e4-48dc-b6e2-9f5ad8ed01f3', 'USD', 5000.00, 5000.00, 50000.00, true, '2026-07-28 06:00:00', 0),
('99d60e62-d3d2-4fee-881b-fba6274922ad', '1cc789c1-c2e4-48dc-b6e2-9f5ad8ed01f3', 'ZIG', 50000.00, 50000.00, 500000.00, true, '2026-07-28 06:00:00', 0),
-- Agent 1
('84513d5d-20a3-48fb-b3c3-b63435c4efb5', 'a8d7a411-c219-4b23-a247-392a0b600a4f', 'USD', 10000.00, 5000.00, 50000.00, true, '2026-07-28 06:05:00', 0),
('45aa8201-f448-4087-afba-f791ea29d418', 'a8d7a411-c219-4b23-a247-392a0b600a4f', 'ZIG', 100000.00, 50000.00, 500000.00, true, '2026-07-28 06:05:00', 0),
-- Agent 2
('46a2535b-3781-4c5e-b6e7-b5c02e285c8b', 'c20c7c3d-68a2-4568-8cd0-f345bd23dfc5', 'USD', 8000.00, 5000.00, 50000.00, true, '2026-07-28 06:10:00', 0),
('b197943e-578c-4c4c-9c83-9e6ec61e81c1', 'c20c7c3d-68a2-4568-8cd0-f345bd23dfc5', 'ZIG', 80000.00, 50000.00, 500000.00, true, '2026-07-28 06:10:00', 0),
-- Customer 1
('2febf3ea-ad7c-4b2f-a1f9-901324095b04', '26de6380-a95b-414f-9cbd-51462b80120f', 'USD', 299.75, 5000.00, 50000.00, true, '2026-07-28 06:15:00', 0),
('1b971888-b47c-4c8e-a7d2-fa7a3daea1bc', '26de6380-a95b-414f-9cbd-51462b80120f', 'ZIG', 3000.00, 5000.00, 50000.00, true, '2026-07-28 06:15:00', 0),
-- Customer 2
('85d5f3b5-73c4-4287-9501-26ab2d9418f6', '528f2ce5-6f94-4083-804a-3b9c1460805c', 'USD', 139.50, 5000.00, 50000.00, true, '2026-07-28 06:20:00', 0),
('70741f96-9e90-427b-b724-1474006ee99b', '528f2ce5-6f94-4083-804a-3b9c1460805c', 'ZIG', 1500.00, 5000.00, 50000.00, true, '2026-07-28 06:20:00', 0),
-- Merchant 1
('7cebbf56-cf84-4429-9963-fd03682ab52c', '0e9ac940-0829-4294-9637-6ea5324a61b6', 'USD', 5000.00, 10000.00, 100000.00, true, '2026-07-28 06:25:00', 0),
('dae4333e-835e-451a-b197-e0ab5c7378d8', '0e9ac940-0829-4294-9637-6ea5324a61b6', 'ZIG', 50000.00, 10000.00, 100000.00, true, '2026-07-28 06:25:00', 0);


-- ---------------------------------------------------------------
-- 3. AGENTS
-- ---------------------------------------------------------------
INSERT INTO agents (agent_id, user_id, agent_code, business_name, territory, commission_rate, float_balance, status, created_at) VALUES
('dabb2ca4-3d1a-4ee6-9b7a-7750618f52b0', 'a8d7a411-c219-4b23-a247-392a0b600a4f', 'AGT001', 'Moyo Mobile Money', 'Harare', 0.0100, 5400.00, 'ACTIVE', '2026-07-28 06:05:00'),
('9d341594-1a4f-4ede-a762-98e6fd99006d', 'c20c7c3d-68a2-4568-8cd0-f345bd23dfc5', 'AGT002', 'Dube Express', 'Bulawayo', 0.0100, 3030.00, 'ACTIVE', '2026-07-28 06:10:00');


-- ---------------------------------------------------------------
-- 4. MERCHANTS
-- ---------------------------------------------------------------
INSERT INTO merchants (merchant_id, user_id, business_name, settlement_wallet_id, webhook_url, status, created_at) VALUES
('66334e15-48e7-46fa-83d7-a76ad85d42f8', '0e9ac940-0829-4294-9637-6ea5324a61b6', 'GreenGrocer Pvt Ltd', '7cebbf56-cf84-4429-9963-fd03682ab52c', 'https://greengrocer.co.zw/webhooks/momo', 'ACTIVE', '2026-07-28 06:25:00');


-- ---------------------------------------------------------------
-- 5. TRANSACTIONS
-- ---------------------------------------------------------------
INSERT INTO transactions (transaction_id, reference_number, idempotency_key, transaction_type, status, amount, fee_amount, agent_commission, currency_code, sender_wallet_id, receiver_wallet_id, agent_id, source_channel, description, created_at, completed_at) VALUES
-- Cash In: Customer 1 +$100 via Agent 1
('4e36cb0f-ada0-45a9-875a-91450fa3ebe8', 'TXN-SEED01', 'seed-cashin-001', 'CASH_IN', 'COMPLETED', 100.00, 0.00, 0.00, 'USD', NULL, '2febf3ea-ad7c-4b2f-a1f9-901324095b04', 'dabb2ca4-3d1a-4ee6-9b7a-7750618f52b0', 'AGENT_APP', 'Cash-in at Moyo Mobile Money', '2026-07-28 08:00:00', '2026-07-28 08:00:01'),
-- Transfer: Customer 1 -> Customer 2, $50
('63b3dfe4-d483-47c0-afef-76f4f77355e7', 'TXN-SEED02', 'seed-transfer-001', 'TRANSFER', 'COMPLETED', 50.00, 0.25, 0.00, 'USD', '2febf3ea-ad7c-4b2f-a1f9-901324095b04', '85d5f3b5-73c4-4287-9501-26ab2d9418f6', NULL, 'MOBILE_APP', 'Lunch money', '2026-07-28 09:00:00', '2026-07-28 09:00:01'),
-- Cash Out: Customer 2 -$30 via Agent 2
('a69e1d9f-eabf-41fa-8ae1-393febd9c3a5', 'TXN-SEED03', 'seed-cashout-001', 'CASH_OUT', 'COMPLETED', 30.00, 0.50, 0.00, 'USD', '85d5f3b5-73c4-4287-9501-26ab2d9418f6', NULL, '9d341594-1a4f-4ede-a762-98e6fd99006d', 'AGENT_APP', 'Cash-out at Dube Express', '2026-07-28 10:00:00', '2026-07-28 10:00:01');


-- ---------------------------------------------------------------
-- 6. LEDGER ENTRIES (Double-Entry Bookkeeping)
-- ---------------------------------------------------------------
INSERT INTO ledger_entries (entry_id, transaction_id, wallet_id, entry_type, amount, running_balance, description, created_at) VALUES
-- Cash In: Credit to Customer 1
('4fc1ac29-cebf-4c34-805e-452ba525c94c', '4e36cb0f-ada0-45a9-875a-91450fa3ebe8', '2febf3ea-ad7c-4b2f-a1f9-901324095b04', 'CREDIT', 100.00, 350.00, 'Credit: Cash-in via agent AGT001', '2026-07-28 08:00:01'),
-- Transfer: Debit from Customer 1
('9263041a-49f1-4a83-96df-6e7940a73e6c', '63b3dfe4-d483-47c0-afef-76f4f77355e7', '2febf3ea-ad7c-4b2f-a1f9-901324095b04', 'DEBIT', 50.25, 299.75, 'Debit: Lunch money', '2026-07-28 09:00:01'),
-- Transfer: Credit to Customer 2
('0fd5ebf8-2b15-44ae-8450-ccd2dc0ab74c', '63b3dfe4-d483-47c0-afef-76f4f77355e7', '85d5f3b5-73c4-4287-9501-26ab2d9418f6', 'CREDIT', 50.00, 170.00, 'Credit: Lunch money', '2026-07-28 09:00:01'),
-- Cash Out: Debit from Customer 2
('2902af0c-06e0-4664-b468-75a297820694', 'a69e1d9f-eabf-41fa-8ae1-393febd9c3a5', '85d5f3b5-73c4-4287-9501-26ab2d9418f6', 'DEBIT', 30.50, 139.50, 'Debit: Cash-out via agent AGT002', '2026-07-28 10:00:01');


-- ---------------------------------------------------------------
-- 7. AGENT FLOAT LOGS
-- ---------------------------------------------------------------
INSERT INTO agent_float_logs (log_id, agent_id, transaction_id, movement_type, amount, running_float, created_at) VALUES
-- Cash In: Agent 1 float decreased by 100
('3ef786b8-e373-45d6-845f-fe7290045870', 'dabb2ca4-3d1a-4ee6-9b7a-7750618f52b0', '4e36cb0f-ada0-45a9-875a-91450fa3ebe8', 'CASH_IN', -100.00, 4900.00, '2026-07-28 08:00:01'),
-- Cash Out: Agent 2 float increased by 30
('b262577a-2a27-4bff-b292-4563a0fc6c11', '9d341594-1a4f-4ede-a762-98e6fd99006d', 'a69e1d9f-eabf-41fa-8ae1-393febd9c3a5', 'CASH_OUT', 30.00, 3030.00, '2026-07-28 10:00:01'),
-- Top Up: Admin topped up Agent 1 float by 500
('a354791e-5621-4747-8b10-65854b76f743', 'dabb2ca4-3d1a-4ee6-9b7a-7750618f52b0', NULL, 'TOP_UP', 500.00, 5400.00, '2026-07-28 11:00:00');


-- ---------------------------------------------------------------
-- 8. AUDIT LOGS
-- ---------------------------------------------------------------
INSERT INTO audit_logs (user_id, action, details, ip_address, created_at) VALUES
('1cc789c1-c2e4-48dc-b6e2-9f5ad8ed01f3', 'USER_REGISTERED', 'Admin user seeded', '127.0.0.1', '2026-07-28 06:00:00'),
('a8d7a411-c219-4b23-a247-392a0b600a4f', 'AGENT_ONBOARDED', 'Agent John Moyo onboarded with code AGT001', '127.0.0.1', '2026-07-28 06:05:00'),
('a8d7a411-c219-4b23-a247-392a0b600a4f', 'CASH_IN_COMPLETED', 'Cash-in 100.00 USD for 0772123456', '192.168.1.10', '2026-07-28 08:00:01'),
('26de6380-a95b-414f-9cbd-51462b80120f', 'TRANSFER_COMPLETED', 'Transferred 50.00 USD to 0772987654', '192.168.1.20', '2026-07-28 09:00:01'),
('c20c7c3d-68a2-4568-8cd0-f345bd23dfc5', 'CASH_OUT_COMPLETED', 'Cash-out 30.00 USD for 0772987654', '192.168.1.30', '2026-07-28 10:00:01'),
('1cc789c1-c2e4-48dc-b6e2-9f5ad8ed01f3', 'AGENT_FLOAT_TOPUP', 'Topped up agent AGT001 with 500.00 USD', '127.0.0.1', '2026-07-28 11:00:00');


-- ---------------------------------------------------------------
-- 9. IDEMPOTENCY KEYS
-- ---------------------------------------------------------------
INSERT INTO idempotency_keys (key_hash, response_body, created_at) VALUES
('SSDF7SOaFyN1OiLopzlge5B1RavBwX1TLKTDR10DcoU=', NULL, '2026-07-28 08:00:00'),
('RRniBKh6qQa0pxL4iOq2qYWjcCFHLP5obPJdnEHjz4w=', NULL, '2026-07-28 09:00:00'),
('lyM74uV8bp+Ul1kZ4YOT5eawz9lXSoFADTJHVgY7oFI=', NULL, '2026-07-28 10:00:00'),
('Z+3ebyCrL6qWCXwTDIdkzcQhzXESFA9xLVgxG8fQaRI=', NULL, '2026-07-28 11:00:00');
