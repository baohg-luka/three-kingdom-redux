package com.example.three_kingdom_backend.match.service;

import com.example.three_kingdom_backend.match.MatchDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MatchDetailRepository extends JpaRepository<MatchDetail, Long> {
    @Query("select d from MatchDetail d where d.match.id = :matchId order by d.id desc")
    Optional<MatchDetail> findTopByMatchIdOrderByIdDesc(@Param("matchId") Long matchId);
}
