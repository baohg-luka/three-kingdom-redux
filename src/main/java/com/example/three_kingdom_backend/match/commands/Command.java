package com.example.three_kingdom_backend.match.commands;

import com.example.three_kingdom_backend.match.enums.EnumKingdom;

public interface Command {
    String type();

    Long matchId();

    EnumKingdom actor();
}
