package com.example.securityOAuth.security.services.impl;

import com.example.securityOAuth.dto.securityChange.changePasswordDTO;
import com.example.securityOAuth.dto.securityChange.changeEmailDTO;
import com.example.securityOAuth.entity.User.UserEntity;
import com.example.securityOAuth.exception.EmailAlreadyExistsException;
import com.example.securityOAuth.exception.InvalidPasswordException;
import com.example.securityOAuth.repository.UserEntityRepository;
import com.example.securityOAuth.security.services.UserService;
import com.mentorship.shared.events.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class  UserDetailsServiceImpl implements UserService {

    @Autowired
    private final UserEntityRepository userEntityRepository;

    @Autowired
    private final KafkaTemplate<Long, UserCreatedEvent> kafkaUserCreatedTemplate;

    @Autowired
    private final KafkaTemplate<String, Long> kafkaUserDeletionTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        return userEntityRepository.findByEmail(email);
    }

    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userEntityRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with email: " + email));

        return UserDetailsImpl.build(userEntity);
    }

    @Override
    public void save(UserEntity user) {

        userEntityRepository.save(user);

        UserCreatedEvent userCreatedEvent = new UserCreatedEvent(user.getId(), user.getEmail(), user.getUsername());
        kafkaUserCreatedTemplate.send("user-created", user.getId(), userCreatedEvent);
    }

    @Transactional
    public void deleteUserById(Long userId) {

        userEntityRepository.deleteById(userId);
        kafkaUserDeletionTemplate.send("user-deletion", "user-key", userId);
    }

    public int getAccountAgeInDays(Long userId) {
        UserEntity user = userEntityRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return (int) ChronoUnit.DAYS.between(user.getCreatedAt(), LocalDateTime.now());
    }

    @Transactional
    public void changePassword(Long userId, changePasswordDTO changePasswordDTO) {
        UserEntity user = userEntityRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.getPassword() != null && !passwordEncoder.matches(changePasswordDTO.getCurrentPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        userEntityRepository.save(user);
    }

    @Transactional
    public void changeEmail(Long userId, changeEmailDTO securityInfoChangeDto) {
        UserEntity user = userEntityRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(securityInfoChangeDto.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Password is incorrect");
        }

        if (userEntityRepository.existsByEmail(securityInfoChangeDto.getNewEmail())) {
            throw new EmailAlreadyExistsException("Email already in use");
        }

        user.setEmail(securityInfoChangeDto.getNewEmail());
        userEntityRepository.save(user);
    }
}
