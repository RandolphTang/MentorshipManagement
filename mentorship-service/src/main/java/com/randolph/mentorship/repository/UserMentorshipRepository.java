package com.randolph.mentorship.repository;

import com.randolph.mentorship.entity.user.UserMentorshipEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserMentorshipRepository extends JpaRepository<UserMentorshipEntity, Long> {

    Optional<UserMentorshipEntity> findByUsername(String username);

    Optional<UserMentorshipEntity> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM UserMentorshipEntity u WHERE " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<UserMentorshipEntity> searchUsers(String searchTerm);
}
