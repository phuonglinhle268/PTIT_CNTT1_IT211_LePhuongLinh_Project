package org.example.java_web_service_project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "token_blacklist", indexes = {
        @Index(name = "idx_token_string", columnList = "tokenString")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenBlacklist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,length = 1000)
    private String tokenString;

    @Column(nullable = false)
    private LocalDateTime revokedAt;

    private LocalDateTime expiresAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @PrePersist
    protected void onCreate() {
        revokedAt = LocalDateTime.now();
    }
}
