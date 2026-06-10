package org.example.java_web_service_project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.java_web_service_project.dto.request.CreateCourseRequest;
import org.example.java_web_service_project.dto.request.CreateUserRequest;
import org.example.java_web_service_project.dto.request.UpdateCourseRequest;
import org.example.java_web_service_project.dto.request.UpdateUserRequest;
import org.example.java_web_service_project.dto.response.ApiResponse;
import org.example.java_web_service_project.dto.response.CourseResponse;
import org.example.java_web_service_project.dto.response.PageResponse;
import org.example.java_web_service_project.dto.response.UserResponse;
import org.example.java_web_service_project.entity.enums.RoleEnum;
import org.example.java_web_service_project.service.CourseService;
import org.example.java_web_service_project.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final CourseService courseService;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) RoleEnum role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<UserResponse> data = userService.getAllUsers(keyword, role, page, size);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách người dùng thành công", data));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin user thành công", userService.getUserById(id)));
    }

    @PostMapping("/users")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        UserResponse created = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo người dùng thành công", created));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thành công", userService.updateUser(id, request)));
    }

    @PatchMapping("/users/{id}/deactivate")
    public ResponseEntity<ApiResponse<Void>> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.ok(ApiResponse.success("Vô hiệu hóa tài khoản thành công"));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/courses")
    public ResponseEntity<ApiResponse<PageResponse<CourseResponse>>> getCourses(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<CourseResponse> data = courseService.getAllCourses(keyword, page, size);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách khóa học thành công", data));
    }

    @GetMapping("/courses/{id}")
    public ResponseEntity<ApiResponse<CourseResponse>> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin khóa học thành công", courseService.getCourseById(id)));
    }

    @PostMapping("/courses")
    public ResponseEntity<ApiResponse<CourseResponse>> createCourse(
            @Valid @RequestBody CreateCourseRequest request) {
        CourseResponse created = courseService.createCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Tạo khóa học thành công", created));
    }

    @PutMapping("/courses/{id}")
    public ResponseEntity<ApiResponse<CourseResponse>> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCourseRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật khóa học thành công",
                courseService.updateCourse(id, request)));
    }

    @DeleteMapping("/courses/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}

