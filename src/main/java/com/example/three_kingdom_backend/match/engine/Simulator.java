package com.example.three_kingdom_backend.match.engine;

import com.example.three_kingdom_backend.match.commands.MatchCommand;
import com.example.three_kingdom_backend.match.commands.RaiseTroops;
import com.example.three_kingdom_backend.match.events.DomainEvent;
import com.example.three_kingdom_backend.match.events.GoldSpent;
import com.example.three_kingdom_backend.match.events.TroopsRaised;

import java.util.ArrayList;
import java.util.List;

public final class Simulator {
    private Simulator() {
    }

    private static final int GOLD_COST = 1;
    private static final int TROOPS_PER_CLICK = 2;

    public static List<DomainEvent> decide(MatchAggregate matchAggregate, MatchCommand matchCommand,
            long transactionId) {
        matchCommand.basicValidate();

        return switch (matchCommand.type()) {
            case RAISE_TROOPS -> {
                var command = (RaiseTroops) matchCommand;
                Validation.ensureAmountPositive(command.amount());
                Validation.ensureTurn(matchAggregate, command.actor());
                Validation.ensureEnoughGold(matchAggregate, command.actor(), GOLD_COST);

                List<DomainEvent> outputEvents = new ArrayList<>(2);
                outputEvents
                        .add(GoldSpent.ofNew(command.matchId(), transactionId, command.actor(), GOLD_COST));
                outputEvents
                        .add(TroopsRaised.ofNew(command.matchId(), transactionId, command.actor(), TROOPS_PER_CLICK));

                yield outputEvents;
            }
            default -> throw new IllegalArgumentException("Unsupported command: " + matchCommand.type());
        };
    }
}
