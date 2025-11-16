package com.example.three_kingdom_backend.match.service;

import com.example.three_kingdom_backend.match.KingdomInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface KingdomInfoRepository extends JpaRepository<KingdomInfo, Long> {
    Optional<KingdomInfo> findById(Long id);
}
