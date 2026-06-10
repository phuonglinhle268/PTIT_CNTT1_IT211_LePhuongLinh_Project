package org.example.java_web_service_project.config;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.java_web_service_project.entity.User;
import org.example.java_web_service_project.entity.enums.RoleEnum;
import org.example.java_web_service_project.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername("admin")) {
            userRepository.save(User.builder()
                    .username("admin")
                    .passwordHash(passwordEncoder.encode("Admin@123"))
                    .email("admin@coursemanagement.com")
                    .fullName("System Administrator")
                    .role(RoleEnum.ADMIN)
                    .isActive(true)
                    .build());
            log.info("Default admin created");
        }

        if (!userRepository.existsByUsername("lecturer01")) {
            userRepository.save(User.builder()
                    .username("lecturer01")
                    .passwordHash(passwordEncoder.encode("Lecturer@123"))
                    .email("lecturer01@coursemanagement.com")
                    .fullName("Nguyễn Văn A")
                    .role(RoleEnum.LECTURER)
                    .isActive(true)
                    .build());
            log.info("=== Sample lecturer created: username=lecturer01 / password=Lecturer@123 ===");
        }
    }
}
