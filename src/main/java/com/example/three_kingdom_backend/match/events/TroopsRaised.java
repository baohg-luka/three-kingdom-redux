package com.example.three_kingdom_backend.match.events;

public record TroopsRaised(Long matchId, long txId, long seq, int amount) implements DomainEvent {
    @Override
    public String type() {
        return "TROOPS_RAISED";
    }
}