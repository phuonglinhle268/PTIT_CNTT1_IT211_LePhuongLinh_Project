package org.example.java_web_service_project.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.java_web_service_project.entity.enums.StatusEnum;

import java.time.LocalDateTime;

@Entity
@Table(name = "submissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500)
    private String githubUrl;

    @Column(length = 1000)
    private String reportUrl;

    @Column(precision = 5)
    private Double score;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatusEnum status = StatusEnum.PENDING;

    private LocalDateTime submittedAt;

    private LocalDateTime gradedAt;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecturer_id")
    private User lecturer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
