CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Users
CREATE TABLE users (
    user_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    phone_number VARCHAR(15) NOT NULL UNIQUE,
    pin_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    national_id VARCHAR(50) UNIQUE,
    id_document_path VARCHAR(500),
    kyc_level INT NOT NULL DEFAULT 1,
    kyc_verified_at TIMESTAMP,
    user_type VARCHAR(20) NOT NULL CHECK (user_type IN ('CUSTOMER','AGENT','MERCHANT','ADMIN')),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE','SUSPENDED','CLOSED')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_phone ON users(phone_number);
CREATE INDEX idx_users_national_id ON users(national_id);

-- Wallets
CREATE TABLE wallets (
    wallet_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(user_id),
    currency_code CHAR(3) NOT NULL,
    balance DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    daily_limit DECIMAL(18,2) NOT NULL DEFAULT 5000.00,
    monthly_limit DECIMAL(18,2) NOT NULL DEFAULT 50000.00,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    UNIQUE (user_id, currency_code)
);

CREATE INDEX idx_wallets_user ON wallets(user_id);

-- Agents
CREATE TABLE agents (
    agent_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(user_id),
    agent_code VARCHAR(20) NOT NULL UNIQUE,
    business_name VARCHAR(200) NOT NULL,
    territory VARCHAR(100),
    commission_rate DECIMAL(5,4) NOT NULL DEFAULT 0.0100,
    float_balance DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE','SUSPENDED')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_agents_code ON agents(agent_code);

-- Merchants
CREATE TABLE merchants (
    merchant_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(user_id),
    business_name VARCHAR(200) NOT NULL,
    settlement_wallet_id UUID REFERENCES wallets(wallet_id),
    webhook_url VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE','SUSPENDED')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Transactions
CREATE TABLE transactions (
    transaction_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    reference_number VARCHAR(50) NOT NULL UNIQUE,
    idempotency_key VARCHAR(100) NOT NULL UNIQUE,
    transaction_type VARCHAR(20) NOT NULL CHECK (transaction_type IN ('CASH_IN','CASH_OUT','TRANSFER','PAYMENT','AIRTIME','BILL_PAY','REFUND')),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING','COMPLETED','FAILED','REVERSED')),
    amount DECIMAL(18,2) NOT NULL CHECK (amount > 0),
    fee_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00 CHECK (fee_amount >= 0),
    agent_commission DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    currency_code CHAR(3) NOT NULL,
    sender_wallet_id UUID REFERENCES wallets(wallet_id),
    receiver_wallet_id UUID REFERENCES wallets(wallet_id),
    agent_id UUID REFERENCES agents(agent_id),
    merchant_id UUID REFERENCES merchants(merchant_id),
    source_channel VARCHAR(20) NOT NULL CHECK (source_channel IN ('USSD','MOBILE_APP','AGENT_APP','WEB')),
    description TEXT,
    external_reference VARCHAR(100),
    failure_reason VARCHAR(255),
    reversed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    CONSTRAINT chk_sender_receiver CHECK (sender_wallet_id IS DISTINCT FROM receiver_wallet_id),
    CONSTRAINT chk_fees CHECK (fee_amount + agent_commission <= amount)
);

CREATE INDEX idx_txn_sender ON transactions(sender_wallet_id, created_at);
CREATE INDEX idx_txn_receiver ON transactions(receiver_wallet_id, created_at);
CREATE INDEX idx_txn_reference ON transactions(reference_number);
CREATE INDEX idx_txn_idempotency ON transactions(idempotency_key);
CREATE INDEX idx_txn_external ON transactions(external_reference);

-- Ledger Entries (Double Entry)
CREATE TABLE ledger_entries (
    entry_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    transaction_id UUID NOT NULL REFERENCES transactions(transaction_id),
    wallet_id UUID NOT NULL REFERENCES wallets(wallet_id),
    entry_type VARCHAR(10) NOT NULL CHECK (entry_type IN ('DEBIT','CREDIT')),
    amount DECIMAL(18,2) NOT NULL CHECK (amount > 0),
    running_balance DECIMAL(18,2) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_ledger_wallet ON ledger_entries(wallet_id, created_at);
CREATE INDEX idx_ledger_txn ON ledger_entries(transaction_id);

-- Agent Float Logs
CREATE TABLE agent_float_logs (
    log_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    agent_id UUID NOT NULL REFERENCES agents(agent_id),
    transaction_id UUID REFERENCES transactions(transaction_id),
    movement_type VARCHAR(20) NOT NULL CHECK (movement_type IN ('TOP_UP','CASH_IN','CASH_OUT','SETTLEMENT')),
    amount DECIMAL(18,2) NOT NULL,
    running_float DECIMAL(18,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_float_agent ON agent_float_logs(agent_id, created_at);

-- Audit Logs
CREATE TABLE audit_logs (
    log_id BIGSERIAL PRIMARY KEY,
    user_id UUID REFERENCES users(user_id),
    action VARCHAR(100) NOT NULL,
    details TEXT,
    ip_address VARCHAR(45),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_user ON audit_logs(user_id, created_at);

-- Idempotency Keys
CREATE TABLE idempotency_keys (
    key_hash VARCHAR(64) PRIMARY KEY,
    response_body TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
