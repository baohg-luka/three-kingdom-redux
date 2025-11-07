package com.example.three_kingdom_backend.match.store;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatchEventRepository extends JpaRepository<MatchEvent, MatchEventKey> {
    // MatchEvent and MatchEventKey are in the same package, no import needed

    @Query("SELECT COALESCE(MAX(e.seq), 0) FROM MatchEvent e WHERE e.matchId = :matchId")
    long findMaxSeq(@Param("matchId") Long matchId);
}
