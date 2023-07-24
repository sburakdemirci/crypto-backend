package com.mtd.crypto.core.user.service;

import com.mtd.crypto.core.security.service.JwtTokenService;
import com.mtd.crypto.core.user.data.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor

public class UserInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService tokenService;

/*    @Transactional
    @PostConstruct
    public void createAdminUser() {

        User user = userRepository.save(User.builder()
                .name("")
                .email("")
                .password(this.passwordEncoder.encode(""))
                .role(Role.ROLE_CRON)
                .build());


        userRepository.setUserEnabled(user.getId(), true);

    }*/
}
