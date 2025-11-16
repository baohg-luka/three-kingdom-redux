package com.example.three_kingdom_backend.match.commands;

import com.example.three_kingdom_backend.match.enums.EnumKingdom;

public interface MatchCommand {
    CommandType type();

    Long matchId();

    EnumKingdom actor();

    String idempotencyKey();

    default void basicValidate() {
        if (matchId() == null)
            throw new IllegalArgumentException("matchId required");
        if (actor() == null)
            throw new IllegalArgumentException("actor required");
    }
}
