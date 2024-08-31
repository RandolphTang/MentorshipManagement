//package com.example.securityOAuth.services;
//
//import com.example.securityOAuth.entity.User.UserEntity;
//import com.example.securityOAuth.events.UserUpdatedEvent;
//import org.springframework.context.ApplicationEventPublisher;
//import org.springframework.stereotype.Service;
//
//@Service
//public class UserEventPublisher {
//
//    private final ApplicationEventPublisher eventPublisher;
//
//    public UserEventPublisher(ApplicationEventPublisher eventPublisher){
//        this.eventPublisher = eventPublisher;
//    }
//
//    public void publishUserUpdatedEvent(UserEntity userEntity){
//        UserUpdatedEvent event = new UserUpdatedEvent(
//                userEntity.getId(),
//                userEntity.getEmail(),
//                userEntity.getUsername(),
//                userEntity.getRole()
//        );
//        eventPublisher.publishEvent(event);
//    }
//}

//for intra service communication

