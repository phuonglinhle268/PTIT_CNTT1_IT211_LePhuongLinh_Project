package org.example.java_web_service_project.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.java_web_service_project.dto.request.CreateCourseRequest;
import org.example.java_web_service_project.dto.request.UpdateCourseRequest;
import org.example.java_web_service_project.dto.response.CourseResponse;
import org.example.java_web_service_project.dto.response.PageResponse;
import org.example.java_web_service_project.entity.Course;
import org.example.java_web_service_project.entity.CourseEnrollment;
import org.example.java_web_service_project.entity.User;
import org.example.java_web_service_project.entity.enums.RoleEnum;
import org.example.java_web_service_project.exception.AppException;
import org.example.java_web_service_project.repository.CourseEnrollmentRepository;
import org.example.java_web_service_project.repository.CourseRepository;
import org.example.java_web_service_project.repository.UserRepository;

import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CourseEnrollmentRepository enrollmentRepository;

    @Transactional
    public CourseResponse createCourse(CreateCourseRequest request) {
        if (courseRepository.existsByCourseCode(request.getCourseCode()))
            throw new AppException("Mã khóa học '" + request.getCourseCode() + "' đã tồn tại", HttpStatus.CONFLICT);

        Course course = Course.builder()
                .courseCode(request.getCourseCode()).courseName(request.getCourseName())
                .credit(request.getCredit()).description(request.getDescription()).isActive(true).build();

        if (request.getLecturerId() != null) course.setLecturer(findLecturerOrThrow(request.getLecturerId()));

        Course saved = courseRepository.save(course);
        log.info("Admin tạo khóa học: {}", saved.getCourseCode());
        return CourseResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public PageResponse<CourseResponse> getAllCourses(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Course> coursePage = (keyword != null && !keyword.isBlank())
                ? courseRepository.findByCourseNameContainingIgnoreCaseAndIsActiveTrue(keyword, pageable)
                : courseRepository.findByIsActiveTrue(pageable);

        List<CourseResponse> list = coursePage.getContent().stream()
                .map(CourseResponse::from).collect(Collectors.toList());
        return PageResponse.from(new PageImpl<>(list, pageable, coursePage.getTotalElements()));
    }

    @Transactional(readOnly = true)
    public CourseResponse getCourseById(Long id) {
        return CourseResponse.from(findCourseOrThrow(id));
    }

    @Transactional
    public CourseResponse updateCourse(Long id, UpdateCourseRequest request) {
        Course course = findCourseOrThrow(id);
        if (request.getCourseName() != null) course.setCourseName(request.getCourseName());
        if (request.getCredit() != null) course.setCredit(request.getCredit());
        if (request.getDescription() != null) course.setDescription(request.getDescription());
        if (request.getIsActive() != null) course.setIsActive(request.getIsActive());
        if (request.getLecturerId() != null) course.setLecturer(findLecturerOrThrow(request.getLecturerId()));
        return CourseResponse.from(courseRepository.save(course));
    }

    @Transactional
    public void deleteCourse(Long id) {
        Course course = findCourseOrThrow(id);
        course.setIsActive(false);
        courseRepository.save(course);
    }

    @Transactional
    public CourseResponse enrollCourse(Long courseId, Long studentId) {
        Course course = findCourseOrThrow(courseId);
        if (!course.getIsActive()) throw new AppException("Khóa học không còn hoạt động", HttpStatus.BAD_REQUEST);

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new AppException("Không tìm thấy sinh viên", HttpStatus.NOT_FOUND));
        if (student.getRole() != RoleEnum.STUDENT)
            throw new AppException("Chỉ sinh viên mới được đăng ký", HttpStatus.FORBIDDEN);
        if (enrollmentRepository.existsByCourse_IdAndStudent_Id(courseId, studentId))
            throw new AppException("Bạn đã đăng ký khóa học này rồi", HttpStatus.CONFLICT);

        enrollmentRepository.save(CourseEnrollment.builder().course(course).student(student).build());
        log.info("Student {} enrolled course {}", student.getUsername(), course.getCourseCode());
        return CourseResponse.from(course);
    }

    @Transactional
    public void unenrollCourse(Long courseId, Long studentId) {
        if (!courseRepository.existsById(courseId))
            throw new AppException("Không tìm thấy khóa học", HttpStatus.NOT_FOUND);
        if (!enrollmentRepository.existsByCourse_IdAndStudent_Id(courseId, studentId))
            throw new AppException("Bạn chưa đăng ký khóa học này", HttpStatus.BAD_REQUEST);
        enrollmentRepository.deleteByCourse_IdAndStudent_Id(courseId, studentId);
    }

    @Transactional(readOnly = true)
    public PageResponse<CourseResponse> getMyCourses(Long studentId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Course> coursePage = courseRepository.findByEnrolledStudentId(studentId, pageable);
        List<CourseResponse> list = coursePage.getContent().stream()
                .map(CourseResponse::from).collect(Collectors.toList());
        return PageResponse.from(new PageImpl<>(list, pageable, coursePage.getTotalElements()));
    }

    private Course findCourseOrThrow(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new AppException("Không tìm thấy khóa học id: " + id, HttpStatus.NOT_FOUND));
    }

    private User findLecturerOrThrow(Long lecturerId) {
        User u = userRepository.findById(lecturerId)
                .orElseThrow(() -> new AppException("Không tìm thấy giảng viên id: " + lecturerId, HttpStatus.NOT_FOUND));
        if (u.getRole() != RoleEnum.LECTURER)
            throw new AppException("User id=" + lecturerId + " không phải giảng viên", HttpStatus.BAD_REQUEST);
        return u;
    }
}
