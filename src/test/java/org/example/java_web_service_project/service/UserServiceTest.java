package org.example.java_web_service_project.service;

import static org.junit.jupiter.api.Assertions.*;

import org.example.java_web_service_project.dto.request.RegisterRequest;
import org.example.java_web_service_project.dto.response.UserResponse;
import org.example.java_web_service_project.entity.User;
import org.example.java_web_service_project.entity.enums.RoleEnum;
import org.example.java_web_service_project.exception.AppException;
import org.example.java_web_service_project.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private OtpService otpService;

    @InjectMocks private UserService userService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(1L).username("newStudent").passwordHash("$2a$hashed")
                .email("newstudent@gmail.com").fullName("Yuri")
                .role(RoleEnum.STUDENT).isActive(true).build();
    }

    @Test
    @DisplayName("Đăng kí sinh viên thành công")
    void registerStudent_Success() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("newStudent");
        req.setPassword("123456");
        req.setEmail("newstudent@gmail.com");
        req.setFullName("Yuri");

        when(userRepository.existsByUsername("newStudent")).thenReturn(false);
        when(userRepository.existsByEmail("newstudent@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("$2a$hashed");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        UserResponse result = userService.registerStudent(req);

        assertThat(result.getUsername()).isEqualTo("newStudent");
        assertThat(result.getRole()).isEqualTo(RoleEnum.STUDENT);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Đăng kí trùng username sinh viên")
    void registerStudent_UsernameExists_Throws409() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("newStudent");
        req.setPassword("Pass@123");
        req.setEmail("other@gmail.com");
        req.setFullName("Test");

        when(userRepository.existsByUsername("newStudent")).thenReturn(true);

        AppException ex = catchThrowableOfType(() -> userService.registerStudent(req), AppException.class);

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.CONFLICT);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Tìm thấy")
    void getUserById_Found() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        UserResponse result = userService.getUserById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("newStudent");
    }

    @Test
    @DisplayName("Không tìm thấy user")
    void getUserById_NotFound_Throws404() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        AppException ex = catchThrowableOfType(
                () -> userService.getUserById(99L), AppException.class);

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Vô hiệu hóa user")
    void deactivateUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        userService.deactivateUser(1L);

        assertThat(mockUser.getIsActive()).isFalse();
        verify(userRepository).save(mockUser);
    }
}