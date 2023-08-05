package com.mtd.crypto.core.user.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @Setter
    private User user;

    @Setter
    @Column(nullable = false, unique = true)
    private String token;

    @Setter
    @Column(nullable = false)
    private Instant expiresAt;

    public RefreshToken(User user, String token, Instant expiresAt) {
        this.user = user;
        this.token = token;
        this.expiresAt = expiresAt;
    }
}
