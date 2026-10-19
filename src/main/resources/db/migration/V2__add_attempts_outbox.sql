CREATE TABLE payment_attempts (
    id UUID PRIMARY KEY,

    payment_id UUID NOT NULL
        REFERENCES payments(id),

    provider_name VARCHAR(80) NOT NULL,
    provider_reference VARCHAR(128),

    outcome VARCHAR(32) NOT NULL,
    provider_code VARCHAR(80),

    created_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_attempts_payment
    ON payment_attempts (payment_id, created_at);


CREATE TABLE outbox_events (
    id UUID PRIMARY KEY,

    aggregate_id UUID NOT NULL,
    event_type VARCHAR(100) NOT NULL,

    payload TEXT NOT NULL,

    created_at TIMESTAMPTZ NOT NULL,
    published_at TIMESTAMPTZ,

    attempts INTEGER NOT NULL DEFAULT 0,
    last_error VARCHAR(500)
);

CREATE INDEX idx_outbox_unpublished
    ON outbox_events (published_at, created_at);
