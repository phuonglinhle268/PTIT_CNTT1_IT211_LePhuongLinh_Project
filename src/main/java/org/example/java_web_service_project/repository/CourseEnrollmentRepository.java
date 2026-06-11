package org.example.java_web_service_project.repository;

import org.example.java_web_service_project.entity.CourseEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long> {
//    boolean existsByCourse_IdAndStudent_Id(Long courseId, Long studentId);
//
//    void deleteByCourse_IdAndStudent_Id(Long courseId, Long studentId);
}
