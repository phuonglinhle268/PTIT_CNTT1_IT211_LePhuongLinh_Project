package org.example.java_web_service_project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.java_web_service_project.dto.request.SubmitRequest;
import org.example.java_web_service_project.dto.response.*;
import org.example.java_web_service_project.entity.User;
import org.example.java_web_service_project.service.CourseService;
import org.example.java_web_service_project.service.LectureMaterialService;
import org.example.java_web_service_project.service.SubmissionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/student")
@PreAuthorize("hasRole('STUDENT')")
@RequiredArgsConstructor
public class StudentController {

    private final CourseService courseService;
    private final SubmissionService submissionService;
    private final LectureMaterialService materialService;

    // Khóa học
    @GetMapping("/courses")
    public ResponseEntity<ApiResponse<PageResponse<CourseResponse>>> browseCourses(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success("Danh sách khóa học",
                courseService.getAllCourses(keyword, page, size)));
    }

    @PostMapping("/courses/{courseId}/enroll")
    public ResponseEntity<ApiResponse<CourseResponse>> enroll(
            @PathVariable Long courseId,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Đăng ký khóa học thành công",
                        courseService.enrollCourse(courseId, currentUser.getId())));
    }

    @DeleteMapping("/courses/{courseId}/enroll")
    public ResponseEntity<Void> unenroll(
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
        return ResponseEntity.ok(ApiResponse.success("Khóa học của bạn",
                courseService.getMyCourses(currentUser.getId(), page, size)));
    }

    //Nộp bài
    @PostMapping("/submissions")
    public ResponseEntity<ApiResponse<SubmissionResponse>> submitGithub(
            @Valid @RequestBody SubmitRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Nộp bài thành công",
                        submissionService.submit(request, currentUser.getId())));
    }

    @PostMapping(value = "/submissions/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<SubmissionResponse>> uploadReport(
            @RequestParam Long courseId,
            @RequestParam MultipartFile file,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tải báo cáo thành công",
                        submissionService.uploadReport(courseId, file, currentUser.getId())));
    }

    @GetMapping("/submissions")
    public ResponseEntity<ApiResponse<PageResponse<SubmissionResponse>>> getMySubmissions(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success("Danh sách bài nộp",
                submissionService.getMySubmissions(currentUser.getId(), page, size)));
    }

    //Xem tài liệu
    @GetMapping("/courses/{courseId}/materials")
    public ResponseEntity<ApiResponse<PageResponse<LectureMaterialResponse>>> getMaterials(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success("Tài liệu bài giảng",
                materialService.getMaterialsByCourse(courseId, page, size)));
    }
}