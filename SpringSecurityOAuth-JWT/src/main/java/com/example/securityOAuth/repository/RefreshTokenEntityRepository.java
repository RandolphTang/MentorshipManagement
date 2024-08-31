package com.example.securityOAuth.repository;

import com.example.securityOAuth.entity.RefreshToken.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenEntityRepository extends JpaRepository<RefreshTokenEntity, Long>  {
    Optional<RefreshTokenEntity> findByToken(String token);
    void deleteByToken(String token);

    void deleteByEmail(String email);
}
