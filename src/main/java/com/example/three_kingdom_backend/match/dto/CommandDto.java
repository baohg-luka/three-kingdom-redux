package com.example.three_kingdom_backend.match.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Payload REST cho command từ client.
 * - type: tên lệnh (vd "RAISE_TROOPS")
 * - matchId: id ván đấu
 * - actor: kingdom (WEI/SHU/WU) dạng string
 * - idempotencyKey: (optional) UUID v4 do client sinh
 * - data: (optional) object/JSON chứa tham số phụ (vd { "amount": 2 })
 */
public record CommandDto(
        @NotBlank String type,
        @NotNull Long matchId,
        @NotBlank String actor,
        String idempotencyKey,
        Object data) {
}
