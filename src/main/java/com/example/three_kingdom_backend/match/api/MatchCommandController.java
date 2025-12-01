package com.example.three_kingdom_backend.match.api;

import com.example.three_kingdom_backend.config.security.AuthUser;
import com.example.three_kingdom_backend.match.dto.CommandDto;
import com.example.three_kingdom_backend.match.dto.CommandMapper;
import com.example.three_kingdom_backend.match.enums.EnumKingdom;
import com.example.three_kingdom_backend.match.service.CommandResult;
import com.example.three_kingdom_backend.match.service.MatchCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
            @RequestHeader(value = "Idempotency-Key", required = false) String idemHeader,
            @AuthenticationPrincipal AuthUser authUser) {
        if (!id.equals(dto.matchId())) {
            return ResponseEntity.badRequest().build();
        }

        // Prioritize header if body is missing idempotencyKey
        CommandDto effective = dto.idempotencyKey() == null && idemHeader != null
                ? new CommandDto(dto.type(), dto.matchId(), idemHeader, dto.data())
                : dto;

        // Determine actor from authenticated user (prevents impersonation)
        EnumKingdom actor = matchCommandService.determineActor(authUser.getId(), id);

        var command = commandMapper.toCommand(effective, actor);
        var result = matchCommandService.handle(command);
        return ResponseEntity.ok(result);
    }
}
