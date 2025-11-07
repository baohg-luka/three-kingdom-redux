package com.example.three_kingdom_backend.match.service;

import java.util.List;
import java.util.Map;

/** Kết quả trả về cho client sau khi xử lý 1 command. */
public record CommandResult(
        long txId, // id nhóm hành động
        long lastSeq, // seq cuối cùng sau khi append
        List<Map<String, Object>> events, // [{seq,type,data},...]
        Map<String, Object> view // snapshot nhẹ để cập nhật UI
) {
    public static CommandResult empty(long txId, long lastSeq) {
        return new CommandResult(txId, lastSeq, List.of(), Map.of());
    }
}
