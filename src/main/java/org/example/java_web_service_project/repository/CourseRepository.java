package org.example.java_web_service_project.repository;

import org.example.java_web_service_project.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByCourseCode(String courseCode);

    boolean existsByCourseCode(String courseCode);

    Page<Course> findByIsActiveTrue(Pageable pageable);

    Page<Course> findByCourseNameContainingIgnoreCaseAndIsActiveTrue(String courseName, Pageable pageable);

    @Query("SELECT c FROM Course c JOIN c.enrolledStudents s WHERE s.id = :studentId")
    Page<Course> findByEnrolledStudentId(@Param("studentId") Long studentId, Pageable pageable);


    @Query("SELECT c FROM Course c WHERE c.lecturer.id = :lecturerId")
    Page<Course> findByLecturer_Id(@Param("lecturerId") Long lecturerId, Pageable pageable);
}
