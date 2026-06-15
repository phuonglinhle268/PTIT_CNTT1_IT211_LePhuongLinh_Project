package org.example.java_web_service_project.dto.response;

import lombok.*;
import org.example.java_web_service_project.entity.Course;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CourseResponse {

    private Long id;
    private String courseCode;
    private String courseName;
    private Integer credit;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private Long lecturerId;
    private String lecturerName;
    //private Integer enrolledCount;

    public static CourseResponse from(Course course) {
        return CourseResponse.builder()
                .id(course.getId())
                .courseCode(course.getCourseCode())
                .courseName(course.getCourseName())
                .credit(course.getCredit())
                .description(course.getDescription())
                .isActive(course.getIsActive())
                .createdAt(course.getCreatedAt())
                .lecturerId(course.getLecturer() != null ? course.getLecturer().getId() : null)
                .lecturerName(course.getLecturer() != null ? course.getLecturer().getFullName() : null)
                //.enrolledCount(0)
                .build();
    }
}

