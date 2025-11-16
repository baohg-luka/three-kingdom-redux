package com.example.three_kingdom_backend.match.api;

import com.example.three_kingdom_backend.match.dto.CommandDto;
import com.example.three_kingdom_backend.match.dto.CommandMapper;
import com.example.three_kingdom_backend.match.service.CommandResult;
import com.example.three_kingdom_backend.match.service.MatchCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchCommandController {

    private final CommandMapper commandMapper;
    private final MatchCommandService matchCommandService;

    @PostMapping("/{id}/commands")
    public ResponseEntity<CommandResult> post(
            @PathVariable Long id,
            @Valid @RequestBody CommandDto dto,
            @RequestHeader(value = "Idempotency-Key", required = false) String idemHeader) {
        if (!id.equals(dto.matchId())) {
            return ResponseEntity.badRequest().build();
        }

        // Prioritize header if body is missing idempotencyKey
        CommandDto effective = dto.idempotencyKey() == null && idemHeader != null
                ? new CommandDto(dto.type(), dto.matchId(), dto.actor(), idemHeader, dto.data())
                : dto;

        var command = commandMapper.toCommand(effective);
        var result = matchCommandService.handle(command);
        return ResponseEntity.ok(result);
    }
}
