package com.example.three_kingdom_backend.match.events;

import com.example.three_kingdom_backend.match.enums.EnumKingdom;

public record TroopsRaised(Long matchId, long txId, long seq, EnumKingdom kingdom, int amount) implements DomainEvent {
    @Override
    public String type() {
        return "TROOPS_RAISED";
    }

    public static TroopsRaised ofNew(Long matchId, long txId, EnumKingdom kingdom, int amount) {
        return new TroopsRaised(matchId, txId, 0, kingdom, amount);
    }

    public TroopsRaised withSeq(long seq) {
        return new TroopsRaised(matchId, txId, seq, kingdom, amount);
    }
}