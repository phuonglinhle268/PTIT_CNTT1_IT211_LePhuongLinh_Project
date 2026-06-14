package org.example.java_web_service_project.security;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;


//vô hiệu hóa tạm thời security khi test tầng controller
//đi thẳng vào gọi API mà không bị chặn lại đòi Token hay quyền Admin
//controller - nếu không tắt CSRF, tất cả các request dạng POST, PUT, DELETE sẽ bị Spring Security chặn lại và trả về lỗi 403 Forbidden
@TestConfiguration
public class TestSecurityConfig {

    @Bean
    public SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());  //cho tất cả các request (anyRequest()) đi qua không điều kiện gì
        return http.build();
    }
}