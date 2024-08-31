package com.mentorship.profile_service.service;
import com.mentorship.profile_service.entity.UserProfileEntity;
import com.mentorship.profile_service.repository.UserProfileRepository;
import com.mentorship.shared.Enums.UserRole;
import com.mentorship.shared.events.UserCreatedEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserProfileService {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileService.class);

    @Autowired
    private UserProfileRepository userProfileRepository;

    @KafkaListener(topics = "user-created", groupId = "profile-service", containerFactory = "userCreatedKafkaListenerContainerFactory")
    //if not specifc, factory would use default
    public void consumeUserUpdates(ConsumerRecord<Long, UserCreatedEvent> record) {

        Long userId = record.key();
        UserCreatedEvent event = record.value();

        try {
            UserProfileEntity profile = userProfileRepository.findById(userId)
                    .orElse(new UserProfileEntity());

            profile.setId(userId);
            profile.setEmail(event.getEmail());
            profile.setUsername(event.getUsername());

            userProfileRepository.save(profile);

            logger.info("Updated profile for user: {}", userId);
        } catch (Exception e) {
            logger.error("Error processing user update for userId: {}", userId, e);
        }

    }

    @KafkaListener(topics = "user-deletion", groupId = "profilole-service", containerFactory = "userDeletionKafkaListenerContainerFactory")
    public void consumeUserDeletes(ConsumerRecord<String, Long> record) {

        Long userId = record.value();

        try {
            userProfileRepository.deleteById(userId);
            logger.info("User deleted in profile service");
        } catch (Exception e) {
            logger.error("Error processing user profile deletion for userId: {}", userId, e);
        }

    }

    public UserProfileEntity createProfile (Long userId, UserRole role){
        logger.info("Creating profile for user: {}", userId);
        UserProfileEntity profile = new UserProfileEntity();
        profile.setId(userId);
        profile.setRole(role);
        return userProfileRepository.save(profile);
    }

    public UserProfileEntity getProfile(Long userId) {
        return userProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found bro"));
    }

    public UserProfileEntity updateProfile(UserProfileEntity profile){
        return userProfileRepository.save(profile);
    }

}
