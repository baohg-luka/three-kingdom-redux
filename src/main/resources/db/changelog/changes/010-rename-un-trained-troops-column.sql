-- liquibase formatted sql

-- changeset three-kingdom-team:010-rename-un-trained-troops-column
-- comment: Rename un_trained_troops to untrained_troops in kingdom_info table

ALTER TABLE kingdom_info 
    RENAME COLUMN un_trained_troops TO untrained_troops;

-- rollback ALTER TABLE kingdom_info RENAME COLUMN untrained_troops TO un_trained_troops;

