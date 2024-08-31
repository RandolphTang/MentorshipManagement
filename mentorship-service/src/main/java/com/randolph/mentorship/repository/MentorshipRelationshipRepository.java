package com.randolph.mentorship.repository;

import com.randolph.mentorship.entity.relationship.MentorshipRelationshipEntity;
import com.randolph.mentorship.entity.session.MentorshipSessionEntity;
import com.randolph.mentorship.entity.user.UserMentorshipEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MentorshipRelationshipRepository extends JpaRepository<MentorshipRelationshipEntity, Long> {

    List<MentorshipRelationshipEntity> findByMentor(UserMentorshipEntity mentor);

    MentorshipRelationshipEntity findByMentee(UserMentorshipEntity mentee);

    MentorshipRelationshipEntity findByMentorAndMentee(UserMentorshipEntity mentor, UserMentorshipEntity mentee);

    List<MentorshipRelationshipEntity> findAllByMentorIdOrMenteeId(Long mentorId, Long menteeId);
}
