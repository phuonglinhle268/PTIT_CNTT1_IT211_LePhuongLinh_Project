package org.example.java_web_service_project.controller;

import lombok.RequiredArgsConstructor;
import org.example.java_web_service_project.dto.response.ApiResponse;
import org.example.java_web_service_project.dto.response.CourseResponse;
import org.example.java_web_service_project.dto.response.PageResponse;
import org.example.java_web_service_project.entity.User;
import org.example.java_web_service_project.service.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/student")
@PreAuthorize("hasRole('STUDENT')")
@RequiredArgsConstructor
public class StudentController {
    private final CourseService courseService;

    @GetMapping("/courses")
    public ResponseEntity<ApiResponse<PageResponse<CourseResponse>>> browseCourses(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<CourseResponse> data = courseService.getAllCourses(keyword, page, size);
        return ResponseEntity.ok(ApiResponse.success("Danh sách khóa học", data));
    }


    @PostMapping("/courses/{courseId}/enroll")
    public ResponseEntity<ApiResponse<CourseResponse>> enrollCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal User currentUser) {
        CourseResponse response = courseService.enrollCourse(courseId, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Đăng ký khóa học thành công", response));
    }


    @DeleteMapping("/courses/{courseId}/enroll")
    public ResponseEntity<Void> unenrollCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal User currentUser) {
        courseService.unenrollCourse(courseId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/my-courses")
    public ResponseEntity<ApiResponse<PageResponse<CourseResponse>>> getMyCourses(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<CourseResponse> data = courseService.getMyCourses(currentUser.getId(), page, size);
        return ResponseEntity.ok(ApiResponse.success("Danh sách khóa học của bạn", data));
    }
}
