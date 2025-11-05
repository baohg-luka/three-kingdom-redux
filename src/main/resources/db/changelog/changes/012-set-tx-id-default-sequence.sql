-- liquibase formatted sql

-- changeset three-kingdom-team:012-set-tx-id-default-sequence
-- comment: Set default value for tx_id using sequence and align ownership/position
-- validCheckSum: 9:2e7eaa13f89d185f25e81c7dfc37cad3

ALTER TABLE match_event_tx
    ALTER COLUMN tx_id SET DEFAULT nextval('match_event_tx_id_seq');

ALTER SEQUENCE match_event_tx_id_seq
    OWNED BY match_event_tx.tx_id;

-- Nếu bảng đã có dữ liệu, đồng bộ sequence để tránh trùng:
SELECT setval('match_event_tx_id_seq',
              COALESCE((SELECT MAX(tx_id) FROM match_event_tx), 0) + 1,
              false);

-- rollback
-- rollback ALTER TABLE match_event_tx ALTER COLUMN tx_id DROP DEFAULT;
-- rollback ALTER SEQUENCE match_event_tx_id_seq OWNED BY NONE;

