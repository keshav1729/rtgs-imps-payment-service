CREATE TABLE payments (
 id UUID PRIMARY KEY, customer_id VARCHAR(128) NOT NULL, beneficiary_id VARCHAR(128) NOT NULL,
 idempotency_key VARCHAR(128) NOT NULL, request_hash VARCHAR(64) NOT NULL,
 mode VARCHAR(16) NOT NULL CHECK (mode IN ('IMPS','RTGS')),
 amount NUMERIC(19,2) NOT NULL CHECK (amount > 0), currency VARCHAR(3) NOT NULL CHECK (currency='INR'),
 status VARCHAR(16) NOT NULL CHECK (status IN ('CREATED','PROCESSING','PENDING','SUCCESS','FAILED','CANCELLED','UNKNOWN')),
 client_reference VARCHAR(100) NOT NULL, provider_reference VARCHAR(128), failure_code VARCHAR(80), failure_message VARCHAR(300),
 version BIGINT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL, updated_at TIMESTAMPTZ NOT NULL,
 CONSTRAINT uq_payment_customer_idempotency UNIQUE(customer_id,idempotency_key),
 CONSTRAINT uq_payment_customer_reference UNIQUE(customer_id,client_reference)
);
CREATE INDEX idx_payments_customer_created ON payments(customer_id,created_at DESC);
CREATE INDEX idx_payments_status_created ON payments(status,created_at);
