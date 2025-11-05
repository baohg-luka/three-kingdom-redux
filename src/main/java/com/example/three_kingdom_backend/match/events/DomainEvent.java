package com.example.three_kingdom_backend.match.events;

public interface DomainEvent {
    String type();

    long txId();

    long seq();

    Long matchId();
}
