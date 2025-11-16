-- liquibase formatted sql

-- changeset three-kingdom-team:013-complete-tx-id-sequence-setup
-- comment: Complete tx_id sequence setup with OWNED BY and sync existing data

-- Set sequence ownership to match_event_tx.tx_id column
ALTER SEQUENCE match_event_tx_id_seq
    OWNED BY match_event_tx.tx_id;

-- Sync sequence value with existing data (if any)
-- This ensures the sequence continues from the highest existing tx_id
SELECT setval('match_event_tx_id_seq',
              COALESCE((SELECT MAX(tx_id) FROM match_event_tx), 0) + 1,
              false);

-- rollback
-- rollback ALTER SEQUENCE match_event_tx_id_seq OWNED BY NONE;

