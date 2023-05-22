package com.mtd.crypto.core.user.service;

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

    @PostConstruct
    public void createAdminUser() {

      /*  userRepository.save(User.builder()
                .name("")
                .email("")
                .password(this.passwordEncoder.encode(""))
                .build());*/

    }
}
