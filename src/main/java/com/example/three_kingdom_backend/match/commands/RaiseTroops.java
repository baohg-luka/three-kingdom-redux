package com.example.three_kingdom_backend.match.commands;

import com.example.three_kingdom_backend.match.enums.EnumKingdom;

public record RaiseTroops(Long matchId, EnumKingdom actor, String province, int amount) implements Command {
    @Override
    public String type() {
        return "RAISE_TROOPS";
    }
}