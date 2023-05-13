package com.mtd.crypto.core.user.service;

import com.mtd.crypto.core.user.data.entity.User;
import com.mtd.crypto.core.user.data.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;


    public Optional<User> findByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    public Optional<User> findById(String userId) {
        return userRepository.findById(userId);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

}
