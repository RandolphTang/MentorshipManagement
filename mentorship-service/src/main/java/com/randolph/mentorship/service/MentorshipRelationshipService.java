package com.randolph.mentorship.service;

import com.mentorship.shared.Enums.MentorshipStatus;
import com.mentorship.shared.Enums.UserRole;
import com.randolph.mentorship.entity.relationship.MentorshipRelationshipEntity;
import com.randolph.mentorship.entity.request.MentorshipRequestEntity;
import com.randolph.mentorship.entity.user.UserMentorshipEntity;
import com.randolph.mentorship.exception.UserNotFoundException;
import com.randolph.mentorship.repository.MentorshipRelationshipRepository;
import com.randolph.mentorship.repository.UserMentorshipRepository;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MentorshipRelationshipService {

    @Autowired
    private MentorshipRelationshipRepository relationshipRepository;

    @Autowired
    private UserMentorshipRepository userRepository;

//    private static final Logger logger = LoggerFactory.getLogger(MentorshipSessionService.class);

    @Transactional
    public MentorshipRelationshipEntity createMentorshipRelationship(Long mentorId, Long menteeId){
        UserMentorshipEntity mentor = userRepository.findById(mentorId)
                .orElseThrow(() -> new UserNotFoundException("Mentor not found"));
        UserMentorshipEntity mentee = userRepository.findById(menteeId)
                .orElseThrow(() -> new UserNotFoundException("Mentee not found"));


        if (mentor.getRole() != UserRole.MENTOR) {
            throw new IllegalArgumentException("User is not a mentor");
        }
        if (mentee.getRole() != UserRole.MENTEE) {
            throw new IllegalArgumentException("User is not a mentee");
        }

        MentorshipRelationshipEntity relationship = new MentorshipRelationshipEntity();
        relationship.setMentor(mentor);
        relationship.setMentee(mentee);
        relationship.setStartDate(LocalDateTime.now());
        relationship.setStatus(MentorshipStatus.ACTIVE);

        return relationshipRepository.save(relationship);
    }

    public List<MentorshipRelationshipEntity> getMentorshipsByMentor(Long mentorId) {
        UserMentorshipEntity mentor = userRepository.findById(mentorId)
                .orElseThrow(() -> new UserNotFoundException("Mentor not found"));
        return relationshipRepository.findByMentor(mentor);
    }

    public MentorshipRelationshipEntity getMentorshipByMentee(Long menteeId) {
        UserMentorshipEntity mentee = userRepository.findById(menteeId)
                .orElseThrow(() -> new UserNotFoundException("Mentee not found"));
        return relationshipRepository.findByMentee(mentee);
    }
}
