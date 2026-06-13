package org.example.java_web_service_project.service;

import lombok.RequiredArgsConstructor;
import org.example.java_web_service_project.entity.OtpToken;
import org.example.java_web_service_project.exception.AppException;
import org.example.java_web_service_project.repository.OtpTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpTokenRepository otpTokenRepository;
    private final EmailService emailService;

    @Value("${otp.expiration-seconds:300}")
    private int expirationSeconds;

    public static final String TYPE_CHANGE = "CHANGE_PASSWORD";
    public static final String TYPE_FORGOT = "FORGOT_PASSWORD";

    // Tạo và gửi OTP
    @Transactional
    public void sendOtp(String email, String type, String emailSubject) {
        String otp = generateOtp();

        OtpToken token = OtpToken.builder()
                .email(email)
                .otp(otp)
                .type(type)
                .used(false)
                .expiresAt(LocalDateTime.now().plusSeconds(expirationSeconds))
                .build();

        otpTokenRepository.save(token);
        emailService.sendOtp(email, otp, emailSubject);
    }

    // Xác minh OTP
    @Transactional
    public void verifyOtp(String email, String otp, String type) {
        OtpToken token = otpTokenRepository
                .findTopByEmailAndTypeAndUsedFalseOrderByCreatedAtDesc(email, type)
                .orElseThrow(() -> new AppException(
                        "Không tìm thấy OTP, vui lòng yêu cầu gửi lại", HttpStatus.BAD_REQUEST));

        if (token.isExpired()) {
            throw new AppException("OTP đã hết hạn, vui lòng yêu cầu gửi lại", HttpStatus.BAD_REQUEST);
        }

        if (!token.getOtp().equals(otp)) {
            throw new AppException("OTP không đúng", HttpStatus.BAD_REQUEST);
        }

        token.setUsed(true);
        otpTokenRepository.save(token);
    }

    //OTP 6 số ngẫu nhiên
    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        int number = random.nextInt(900000) + 100000;
        return String.valueOf(number);
    }
}