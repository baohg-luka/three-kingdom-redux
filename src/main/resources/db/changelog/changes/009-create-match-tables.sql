-- liquibase formatted sql

-- changeset three-kingdom-team:009-create-match-tables
-- comment: Create match-related tables (matches, match_details, kingdom_info, buildings, match_active_players)

-- ============================================
-- 1. MATCHES TABLE
-- ============================================
CREATE TABLE matches (
    id BIGSERIAL PRIMARY KEY,
    room_id BIGINT NOT NULL,
    wei_player_id BIGINT NOT NULL,
    shu_player_id BIGINT NOT NULL,
    wu_player_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    current_turn VARCHAR(10),
    is_wei_turn BOOLEAN NOT NULL DEFAULT false,
    is_shu_turn BOOLEAN NOT NULL DEFAULT false,
    is_wu_turn BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ,
    CONSTRAINT fk_match_room FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE RESTRICT,
    CONSTRAINT fk_match_wei_player FOREIGN KEY (wei_player_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_match_shu_player FOREIGN KEY (shu_player_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_match_wu_player FOREIGN KEY (wu_player_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT ck_matches_status CHECK (status IN ('IN_PROGRESS', 'FINISHED', 'CANCELLED')),
    CONSTRAINT ck_matches_current_turn CHECK (current_turn IN ('WEI', 'SHU', 'WU') OR current_turn IS NULL)
);

CREATE INDEX idx_matches_room_id ON matches(room_id);
CREATE INDEX idx_matches_wei_player_id ON matches(wei_player_id);
CREATE INDEX idx_matches_shu_player_id ON matches(shu_player_id);
CREATE INDEX idx_matches_wu_player_id ON matches(wu_player_id);
CREATE INDEX idx_matches_status ON matches(status);

-- ============================================
-- 2. MATCH_ACTIVE_PLAYERS (JOIN TABLE)
-- ============================================
CREATE TABLE match_active_players (
    match_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (match_id, user_id),
    CONSTRAINT fk_match_active_players_match FOREIGN KEY (match_id) REFERENCES matches(id) ON DELETE CASCADE,
    CONSTRAINT fk_match_active_players_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_match_active_players_match_id ON match_active_players(match_id);
CREATE INDEX idx_match_active_players_user_id ON match_active_players(user_id);

-- ============================================
-- 3. KINGDOM_INFO TABLE
-- ============================================
CREATE TABLE kingdom_info (
    id BIGSERIAL PRIMARY KEY,
    kingdom VARCHAR(10) NOT NULL,
    gold INTEGER,
    rice INTEGER,
    population_support_token INTEGER,
    un_trained_troops INTEGER,
    trained_troops INTEGER,
    spear INTEGER,
    crossbow INTEGER,
    horse INTEGER,
    vessel INTEGER,
    red_card INTEGER,
    yellow_card INTEGER,
    total_general INTEGER,
    station_general INTEGER,
    unused_general INTEGER,
    flipped_market INTEGER,
    flipped_farm INTEGER,
    developed_market INTEGER,
    developed_farm INTEGER,
    market_flag_vp INTEGER,
    farm_flag_vp INTEGER,
    market_flag_no_vp INTEGER,
    farm_flag_no_vp INTEGER,
    military_victory_points INTEGER,
    economic_level INTEGER,
    tribal_level INTEGER,
    rank_level INTEGER,
    wu_border_level INTEGER,
    shu_border_level INTEGER,
    wei_border_level INTEGER,
    station_troops INTEGER,
    is_emperor_token BOOLEAN,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ,
    CONSTRAINT ck_kingdom_info_kingdom CHECK (kingdom IN ('WEI', 'SHU', 'WU'))
);

CREATE INDEX idx_kingdom_info_kingdom ON kingdom_info(kingdom);

-- ============================================
-- 4. BUILDINGS TABLE
-- ============================================
CREATE TABLE buildings (
    id BIGSERIAL PRIMARY KEY,
    kingdom_info_id BIGINT NOT NULL,
    type VARCHAR(10) NOT NULL,
    name VARCHAR(255),
    description TEXT,
    ability TEXT,
    vp INTEGER,
    cost VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ,
    CONSTRAINT fk_building_kingdom_info FOREIGN KEY (kingdom_info_id) REFERENCES kingdom_info(id) ON DELETE CASCADE,
    CONSTRAINT ck_buildings_type CHECK (type IN ('RED', 'YELLOW'))
);

CREATE INDEX idx_buildings_kingdom_info_id ON buildings(kingdom_info_id);

-- ============================================
-- 5. MATCH_DETAILS TABLE
-- ============================================
CREATE TABLE match_details (
    id BIGSERIAL PRIMARY KEY,
    match_id BIGINT NOT NULL,
    round_number INTEGER,
    king_marker VARCHAR(10),
    population_marker VARCHAR(10),
    phase VARCHAR(20),
    alliance_marker VARCHAR(20),
    first_kingdom VARCHAR(10),
    second_kingdom VARCHAR(10),
    third_kingdom VARCHAR(10),
    wei_kingdom_info_id BIGINT,
    shu_kingdom_info_id BIGINT,
    wu_kingdom_info_id BIGINT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ,
    CONSTRAINT fk_match_detail_match FOREIGN KEY (match_id) REFERENCES matches(id) ON DELETE CASCADE,
    CONSTRAINT fk_match_detail_wei_kingdom FOREIGN KEY (wei_kingdom_info_id) REFERENCES kingdom_info(id) ON DELETE SET NULL,
    CONSTRAINT fk_match_detail_shu_kingdom FOREIGN KEY (shu_kingdom_info_id) REFERENCES kingdom_info(id) ON DELETE SET NULL,
    CONSTRAINT fk_match_detail_wu_kingdom FOREIGN KEY (wu_kingdom_info_id) REFERENCES kingdom_info(id) ON DELETE SET NULL,
    CONSTRAINT ck_match_details_king_marker CHECK (king_marker IN ('ADMIN', 'COMBAT') OR king_marker IS NULL),
    CONSTRAINT ck_match_details_population_marker CHECK (population_marker IN ('ADMIN', 'COMBAT') OR population_marker IS NULL),
    CONSTRAINT ck_match_details_phase CHECK (phase IN ('RECRUIT', 'ALLIANCE', 'CONFLICT', 'RESOLVE', 'MAINTAINCE', 'END_ROUND') OR phase IS NULL),
    CONSTRAINT ck_match_details_alliance_marker CHECK (alliance_marker IN ('TRAIN', 'RECRUIT', 'DEMAND', 'CROSSBOW_NAVAL', 'HORSE_SPEAR', 'FARM', 'MARKET', 'TRADE', 'CONSTRUCT', 'HIRE') OR alliance_marker IS NULL),
    CONSTRAINT ck_match_details_first_kingdom CHECK (first_kingdom IN ('WEI', 'SHU', 'WU') OR first_kingdom IS NULL),
    CONSTRAINT ck_match_details_second_kingdom CHECK (second_kingdom IN ('WEI', 'SHU', 'WU') OR second_kingdom IS NULL),
    CONSTRAINT ck_match_details_third_kingdom CHECK (third_kingdom IN ('WEI', 'SHU', 'WU') OR third_kingdom IS NULL)
);

CREATE INDEX idx_match_details_match_id ON match_details(match_id);
CREATE INDEX idx_match_details_wei_kingdom_info_id ON match_details(wei_kingdom_info_id);
CREATE INDEX idx_match_details_shu_kingdom_info_id ON match_details(shu_kingdom_info_id);
CREATE INDEX idx_match_details_wu_kingdom_info_id ON match_details(wu_kingdom_info_id);

-- rollback
-- rollback DROP TABLE match_details;
-- rollback DROP TABLE buildings;
-- rollback DROP TABLE kingdom_info;
-- rollback DROP TABLE match_active_players;
-- rollback DROP TABLE matches;

