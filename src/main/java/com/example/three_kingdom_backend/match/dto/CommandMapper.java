package com.example.three_kingdom_backend.match.dto;

import com.example.three_kingdom_backend.match.commands.CommandType;
import com.example.three_kingdom_backend.match.commands.MatchCommand;
import com.example.three_kingdom_backend.match.commands.RaiseTroops;
import com.example.three_kingdom_backend.match.enums.EnumKingdom;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CommandMapper {
    /**
     * Convert CommandDto to domain MatchCommand.
     * 
     * @param dto   the command DTO from client
     * @param actor the kingdom (determined from authentication, not from client
     *              input)
     * @return domain command object
     */
    public MatchCommand toCommand(CommandDto dto, EnumKingdom actor) {
        var type = CommandType.valueOf(dto.type());
        return switch (type) {
            case RAISE_TROOPS -> {
                int amount = 2;
                if (dto.data() instanceof Map<?, ?> dataMap) {
                    Object amountObj = dataMap.get("amount");
                    if (amountObj instanceof Number num) {
                        amount = num.intValue();
                    }
                }
                yield new RaiseTroops(
                        dto.matchId(), actor, null, amount, dto.idempotencyKey());
            }
            default -> throw new IllegalArgumentException("Unsupported: " + type);
        };
    }
}
