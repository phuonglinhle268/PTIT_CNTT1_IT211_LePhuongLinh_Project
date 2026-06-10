package org.example.java_web_service_project.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.java_web_service_project.entity.enums.RoleEnum;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateUserRequest {
    @NotBlank(message = "Username không được để trống")
    @Size(min = 4, max = 50, message = "Username phải từ 4 - 50 ký tự")
    private String username;

    @NotBlank(message = "Password không được để trống")
    @Size(min = 6, max = 100, message = "Password phải từ 6 - 100 ký tự")
    private String password;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100, message = "Họ tên không được quá 100 ký tự")
    private String fullName;

    @NotNull(message = "Role không được để trống")
    private RoleEnum role;
}
