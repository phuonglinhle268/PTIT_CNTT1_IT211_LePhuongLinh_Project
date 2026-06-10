package org.example.java_web_service_project.dto.response;

import lombok.*;
import org.example.java_web_service_project.entity.User;
import org.example.java_web_service_project.entity.enums.RoleEnum;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private String fullName;
    private RoleEnum role;
    private Boolean isActive;
    private LocalDateTime createdAt;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
