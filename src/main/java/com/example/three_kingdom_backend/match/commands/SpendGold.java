package com.example.three_kingdom_backend.match.commands;

import com.example.three_kingdom_backend.match.enums.EnumKingdom;

public record SpendGold(Long matchId, EnumKingdom actor, int amount, String idempotencyKey) implements MatchCommand {
    public SpendGold(Long matchId, EnumKingdom actor, int amount) {
        this(matchId, actor, amount, null);
    }

    @Override
    public CommandType type() {
        return CommandType.SPEND_GOLD;
    }
}