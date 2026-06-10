package org.example.java_web_service_project.repository;

import org.example.java_web_service_project.entity.LectureMaterial;
import org.example.java_web_service_project.entity.Submission;
import org.example.java_web_service_project.entity.enums.StatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    Optional<Submission> findByStudent_IdAndCourse_Id(Long studentId, Long courseId);

    boolean existsByStudent_IdAndCourse_Id(Long studentId, Long courseId);

    Page<Submission> findByCourse_Id(Long courseId, Pageable pageable);

    Page<Submission> findByStudent_Id(Long studentId, Pageable pageable);

    Page<Submission> findByCourse_IdAndStatus(Long courseId, StatusEnum status, Pageable pageable);

    Page<Submission> findByLecturer_Id(Long lecturerId, Pageable pageable);
}