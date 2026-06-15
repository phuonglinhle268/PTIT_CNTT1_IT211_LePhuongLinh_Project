package org.example.java_web_service_project.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.java_web_service_project.dto.response.LectureMaterialResponse;
import org.example.java_web_service_project.dto.response.PageResponse;
import org.example.java_web_service_project.entity.Course;
import org.example.java_web_service_project.entity.LectureMaterial;
import org.example.java_web_service_project.entity.User;
import org.example.java_web_service_project.exception.AppException;
import org.example.java_web_service_project.repository.CourseRepository;
import org.example.java_web_service_project.repository.LectureMaterialRepository;
import org.example.java_web_service_project.repository.UserRepository;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LectureMaterialService {

    private final LectureMaterialRepository materialRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CloudStorageService cloudStorageService;

    //Lecturer: Tải lên tài liệu bài giảng
    @Transactional
    public LectureMaterialResponse upload(Long courseId, String title, String description,
                                          MultipartFile file, Long lecturerId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException("Không tìm thấy khóa học", HttpStatus.NOT_FOUND));

        if (course.getLecturer() == null || !course.getLecturer().getId().equals(lecturerId)) {
            throw new AppException("Bạn không phụ trách khóa học này", HttpStatus.FORBIDDEN);
        }

        User lecturer = userRepository.findById(lecturerId)
                .orElseThrow(() -> new AppException("Không tìm thấy giảng viên", HttpStatus.NOT_FOUND));

        String fileUrl = cloudStorageService.uploadFile(file, "materials/" + courseId);

        LectureMaterial material = LectureMaterial.builder()
                .title(title)
                .description(description)
                .fileUrl(fileUrl)
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .course(course)
                .uploadedBy(lecturer)
                .build();

        LectureMaterial saved = materialRepository.save(material);
        log.info("Giảng viên {} uploaded tài liệu '{}' cho khóa học {}",
                lecturer.getUsername(), title, course.getCourseCode());
        return LectureMaterialResponse.from(saved);
    }

    //student + lecturer: Xem danh sách tài liệu theo course
    @Transactional(readOnly = true)
    public PageResponse<LectureMaterialResponse> getMaterialsByCourse(Long courseId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("uploadedAt").ascending());
        Page<LectureMaterial> pageData = materialRepository.findByCourse_Id(courseId, pageable);

        List<LectureMaterialResponse> list = pageData.getContent().stream()
                .map(LectureMaterialResponse::from)
                .collect(Collectors.toList());

        return PageResponse.from(new PageImpl<>(list, pageable, pageData.getTotalElements()));
    }

    //lecturer: xóa tài liệu
    @Transactional
    public void deleteMaterial(Long materialId, Long lecturerId) {
        LectureMaterial material = materialRepository.findById(materialId)
                .orElseThrow(() -> new AppException("Không tìm thấy tài liệu", HttpStatus.NOT_FOUND));

        if (!material.getUploadedBy().getId().equals(lecturerId)) {
            throw new AppException("Bạn không có quyền xóa tài liệu này", HttpStatus.FORBIDDEN);
        }

        materialRepository.delete(material);
        log.info("Giảng viên {} xóa tài liệu với id {}", lecturerId, materialId);
    }
}