package org.example.java_web_service_project.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.java_web_service_project.repository.OtpTokenRepository;
import org.example.java_web_service_project.repository.TokenBlacklistRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

//dọn OTP hết hạn
@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledTasks {

    private final OtpTokenRepository otpTokenRepository;
    private final TokenBlacklistRepository tokenBlacklistRepository;

    // Chạy mỗi 1 giờ — dọn OTP hết hạn và token blacklist cũ
    @Scheduled(fixedRate = 3600000)
    public void cleanExpiredData() {
        otpTokenRepository.deleteExpiredAndUsed(LocalDateTime.now());
        tokenBlacklistRepository.deleteExpiredTokens(LocalDateTime.now());
        log.debug("Cleaned expired OTPs and blacklisted tokens");
    }
}