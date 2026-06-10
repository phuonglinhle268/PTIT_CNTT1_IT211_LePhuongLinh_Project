package org.example.java_web_service_project.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.java_web_service_project.dto.request.CreateUserRequest;
import org.example.java_web_service_project.dto.request.RegisterRequest;
import org.example.java_web_service_project.dto.request.UpdateUserRequest;
import org.example.java_web_service_project.dto.response.PageResponse;
import org.example.java_web_service_project.dto.response.UserResponse;
import org.example.java_web_service_project.entity.User;
import org.example.java_web_service_project.entity.enums.RoleEnum;
import org.example.java_web_service_project.exception.AppException;
import org.example.java_web_service_project.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse registerStudent(RegisterRequest request) {
        validateUniqueUsernameAndEmail(request.getUsername(), request.getEmail());

        User user = User.builder()
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .fullName(request.getFullName())
                .role(RoleEnum.STUDENT)
                .isActive(true)
                .build();

        User saved = userRepository.save(user);
        log.info("Sinh viên mới đăng kí: {}", saved.getUsername());
        return UserResponse.from(saved);
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        validateUniqueUsernameAndEmail(request.getUsername(), request.getEmail());

        User user = User.builder()
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .fullName(request.getFullName())
                .role(request.getRole())
                .isActive(true)
                .build();

        User saved = userRepository.save(user);
        log.info("Admin tạo user: {} với role: {}", saved.getUsername(), saved.getRole());
        return UserResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getAllUsers(String keyword, RoleEnum role, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<User> userPage;
        if (keyword != null && !keyword.isBlank()) {
            userPage = userRepository
                    .findByFullNameContainingIgnoreCaseOrUsernameContainingIgnoreCase(keyword, keyword, pageable);
        } else if (role != null) {
            userPage = userRepository.findByRole(role, pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }

        List<UserResponse> responseList = userPage.getContent()
                .stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());

        return PageResponse.from(new PageImpl<>(responseList, pageable, userPage.getTotalElements()));
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        return UserResponse.from(findUserOrThrow(id));
    }

    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = findUserOrThrow(id);

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new AppException("Email đã được sử dụng bởi tài khoản khác", HttpStatus.CONFLICT);
            }
            user.setEmail(request.getEmail());
        }
        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getIsActive() != null) user.setIsActive(request.getIsActive());

        User saved = userRepository.save(user);
        log.info("Admin updated user: {}", saved.getUsername());
        return UserResponse.from(saved);
    }

    @Transactional
    public void deactivateUser(Long id) {
        User user = findUserOrThrow(id);
        user.setIsActive(false);
        userRepository.save(user);
        log.info("Admin vô hiệu hóa user: {}", user.getUsername());
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = findUserOrThrow(id);
        userRepository.delete(user);
        log.info("Admin xóa user id: {}", id);
    }

    private User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new AppException("Không tìm thấy user với id: " + id, HttpStatus.NOT_FOUND));
    }

    private void validateUniqueUsernameAndEmail(String username, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new AppException("Username '" + username + "' đã tồn tại", HttpStatus.CONFLICT);
        }
        if (userRepository.existsByEmail(email)) {
            throw new AppException("Email '" + email + "' đã được sử dụng", HttpStatus.CONFLICT);
        }
    }
}
