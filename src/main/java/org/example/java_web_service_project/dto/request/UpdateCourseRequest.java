package org.example.java_web_service_project.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateCourseRequest {
    @NotBlank(message = "Tên khóa học không được để trống")
    @Size(max = 200, message = "Tên khóa học không quá 200 kí tự")
    private String courseName;

    @Min(value = 1, message = "Số tín chỉ phải lớn hơn 0")
    @Max(value = 10, message = "Không được quá 10 tín chỉ")
    private Integer credit;

    private String description;

    private Long lecturerId;

    private Boolean isActive;
}
