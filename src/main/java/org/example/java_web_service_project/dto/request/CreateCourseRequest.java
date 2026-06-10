package org.example.java_web_service_project.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateCourseRequest {
    @NotBlank(message = "Mã khóa học không được để trống")
    @Size(max = 20, message = "Mã khóa học không được quá 20 kí tự")
    private String courseCode;

    @NotBlank(message = "Tên khóa học không được để trống")
    @Size(max = 200, message = "Mã khóa học không được quá 200 kí tự")
    private String courseName;

    @NotNull(message = "Số tín chỉ không được để trống")
    @Min(value = 1, message = "Số tín chỉ phải lớn hơn 0")
    @Max(value = 10, message = "Không được quá 10 tín chỉ")
    private Integer credit;

    private String description;

    private Long lecturerId;



}
