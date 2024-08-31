package com.example.securityOAuth.security.services;

import com.example.securityOAuth.entity.User.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface UserService extends UserDetailsService {

    Optional<UserEntity> findByEmail(String email);

    void save(UserEntity user);

}
