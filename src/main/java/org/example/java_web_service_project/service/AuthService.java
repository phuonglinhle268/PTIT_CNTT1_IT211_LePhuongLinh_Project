package org.example.java_web_service_project.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.java_web_service_project.dto.request.LoginRequest;
import org.example.java_web_service_project.dto.request.RefreshTokenRequest;
import org.example.java_web_service_project.dto.response.AuthResponse;
import org.example.java_web_service_project.dto.response.UserResponse;
import org.example.java_web_service_project.entity.TokenBlacklist;
import org.example.java_web_service_project.entity.User;
import org.example.java_web_service_project.exception.AppException;
import org.example.java_web_service_project.repository.TokenBlacklistRepository;
import org.example.java_web_service_project.repository.UserRepository;
import org.example.java_web_service_project.security.jwt.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final TokenBlacklistRepository tokenBlacklistRepository;

    // Đăng nhập
    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = (User) authentication.getPrincipal();

        String accessToken  = jwtUtil.generateAccessToken(user.getUsername(), user.getRole().name(), user.getId());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername(), user.getRole().name(), user.getId());

        log.info("User {} đăng nhập", user.getUsername());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(900L)
                .user(UserResponse.from(user))
                .build();
    }

    // Refresh Token — cấp AccessToken mới
    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtUtil.validateToken(refreshToken)) {
            throw new AppException("Refresh token không hợp lệ hoặc đã hết hạn", HttpStatus.UNAUTHORIZED);
        }

        if (tokenBlacklistRepository.existsByTokenString(refreshToken)) {
            throw new AppException("Refresh token đã bị thu hồi", HttpStatus.UNAUTHORIZED);
        }

        String username = jwtUtil.extractUsername(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException("Không tìm thấy người dùng", HttpStatus.UNAUTHORIZED));

        if (!user.getIsActive()) {
            throw new AppException("Tài khoản đã bị vô hiệu hóa", HttpStatus.FORBIDDEN);
        }

        String newAccessToken = jwtUtil.generateAccessToken(user.getUsername(), user.getRole().name(), user.getId());

        log.info("User {} refreshed token", user.getUsername());

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)   // giữ nguyên refresh token cũ
                .tokenType("Bearer")
                .expiresIn(900L)
                .user(UserResponse.from(user))
                .build();
    }

    // Đăng xuất — blacklist AccessToken hiện tại
    @Transactional
    public void logout(String bearerToken) {
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            throw new AppException("Token không hợp lệ", HttpStatus.BAD_REQUEST);
        }

        String token = bearerToken.substring(7);

        if (!jwtUtil.validateToken(token)) {
            throw new AppException("Token đã hết hạn hoặc không hợp lệ", HttpStatus.UNAUTHORIZED);
        }

        if (tokenBlacklistRepository.existsByTokenString(token)) {
            throw new AppException("Token đã được đăng xuất trước đó", HttpStatus.BAD_REQUEST);
        }

        String username = jwtUtil.extractUsername(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException("Không tìm thấy người dùng", HttpStatus.NOT_FOUND));

        Date expiresAt = jwtUtil.extractExpiration(token);

        tokenBlacklistRepository.save(
                TokenBlacklist.builder()
                        .tokenString(token)
                        .user(user)
                        .expiresAt(expiresAt.toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime())
                        .build()
        );

        log.info("User {} đăng xuất", username);
    }
}