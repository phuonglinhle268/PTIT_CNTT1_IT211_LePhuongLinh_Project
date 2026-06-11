package org.example.java_web_service_project.dto.response;

import lombok.*;
import org.example.java_web_service_project.entity.LectureMaterial;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class LectureMaterialResponse {
    private Long id;
    private String title;
    private String description;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
    private LocalDateTime uploadedAt;
    private Long courseId;
    private String courseCode;
    private Long uploadedById;
    private String uploadedByName;

    public static LectureMaterialResponse from(LectureMaterial m) {
        return LectureMaterialResponse.builder()
                .id(m.getId())
                .title(m.getTitle())
                .description(m.getDescription())
                .fileUrl(m.getFileUrl())
                .fileType(m.getFileType())
                .fileSize(m.getFileSize())
                .uploadedAt(m.getUploadedAt())
                .courseId(m.getCourse() != null ? m.getCourse().getId() : null)
                .courseCode(m.getCourse() != null ? m.getCourse().getCourseCode() : null)
                .uploadedById(m.getUploadedBy() != null ? m.getUploadedBy().getId() : null)
                .uploadedByName(m.getUploadedBy() != null ? m.getUploadedBy().getFullName() : null)
                .build();
    }
}
