package com.mentorship.profile_service.repository;

import com.mentorship.profile_service.entity.UserProfileEntity;
import com.mentorship.shared.Enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfileEntity, Long> {
    List<UserProfileEntity> findBySkillsContainingIgnoreCase(String skill);
    List<UserProfileEntity> findByRoleAndSkillsContainingIgnoreCase(UserRole role, String skill);
}
