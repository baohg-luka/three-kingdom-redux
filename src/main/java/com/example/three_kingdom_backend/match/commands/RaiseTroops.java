package com.example.three_kingdom_backend.match.commands;

import com.example.three_kingdom_backend.match.enums.EnumKingdom;

public record RaiseTroops(Long matchId, EnumKingdom actor, String province, int amount, String idempotencyKey)
        implements MatchCommand {
    public RaiseTroops(Long matchId, EnumKingdom actor, String province, int amount) {
        this(matchId, actor, province, amount, null);
    }

    @Override
    public CommandType type() {
        return CommandType.RAISE_TROOPS;
    }
}