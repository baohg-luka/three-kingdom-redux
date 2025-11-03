package com.example.three_kingdom_backend.auth;

import com.example.three_kingdom_backend.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.time.Instant;

public interface PasswordResetOTPRepository extends JpaRepository<PasswordResetOTP, Long> {
    @Query("""
              SELECT o FROM PasswordResetOTP o
              WHERE o.user = :user AND o.used=false AND o.expiresAt > :now
              ORDER BY o.createdAt DESC
            """)
    List<PasswordResetOTP> findActive(@Param("user") UserEntity user, @Param("now") Instant now);
}
