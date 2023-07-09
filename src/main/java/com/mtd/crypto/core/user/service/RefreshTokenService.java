package com.mtd.crypto.core.user.service;


import com.mtd.crypto.core.user.data.entity.RefreshToken;
import com.mtd.crypto.core.user.data.entity.User;
import com.mtd.crypto.core.user.data.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final Long ONE_YEAR_IN_MILLISECONDS = 31556952000L;


    private final RefreshTokenRepository refreshTokenRepository;


    public Optional<RefreshToken> getByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }


    public RefreshToken getToken(User user) {

        return refreshTokenRepository.findByUserId(user.getId()).orElseGet(() -> create(user));
    }


    @Transactional
    public RefreshToken create(User user) {
        return refreshTokenRepository.save(new RefreshToken(user, UUID.randomUUID().toString(),
                Instant.now().plusMillis(ONE_YEAR_IN_MILLISECONDS)));
    }


}
