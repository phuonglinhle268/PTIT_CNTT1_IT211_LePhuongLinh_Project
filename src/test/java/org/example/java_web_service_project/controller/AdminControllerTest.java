package org.example.java_web_service_project.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.example.java_web_service_project.dto.response.PageResponse;
import org.example.java_web_service_project.dto.response.UserResponse;
import org.example.java_web_service_project.entity.enums.RoleEnum;
import org.example.java_web_service_project.exception.AppException;
import org.example.java_web_service_project.exception.GlobalExceptionHandler;
import org.example.java_web_service_project.repository.TokenBlacklistRepository;
import org.example.java_web_service_project.security.TestSecurityConfig;
import org.example.java_web_service_project.security.jwt.JwtUtil;
import org.example.java_web_service_project.service.CourseService;
import org.example.java_web_service_project.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

@WebMvcTest(AdminController.class)
@Import({TestSecurityConfig.class, GlobalExceptionHandler.class})
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private TokenBlacklistRepository tokenBlacklistRepository;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CourseService courseService;

    @Test
    @DisplayName("Danh sách users")
    void getUsers_Returns200() throws Exception {
        UserResponse user = UserResponse.builder()
                .id(1L).username("student01").role(RoleEnum.STUDENT).isActive(true).build();

        PageResponse<UserResponse> page = PageResponse.<UserResponse>builder()
                .content(List.of(user))
                .page(0).size(10).totalElements(1).totalPages(1).last(true)
                .build();

        when(userService.getAllUsers(null, null, 0, 10)).thenReturn(page);

        mockMvc.perform(get("/api/v1/admin/users")
                        .param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].username").value("student01"))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    @DisplayName("Tìm thấy user")
    void getUserById_Returns200() throws Exception {
        UserResponse user = UserResponse.builder()
                .id(1L).username("student01").role(RoleEnum.STUDENT).isActive(true).build();

        when(userService.getUserById(1L)).thenReturn(user);

        mockMvc.perform(get("/api/v1/admin/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.username").value("student01"));
    }

    @Test
    @DisplayName("Không tồn tại")
    void getUserById_Returns404() throws Exception {
        when(userService.getUserById(99L))
                .thenThrow(new AppException("Không tìm thấy user với id: 99", HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/api/v1/admin/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Không tìm thấy user với id: 99"));
    }

    @Test
    @DisplayName("Tạo thành công")
    void createUser_Returns201() throws Exception {
        UserResponse created = UserResponse.builder()
                .id(3L).username("lecturer02").role(RoleEnum.LECTURER).isActive(true).build();

        when(userService.createUser(any())).thenReturn(created);

        mockMvc.perform(post("/api/v1/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"lecturer02\",\"password\":\"Pass@123\"," +
                                "\"email\":\"lec02@gmail.com\",\"fullName\":\"Tran B\"," +
                                "\"role\":\"LECTURER\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.username").value("lecturer02"))
                .andExpect(jsonPath("$.data.role").value("LECTURER"));
    }

    @Test
    @DisplayName("Xóa thành công")
    void deleteUser_Returns204() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/v1/admin/users/1"))
                .andExpect(status().isNoContent());
    }
}