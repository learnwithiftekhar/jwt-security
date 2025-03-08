package com.learnwithiftekhar.jwtsecurity.repository;

import com.learnwithiftekhar.jwtsecurity.model.RefreshToken;
import com.learnwithiftekhar.jwtsecurity.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshTokenRepository> findByToken(String refreshToken);
    Optional<RefreshTokenRepository> findByUser(User user);
}
