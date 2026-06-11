package org.example.java_web_service_project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.java_web_service_project.dto.request.*;
import org.example.java_web_service_project.dto.response.ApiResponse;
import org.example.java_web_service_project.dto.response.AuthResponse;
import org.example.java_web_service_project.dto.response.UserResponse;
import org.example.java_web_service_project.entity.User;
import org.example.java_web_service_project.service.AuthService;
import org.example.java_web_service_project.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    // Đăng nhập
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        AuthResponse data = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Đăng nhập thành công", data));
    }

    // Xoay vòng token
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse data = authService.refresh(request);
        return ResponseEntity.ok(ApiResponse.success("Làm mới token thành công", data));
    }

    // Đăng xuất — revoke token
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader("Authorization") String bearerToken) {
        authService.logout(bearerToken);
        return ResponseEntity.ok(ApiResponse.success("Đăng xuất thành công"));
    }

    //đăng kí
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        UserResponse data = userService.registerStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Đăng ký tài khoản thành công", data));
    }


    //Đổi mật khẩu (Authenticated)
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal User currentUser) {
        userService.changePassword(currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Đổi mật khẩu thành công"));
    }

    //Quên mật khẩu (Public)
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        userService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Đặt lại mật khẩu thành công"));
    }
}