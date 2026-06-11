package org.example.java_web_service_project.dto.response;

import lombok.*;
import org.example.java_web_service_project.entity.Submission;
import org.example.java_web_service_project.entity.enums.StatusEnum;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionResponse {

    private Long id;
    private String githubUrl;
    private String reportUrl;
    private Double score;
    private String feedback;
    private StatusEnum status;
    private LocalDateTime submittedAt;
    private LocalDateTime gradedAt;
    private LocalDateTime createdAt;

    private Long studentId;
    private String studentName;
    private Long courseId;
    private String courseCode;
    private Long lecturerId;
    private String lecturerName;

    public static SubmissionResponse from(Submission s) {
        return SubmissionResponse.builder()
                .id(s.getId())
                .githubUrl(s.getGithubUrl())
                .reportUrl(s.getReportUrl())
                .score(s.getScore())
                .feedback(s.getFeedback())
                .status(s.getStatus())
                .submittedAt(s.getSubmittedAt())
                .gradedAt(s.getGradedAt())
                .createdAt(s.getCreatedAt())
                .studentId(s.getStudent() != null ? s.getStudent().getId() : null)
                .studentName(s.getStudent() != null ? s.getStudent().getFullName() : null)
                .courseId(s.getCourse() != null ? s.getCourse().getId() : null)
                .courseCode(s.getCourse() != null ? s.getCourse().getCourseCode() : null)
                .lecturerId(s.getLecturer() != null ? s.getLecturer().getId() : null)
                .lecturerName(s.getLecturer() != null ? s.getLecturer().getFullName() : null)
                .build();
    }
}
