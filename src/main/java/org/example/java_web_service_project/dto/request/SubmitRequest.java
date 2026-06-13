package org.example.java_web_service_project.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubmitRequest {
    @NotNull(message = "Course Id không được để trống")
    private Long courseId;

    @Pattern(regexp = "^(https?://)?(www\\.)?github\\.com/.+", message = "GitHub URL không hợp lệ")
    private String githubUrl;
}
