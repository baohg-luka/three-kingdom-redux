package com.example.three_kingdom_backend.match.service;

import com.example.three_kingdom_backend.match.store.MatchEventTx;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MatchEventTxRepository extends JpaRepository<MatchEventTx, Long> {
    Optional<MatchEventTx> findByMatchIdAndIdempotencyKey(Long matchId, String idempotencyKey);
}
