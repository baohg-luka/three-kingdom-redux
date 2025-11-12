-- liquibase formatted sql

-- changeset three-kingdom-team:016-match-detail-kingdom-cascade
-- comment: Cascade delete kingdom_info records when detached from match_details
ALTER TABLE match_details
    DROP CONSTRAINT IF EXISTS fk_match_detail_wei_kingdom;

ALTER TABLE match_details
    DROP CONSTRAINT IF EXISTS fk_match_detail_shu_kingdom;

ALTER TABLE match_details
    DROP CONSTRAINT IF EXISTS fk_match_detail_wu_kingdom;

ALTER TABLE match_details
    ADD CONSTRAINT fk_match_detail_wei_kingdom FOREIGN KEY (wei_kingdom_info_id) REFERENCES kingdom_info(id) ON DELETE CASCADE;

ALTER TABLE match_details
    ADD CONSTRAINT fk_match_detail_shu_kingdom FOREIGN KEY (shu_kingdom_info_id) REFERENCES kingdom_info(id) ON DELETE CASCADE;

ALTER TABLE match_details
    ADD CONSTRAINT fk_match_detail_wu_kingdom FOREIGN KEY (wu_kingdom_info_id) REFERENCES kingdom_info(id) ON DELETE CASCADE;

-- rollback
-- rollback ALTER TABLE match_details DROP CONSTRAINT IF EXISTS fk_match_detail_wei_kingdom;
-- rollback ALTER TABLE match_details DROP CONSTRAINT IF EXISTS fk_match_detail_shu_kingdom;
-- rollback ALTER TABLE match_details DROP CONSTRAINT IF EXISTS fk_match_detail_wu_kingdom;
-- rollback ALTER TABLE match_details ADD CONSTRAINT fk_match_detail_wei_kingdom FOREIGN KEY (wei_kingdom_info_id) REFERENCES kingdom_info(id) ON DELETE SET NULL;
-- rollback ALTER TABLE match_details ADD CONSTRAINT fk_match_detail_shu_kingdom FOREIGN KEY (shu_kingdom_info_id) REFERENCES kingdom_info(id) ON DELETE SET NULL;
-- rollback ALTER TABLE match_details ADD CONSTRAINT fk_match_detail_wu_kingdom FOREIGN KEY (wu_kingdom_info_id) REFERENCES kingdom_info(id) ON DELETE SET NULL;

