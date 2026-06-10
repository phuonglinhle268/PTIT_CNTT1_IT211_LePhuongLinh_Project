package org.example.java_web_service_project.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RegisterRequest {
    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100, message = "Họ tên không được quá 100 ký tự")
    private String fullName;

    @NotBlank(message = "Username không được để trống")
    @Size(min = 4, max = 50, message = "Username phải từ 4 đến 50 ký tự")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username chỉ được chứa chữ, số và dấu gạch dưới")
    private String username;

    @NotBlank(message = "Password không được để trống")
    @Size(min = 6, max = 100, message = "Password phải từ 6 đến 100 ký tự")
    private String password;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;
}
