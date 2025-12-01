package com.example.three_kingdom_backend.match.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Payload REST cho command từ client.
 * - type: tên lệnh (vd "RAISE_TROOPS")
 * - matchId: id ván đấu
 * - idempotencyKey: (optional) UUID v4 do client sinh
 * - data: (optional) object/JSON chứa tham số phụ (vd { "amount": 2 })
 * 
 * Note: actor (kingdom) sẽ được tự động xác định từ JWT token của user hiện tại
 */
public record CommandDto(
                @NotBlank String type,
                @NotNull Long matchId,
                String idempotencyKey,
                Object data) {
}
