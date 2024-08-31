package com.randolph.mentorship.repository;

import com.randolph.mentorship.entity.session.MentorshipSessionEntity;
import com.randolph.mentorship.entity.user.UserMentorshipEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MentorshipSessionRepository extends JpaRepository<MentorshipSessionEntity, Long> {
    List<MentorshipSessionEntity> findMentorshipSessionEntitiesByMentor(UserMentorshipEntity mentor);

    @Query("SELECT ms FROM MentorshipSessionEntity ms WHERE :mentee MEMBER OF ms.mentees")
    List<MentorshipSessionEntity> findMentorshipSessionEntitiesByMentee(UserMentorshipEntity mentee);

    @Query("SELECT ms FROM MentorshipSessionEntity ms " +
            "WHERE (ms.mentor.id = :userId OR :userId IN (SELECT m.id FROM ms.mentees m)) " +
            "AND ms.startTime > :now " +
            "ORDER BY ms.startTime ASC")
    List<MentorshipSessionEntity> findIncomingSessionsForUser(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Query("SELECT ms FROM MentorshipSessionEntity ms " +
            "WHERE ms.mentor.id = :mentorId " +
            "AND ms.startTime > :now " +
            "ORDER BY ms.startTime ASC")
    List<MentorshipSessionEntity> findIncomingSessionsForMentor(@Param("mentorId") Long mentorId, @Param("now")  LocalDateTime now);

    @Query("SELECT ms FROM MentorshipSessionEntity ms " +
            "WHERE :menteeId IN (SELECT m.id FROM ms.mentees m) " +
            "AND ms.startTime > :now " +
            "ORDER BY ms.startTime ASC")
    List<MentorshipSessionEntity> findIncomingSessionsForMentee(@Param("menteeId") Long menteeId, @Param("now")  LocalDateTime now);

    @Query("SELECT ms FROM MentorshipSessionEntity  ms " +
            "WHERE (ms.mentor.id = :userId OR :userId IN (SELECT m.id FROM ms.mentees m)) " +
            "AND NOT (ms.endTime < :start OR ms.startTime > :end)")
    List<MentorshipSessionEntity> findSessionsForUserInDateRange(
            @Param("userId") Long userId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
