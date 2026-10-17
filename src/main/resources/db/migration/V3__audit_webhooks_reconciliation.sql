CREATE TABLE webhook_events (
 id UUID PRIMARY KEY,
 provider_name VARCHAR(80) NOT NULL,
 provider_event_id VARCHAR(160) NOT NULL,
 payload_hash VARCHAR(64) NOT NULL,
 received_at TIMESTAMPTZ NOT NULL,
 processed_at TIMESTAMPTZ,
 processing_status VARCHAR(24) NOT NULL,
 CONSTRAINT uq_webhook_provider_event UNIQUE(provider_name,provider_event_id)
);
CREATE TABLE audit_logs (
 id UUID PRIMARY KEY,
 actor_id VARCHAR(128),
 action VARCHAR(100) NOT NULL,
 aggregate_id UUID,
 correlation_id VARCHAR(128),
 outcome VARCHAR(32) NOT NULL,
 details_redacted JSONB NOT NULL DEFAULT '{}'::jsonb,
 created_at TIMESTAMPTZ NOT NULL
);
CREATE INDEX idx_audit_aggregate_created ON audit_logs(aggregate_id,created_at);
CREATE TABLE reconciliation_records (
 id UUID PRIMARY KEY,
 provider_name VARCHAR(80) NOT NULL,
 provider_reference VARCHAR(128),
 internal_payment_id UUID REFERENCES payments(id),
 internal_status VARCHAR(16),
 provider_status VARCHAR(32),
 match_status VARCHAR(32) NOT NULL,
 mismatch_reason VARCHAR(300),
 observed_at TIMESTAMPTZ NOT NULL,
 resolved_at TIMESTAMPTZ,
 resolution_note VARCHAR(500)
);
CREATE INDEX idx_reconciliation_match_status ON reconciliation_records(match_status,observed_at);
