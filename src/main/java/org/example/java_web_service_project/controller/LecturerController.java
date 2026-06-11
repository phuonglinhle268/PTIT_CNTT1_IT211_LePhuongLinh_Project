package org.example.java_web_service_project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.java_web_service_project.dto.request.GradeRequest;
import org.example.java_web_service_project.dto.response.*;
import org.example.java_web_service_project.entity.User;
import org.example.java_web_service_project.entity.enums.StatusEnum;
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
@RequestMapping("/api/v1/lecturer")
@PreAuthorize("hasRole('LECTURER')")
@RequiredArgsConstructor
public class LecturerController {

    private final SubmissionService submissionService;
    private final LectureMaterialService materialService;
    private final CourseService courseService;

    //Xem khóa học mình phụ trách
    @GetMapping("/courses")
    public ResponseEntity<ApiResponse<PageResponse<CourseResponse>>> getMyCourses(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success("Khóa học phụ trách",
                courseService.getCoursesByLecturer(currentUser.getId(), page, size)));
    }

    //Chấm điểm & Feedback

    @PostMapping("/grades")
    public ResponseEntity<ApiResponse<SubmissionResponse>> grade(
            @Valid @RequestBody GradeRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Chấm điểm thành công",
                submissionService.grade(request, currentUser.getId())));
    }

    @GetMapping("/courses/{courseId}/submissions")
    public ResponseEntity<ApiResponse<PageResponse<SubmissionResponse>>> getSubmissions(
            @PathVariable Long courseId,
            @RequestParam(required = false) StatusEnum status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success("Danh sách bài nộp",
                submissionService.getSubmissionsByCourse(courseId, status, page, size)));
    }

    //Tải lên tài liệu
    @PostMapping(value = "/courses/{courseId}/materials", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<LectureMaterialResponse>> uploadMaterial(
            @PathVariable Long courseId,
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam MultipartFile file,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tải tài liệu thành công",
                        materialService.upload(courseId, title, description, file, currentUser.getId())));
    }

    @GetMapping("/courses/{courseId}/materials")
    public ResponseEntity<ApiResponse<PageResponse<LectureMaterialResponse>>> getMaterials(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success("Tài liệu bài giảng",
                materialService.getMaterialsByCourse(courseId, page, size)));
    }

    @DeleteMapping("/materials/{materialId}")
    public ResponseEntity<Void> deleteMaterial(
            @PathVariable Long materialId,
            @AuthenticationPrincipal User currentUser) {
        materialService.deleteMaterial(materialId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}