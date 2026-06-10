package org.example.java_web_service_project.repository;

import org.example.java_web_service_project.entity.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, Long> {

    boolean existsByTokenString(String tokenString);

    // Dọn dẹp token hết hạn tự nhiên (chạy theo lịch định kỳ)
    @Modifying
    @Transactional
    @Query("DELETE FROM TokenBlacklist t WHERE t.expiresAt < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);
}

