package org.example.java_web_service_project.repository;


import org.example.java_web_service_project.entity.User;
import org.example.java_web_service_project.entity.enums.RoleEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Page<User> findByRole(RoleEnum role, Pageable pageable);

    Page<User> findByFullNameContainingIgnoreCaseOrUsernameContainingIgnoreCase(String fullName, String username, Pageable pageable);
}

