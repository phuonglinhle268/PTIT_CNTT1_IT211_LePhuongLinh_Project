package org.example.java_web_service_project.dto.request;

import jakarta.validation.constraints.Email;
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
public class UpdateUserRequest {
    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100, message = "Họ tên không được quá 100 kí tự")
    private String fullName;

    @Email(message = "Email không đúng định dạng")
    private String email;

    private Boolean isActive;
}
