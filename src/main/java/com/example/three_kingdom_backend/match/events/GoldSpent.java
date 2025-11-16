package com.example.three_kingdom_backend.match.events;

import com.example.three_kingdom_backend.match.enums.EnumKingdom;

public record GoldSpent(Long matchId, long txId, long seq, EnumKingdom kingdom, int amount) implements DomainEvent {
    @Override
    public String type() {
        return "GOLD_SPENT";
    }

    public static GoldSpent ofNew(Long matchId, long txId, EnumKingdom kingdom, int amount) {
        return new GoldSpent(matchId, txId, 0, kingdom, amount);
    }

    public GoldSpent withSeq(long seq) {
        return new GoldSpent(matchId, txId, seq, kingdom, amount);
    }
}
