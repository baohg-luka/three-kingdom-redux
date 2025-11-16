package com.example.three_kingdom_backend.match.service;

import com.example.three_kingdom_backend.match.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long> {

    Optional<Match> findById(Long id);

}
