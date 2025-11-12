-- liquibase formatted sql

-- changeset three-kingdom-team:015-add-match-room-unique-constraint
-- comment: Ensure each room can only be associated with a single match
DROP INDEX IF EXISTS idx_matches_room_id;

ALTER TABLE matches
    ADD CONSTRAINT uq_matches_room UNIQUE (room_id);

-- rollback
-- rollback ALTER TABLE matches DROP CONSTRAINT IF EXISTS uq_matches_room;
-- rollback CREATE INDEX idx_matches_room_id ON matches(room_id);

