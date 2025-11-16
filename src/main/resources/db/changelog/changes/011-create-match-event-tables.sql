-- liquibase formatted sql

-- changeset three-kingdom-team:011-create-match-event-tables
-- comment: Create match event tables (match_event_tx and match_events) for event sourcing

-- ============================================
-- 1. SEQUENCE FOR TX_ID (if needed for application use)
-- ============================================
CREATE SEQUENCE IF NOT EXISTS match_event_tx_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- ============================================
-- 2. MATCH_EVENT_TX TABLE
-- ============================================
CREATE TABLE match_event_tx (
    id BIGSERIAL PRIMARY KEY,
    match_id BIGINT NOT NULL,
    tx_id BIGINT NOT NULL,
    command_type VARCHAR(64) NOT NULL,
    actor_kingdom VARCHAR(8) NOT NULL,
    round_number INTEGER,
    phase VARCHAR(32),
    idempotency_key VARCHAR(64),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ,
    CONSTRAINT fk_match_event_tx_match FOREIGN KEY (match_id) REFERENCES matches(id) ON DELETE CASCADE,
    CONSTRAINT uq_match_event_tx_tx_id UNIQUE (tx_id),
    CONSTRAINT uq_match_event_tx_idem_per_match UNIQUE (match_id, idempotency_key),
    CONSTRAINT ck_match_event_tx_actor_kingdom CHECK (actor_kingdom IN ('WEI', 'SHU', 'WU'))
);

CREATE INDEX idx_tx_match_id ON match_event_tx(match_id);
CREATE INDEX idx_tx_created_at ON match_event_tx(created_at);

-- ============================================
-- 3. MATCH_EVENTS TABLE
-- ============================================
CREATE TABLE match_events (
    match_id BIGINT NOT NULL,
    seq BIGINT NOT NULL,
    tx_id BIGINT NOT NULL,
    type VARCHAR(64) NOT NULL,
    payload_jsonb JSONB NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ,
    PRIMARY KEY (match_id, seq),
    CONSTRAINT fk_match_events_match FOREIGN KEY (match_id) REFERENCES matches(id) ON DELETE CASCADE
);

CREATE INDEX idx_events_match_id ON match_events(match_id);
CREATE INDEX idx_events_match_tx ON match_events(match_id, tx_id);
CREATE INDEX idx_events_type ON match_events(type);

-- rollback
-- rollback DROP TABLE IF EXISTS match_events;
-- rollback DROP TABLE IF EXISTS match_event_tx;
-- rollback DROP SEQUENCE IF EXISTS match_event_tx_id_seq;

