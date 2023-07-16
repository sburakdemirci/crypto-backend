package com.mtd.crypto.core.user.service;

import com.mtd.crypto.core.user.data.repository.RefreshTokenRepository;
import com.mtd.crypto.core.user.data.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor

public class UserInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    @org.springframework.transaction.annotation.Transactional
    @PostConstruct
    public void createAdminUser() {


 /*       User user = userRepository.save(User.builder()
                .name("")
                .email("")
                        .password(this.passwordEncoder.encode(""))
                        .build());


        userRepository.setUserEnabled(user.getId(), true);*/

    }
}
