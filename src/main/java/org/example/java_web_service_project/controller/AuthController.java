package org.example.java_web_service_project.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.java_web_service_project.dto.request.RegisterRequest;
import org.example.java_web_service_project.dto.response.ApiResponse;
import org.example.java_web_service_project.dto.response.UserResponse;
import org.example.java_web_service_project.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        UserResponse response = userService.registerStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Đăng ký tài khoản thành công", response));
    }
}
