package org.example.java_web_service_project.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GradeRequest {
    @NotNull(message = "Mã đăng kí không được để trống")
    private Long submissionId;

    @NotNull(message = "Điểm không được để trống")
    @DecimalMin(value = "0.0", message = "Điểm không được âm")
    @DecimalMax(value = "10.0", message = "Điểm không được quá 10")
    private Double score;

    private String feedback;
}
