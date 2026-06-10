package org.example.java_web_service_project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "course_enrollments", uniqueConstraints = @UniqueConstraint(columnNames = {"course_id", "student_id"}))
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CourseEnrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(updatable = false)
    private LocalDateTime enrolledAt;

    @PrePersist
    protected void onCreate() {
        enrolledAt = LocalDateTime.now();
    }
}
