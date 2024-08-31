package com.example.securityOAuth.security.services;

import com.example.securityOAuth.entity.RefreshToken.RefreshTokenEntity;
import com.example.securityOAuth.repository.RefreshTokenEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private static final long REFRESH_TOKEN_VALIDITY = 7 * 24 * 60 * 60 * 1000;

    @Autowired
    private RefreshTokenEntityRepository refreshTokenEntityRepository;

    public RefreshTokenEntity createRefreshToken(String email) {
        RefreshTokenEntity refreshToken = new RefreshTokenEntity();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setEmail(email);
        refreshToken.setExpiryDates(Instant.now().plusMillis(REFRESH_TOKEN_VALIDITY));
        return refreshTokenEntityRepository.save(refreshToken);
    }

    public RefreshTokenEntity verifyExpiration(RefreshTokenEntity token) {
        if (token.getExpiryDates().isBefore(Instant.now())) {
            refreshTokenEntityRepository.delete(token);
            throw new RuntimeException("Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    public Optional<RefreshTokenEntity> findByToken(String token) {
        return refreshTokenEntityRepository.findByToken(token);
    }

    @Transactional
    public void deleteByToken(String refreshToken) {
        refreshTokenEntityRepository.deleteByToken(refreshToken);
    }

    @Transactional
    public void deleteByEmail(String email) {
        refreshTokenEntityRepository.deleteByEmail(email);
    }
}
