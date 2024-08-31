package com.randolph.mentorship.service;


import com.mentorship.shared.Enums.MentorshipStatus;
import com.mentorship.shared.Enums.UserRole;
import com.mentorship.shared.events.UserCreatedEvent;
import com.randolph.mentorship.dto.RoleSelectionDto;
import com.randolph.mentorship.dto.UserMentorshipInfoDto;
import com.randolph.mentorship.entity.relationship.MentorshipRelationshipEntity;
import com.randolph.mentorship.entity.request.MentorshipRequestEntity;
import com.randolph.mentorship.entity.session.MentorshipSessionEntity;
import com.randolph.mentorship.entity.user.UserMentorshipEntity;
import com.randolph.mentorship.exception.UserNotFoundException;
import com.randolph.mentorship.repository.MentorshipRelationshipRepository;
import com.randolph.mentorship.repository.MentorshipRequestRepository;
import com.randolph.mentorship.repository.MentorshipSessionRepository;
import com.randolph.mentorship.repository.UserMentorshipRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserMentorshipService {

    private static final Logger logger = LoggerFactory.getLogger(UserMentorshipService.class);

    @Autowired
    private UserMentorshipRepository userMentorshipRepository;

    @Autowired
    private MentorshipSessionRepository sessionRepository;

    @Autowired
    private MentorshipRelationshipRepository relationshipRepository;

    @Autowired
    private MentorshipRequestRepository mentorshipRequestRepository;

    @KafkaListener(topics = "user-created", groupId = "mentorship-service", containerFactory = "userCreatedKafkaListenerContainerFactory")
    public void consumeUserUpdates(ConsumerRecord<Long, UserCreatedEvent> record) {

        Long userId = record.key();
        UserCreatedEvent event = record.value();

        try {
            UserMentorshipEntity userMentorshipEntity = userMentorshipRepository.findById(userId)
                    .orElse(new UserMentorshipEntity());

            userMentorshipEntity.setId(userId);
            userMentorshipEntity.setEmail(event.getEmail());
            userMentorshipEntity.setUsername(event.getUsername());
            userMentorshipEntity.setRole(UserRole.UNDECIDED);
//            userMentorshipEntity.setMentorshipStatus(MentorshipStatus.INACTIVE);

            userMentorshipRepository.save(userMentorshipEntity);

            logger.info("Updated profile for user: {}", userId);
        } catch (Exception e) {
            logger.error("Error processing user update for userId: {}", userId, e);
        }

    }

    @Transactional
    @KafkaListener(topics = "user-deletion", groupId = "mentorship-service", containerFactory = "userDeletionKafkaListenerContainerFactory")
    public void consumeUserDeletes(ConsumerRecord<String, Long> record) {

        Long userId = record.value();
        UserMentorshipEntity user = userMentorshipRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        try {
            userMentorshipRepository.deleteById(userId);

            List<MentorshipSessionEntity> mentorSessions = sessionRepository.findMentorshipSessionEntitiesByMentor(user);
            List<MentorshipSessionEntity> menteeSessions = sessionRepository.findMentorshipSessionEntitiesByMentee(user);
            sessionRepository.deleteAll(mentorSessions);
            for (MentorshipSessionEntity session : menteeSessions) {
                session.getMentees().remove(user);
                if (session.getMentees().isEmpty()) {
                    sessionRepository.delete(session);
                } else {
                    sessionRepository.save(session);
                }
            }

            List<MentorshipRequestEntity> requests = mentorshipRequestRepository.findAllByMentorIdOrMenteeId(userId, userId);
            mentorshipRequestRepository.deleteAll(requests);

            List<MentorshipRelationshipEntity> relationships = relationshipRepository.findAllByMentorIdOrMenteeId(userId, userId);
            relationshipRepository.deleteAll(relationships);
            logger.info("User deleted in mentorship service");
        } catch (Exception e) {
            logger.error("Error processing user mentorship deletion for userId: {}", userId, e);
        }

    }

    @Transactional
    public void selectUserRole(RoleSelectionDto roleSelectionDto) {
        UserMentorshipEntity user = userMentorshipRepository.findById(roleSelectionDto.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + roleSelectionDto.getUserId()));

        user.setRole(roleSelectionDto.getUserRole());
        userMentorshipRepository.save(user);
    }

    public UserMentorshipEntity getUserById(Long id) {
        UserMentorshipEntity user = userMentorshipRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        return user;
    }

    @Transactional
    public UserMentorshipInfoDto getUserMentorshipInfo(Long userId) {
        UserMentorshipEntity user = userMentorshipRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        UserMentorshipInfoDto dto = new UserMentorshipInfoDto();
        dto.setUserId(user.getId());
        dto.setRole(user.getRole());

        return dto;
    }

    @Transactional
    public UserMentorshipEntity toggleUserRole(Long userId) {
        UserMentorshipEntity user = userMentorshipRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.getRole() == UserRole.MENTOR) {
            user.setRole(UserRole.MENTEE);
        } else {
            user.setRole(UserRole.MENTOR);
        }

        return userMentorshipRepository.save(user);
    }

    public List<UserMentorshipEntity> searchUsers(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return userMentorshipRepository.searchUsers(searchTerm.trim());
    }
}
