package org.example.java_web_service_project.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.java_web_service_project.dto.request.GradeRequest;
import org.example.java_web_service_project.dto.request.SubmitRequest;
import org.example.java_web_service_project.dto.response.PageResponse;
import org.example.java_web_service_project.dto.response.SubmissionResponse;
import org.example.java_web_service_project.entity.Course;
import org.example.java_web_service_project.entity.Submission;
import org.example.java_web_service_project.entity.User;
import org.example.java_web_service_project.entity.enums.StatusEnum;
import org.example.java_web_service_project.exception.AppException;
import org.example.java_web_service_project.repository.CourseRepository;
import org.example.java_web_service_project.repository.SubmissionRepository;
import org.example.java_web_service_project.repository.UserRepository;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CloudStorageService cloudStorageService;

    //student - nộp bài
    @Transactional
    public SubmissionResponse submit(SubmitRequest request, Long studentId) {
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new AppException("Không tìm thấy khóa học", HttpStatus.NOT_FOUND));

        // Kiểm tra sinh viên đã đăng ký khóa học chưa
        boolean enrolled = courseRepository.isStudentEnrolled(request.getCourseId(), studentId);
        if (!enrolled) {
            throw new AppException("Bạn chưa đăng ký khóa học này", HttpStatus.FORBIDDEN);
        }

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new AppException("Không tìm thấy sinh viên", HttpStatus.NOT_FOUND));

        Submission submission = submissionRepository
                .findByStudent_IdAndCourse_Id(studentId, request.getCourseId())
                .orElse(Submission.builder()
                        .student(student)
                        .course(course)
                        .status(StatusEnum.PENDING)
                        .build());

        if (submission.getStatus() == StatusEnum.GRADED) {
            throw new AppException("Bài đã được chấm điểm, không thể nộp lại", HttpStatus.CONFLICT);
        }

        submission.setGithubUrl(request.getGithubUrl());
        submission.setStatus(StatusEnum.SUBMITTED);
        submission.setSubmittedAt(LocalDateTime.now());

        Submission saved = submissionRepository.save(submission);
        log.info("Sinh viên {} đã nộp bài cho khóa học {}", student.getUsername(), course.getCourseCode());
        return SubmissionResponse.from(saved);
    }

    // Student - Nộp bài + file báo cáo
    @Transactional
    public SubmissionResponse uploadReport(Long courseId, MultipartFile file, Long studentId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException("Không tìm thấy khóa học", HttpStatus.NOT_FOUND));

        boolean enrolled = courseRepository.isStudentEnrolled(courseId, studentId);
        if (!enrolled) {
            throw new AppException("Bạn chưa đăng ký khóa học này", HttpStatus.FORBIDDEN);
        }

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new AppException("Không tìm thấy sinh viên", HttpStatus.NOT_FOUND));

        Submission submission = submissionRepository
                .findByStudent_IdAndCourse_Id(studentId, courseId)
                .orElse(Submission.builder()
                        .student(student)
                        .course(course)
                        .status(StatusEnum.PENDING)
                        .build());

        if (submission.getStatus() == StatusEnum.GRADED) {
            throw new AppException("Bài đã được chấm điểm, không thể nộp lại", HttpStatus.CONFLICT);
        }

        String fileUrl = cloudStorageService.uploadFile(file, "submissions/" + courseId);

        submission.setReportUrl(fileUrl);
        submission.setStatus(StatusEnum.SUBMITTED);
        submission.setSubmittedAt(LocalDateTime.now());

        Submission saved = submissionRepository.save(submission);
        log.info("Sinh viên {} nộp báo cáo cho khóa học {}", student.getUsername(), course.getCourseCode());
        return SubmissionResponse.from(saved);
    }

    //Lecturer - Chấm điểm & Feedback
    @Transactional
    public SubmissionResponse grade(GradeRequest request, Long lecturerId) {
        Submission submission = submissionRepository.findById(request.getSubmissionId())
                .orElseThrow(() -> new AppException("Không tìm thấy bài nộp", HttpStatus.NOT_FOUND));

        if (submission.getStatus() == StatusEnum.PENDING) {
            throw new AppException("Sinh viên chưa nộp bài", HttpStatus.BAD_REQUEST);
        }

        // Kiểm tra giảng viên có phụ trách course này không
        if (submission.getCourse().getLecturer() == null ||
                !submission.getCourse().getLecturer().getId().equals(lecturerId)) {
            throw new AppException("Bạn không phụ trách khóa học này", HttpStatus.FORBIDDEN);
        }

        User lecturer = userRepository.findById(lecturerId)
                .orElseThrow(() -> new AppException("Không tìm thấy giảng viên", HttpStatus.NOT_FOUND));

        submission.setScore(request.getScore());
        submission.setFeedback(request.getFeedback());
        submission.setStatus(StatusEnum.GRADED);
        submission.setGradedAt(LocalDateTime.now());
        submission.setLecturer(lecturer);

        Submission saved = submissionRepository.save(submission);
        return SubmissionResponse.from(saved);
    }

    // Student - Xem danh sách bài nộp của mình
    @Transactional(readOnly = true)
    public PageResponse<SubmissionResponse> getMySubmissions(Long studentId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Submission> pageData = submissionRepository.findByStudentId(studentId, pageable);

        List<SubmissionResponse> list = pageData.getContent().stream()
                .map(SubmissionResponse::from)
                .collect(Collectors.toList());

        return PageResponse.from(new PageImpl<>(list, pageable, pageData.getTotalElements()));
    }

    // Lecturer - Xem danh sách bài nộp theo course
    @Transactional(readOnly = true)
    public PageResponse<SubmissionResponse> getSubmissionsByCourse(Long courseId, StatusEnum status,
                                                                   int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("submittedAt").descending());

        Page<Submission> pageData = (status != null)
                ? submissionRepository.findByCourse_IdAndStatus(courseId, status, pageable)
                : submissionRepository.findByCourse_Id(courseId, pageable);

        List<SubmissionResponse> list = pageData.getContent().stream()
                .map(SubmissionResponse::from)
                .collect(Collectors.toList());

        return PageResponse.from(new PageImpl<>(list, pageable, pageData.getTotalElements()));
    }
}