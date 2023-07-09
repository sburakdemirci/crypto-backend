package com.mtd.crypto.core.user.data.repository;

import com.mtd.crypto.core.user.data.entity.RefreshToken;
import com.mtd.crypto.core.user.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUserId(String userId);

    @Modifying
    void deleteAllByUser(User user);


}
