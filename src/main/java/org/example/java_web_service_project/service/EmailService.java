package org.example.java_web_service_project.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtp(String toEmail, String otp, String subject) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(
                    "Mã OTP của bạn là: " + otp + "\n\n" +
                            "Mã có hiệu lực trong 5 phút.\n" +
                            "Không chia sẻ mã này với bất kỳ ai.\n\n" +
                            "Nếu bạn không yêu cầu, hãy bỏ qua email này."
            );
            mailSender.send(message);
            log.info("OTP sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send OTP to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Không thể gửi email, vui lòng thử lại sau");
        }
    }
}