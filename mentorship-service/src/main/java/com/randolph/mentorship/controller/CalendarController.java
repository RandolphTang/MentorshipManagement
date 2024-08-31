package com.randolph.mentorship.controller;//package com.randolph.mentorship.controller;
//
//import com.google.api.client.util.DateTime;
//import com.google.api.services.calendar.model.Event;
//import com.google.api.services.calendar.model.EventDateTime;
//import com.mentorship.shared.Enums.UserRole;
//import com.randolph.mentorship.dto.calendarRequest.EventDetailsDto;
//import com.randolph.mentorship.dto.calendarRequest.EventRequestDto;
//import com.randolph.mentorship.entity.session.MentorshipSessionEntity;
//import com.randolph.mentorship.service.GoogleCalendarService;
//import com.randolph.mentorship.service.MentorshipSessionService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.IOException;
//import java.security.GeneralSecurityException;
//import java.util.List;
//
//@RestController
//@RequestMapping("/mentorship/calendar")
//public class CalendarController {
//
//    private static final Logger logger = LoggerFactory.getLogger(CalendarController.class);
//
//    @Autowired
//    private GoogleCalendarService googleCalendarService;
//
//    @Autowired
//    private MentorshipSessionService mentorshipSessionService;
//
//    @PostMapping("/create/events")
//    public ResponseEntity<List<MentorshipSessionEntity>> createEvent(@RequestBody EventRequestDto eventRequestDto) {
//        try {
//            List<MentorshipSessionEntity> createdSessions = mentorshipSessionService.createSession(eventRequestDto);
//            return ResponseEntity.ok(createdSessions );
//        } catch (IOException | GeneralSecurityException e) {
//            logger.error("Error creating event", e);
//            return ResponseEntity.internalServerError().build();
//        }
//    }
//
//    @GetMapping("/get/events")
//    public ResponseEntity<List<MentorshipSessionEntity>> getUserEvents(@RequestParam Long userId, @RequestParam UserRole userRole) {
//        List<MentorshipSessionEntity> sessions;
//        if (userRole == UserRole.MENTOR) {
//            sessions = mentorshipSessionService.getSessionsForMentor(userId);
//        } else {
//            sessions = mentorshipSessionService.getSessionsForMentee(userId);
//        }
//        return ResponseEntity.ok(sessions);
//    }
//
//    @GetMapping("/get/incoming-events")
//    public ResponseEntity<List<MentorshipSessionEntity>> getUserIncomingEvents(@RequestParam Long userId, @RequestParam UserRole userRole) {
//        List<MentorshipSessionEntity> sessions;
//        if (userRole == UserRole.MENTOR) {
//            sessions = mentorshipSessionService.getIncomingSessionsForMentor(userId);
//        } else {
//            sessions = mentorshipSessionService.getIncomingSessionsForMentee(userId);
//        }
//        return ResponseEntity.ok(sessions);
//    }
//
//    @PutMapping("/update/events/{sessionId}")
//    public ResponseEntity<MentorshipSessionEntity> updateEvent(@PathVariable Long sessionId, @RequestBody EventRequestDto eventRequestDto) {
//        try {
//            MentorshipSessionEntity updatedSession = mentorshipSessionService.updateSession(sessionId, eventRequestDto);
//            return ResponseEntity.ok(updatedSession);
//        } catch (IOException | GeneralSecurityException e) {
//            logger.error("Error updating event", e);
//            return ResponseEntity.internalServerError().build();
//        }
//    }
//
//
//    @DeleteMapping("/delete/events/{sessionId}")
//    public ResponseEntity<Void> deleteEvent(@PathVariable Long sessionId) {
//        try {
//            mentorshipSessionService.deleteSession(sessionId);
//            return ResponseEntity.noContent().build();
//        } catch (IOException | GeneralSecurityException e) {
//            logger.error("Error deleting event", e);
//            return ResponseEntity.internalServerError().build();
//        }
//    }
//
//}

import com.mentorship.shared.Enums.SessionStatus;
import com.randolph.mentorship.dto.EventRequestDto;
import com.randolph.mentorship.entity.relationship.MentorshipRelationshipEntity;
import com.randolph.mentorship.entity.session.MentorshipSessionEntity;
import com.randolph.mentorship.entity.user.UserMentorshipEntity;
import com.randolph.mentorship.repository.MentorshipRelationshipRepository;
import com.randolph.mentorship.repository.UserMentorshipRepository;
import com.randolph.mentorship.service.MentorshipSessionService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("/mentorship/calendar")
public class CalendarController {

    @Autowired
    private MentorshipSessionService mentorshipSessionService;

    @Autowired
    private UserMentorshipRepository userMentorshipRepository;

    @Autowired
    private MentorshipRelationshipRepository mentorshipRelationshipRepository;

    private static final Logger logger = LoggerFactory.getLogger(CalendarController.class);

    @PostMapping("/create/events")
    public ResponseEntity<MentorshipSessionEntity> createEvent(@RequestBody EventRequestDto eventRequestDto) {
        MentorshipSessionEntity session = convertToEntity(eventRequestDto);
        MentorshipSessionEntity createdSessions = mentorshipSessionService.createSession(session);
        return ResponseEntity.ok(createdSessions);
    }

    @GetMapping("/incoming/user/{userId}")
    public ResponseEntity<List<MentorshipSessionEntity>> getIncomingSessionsForUser(@PathVariable Long userId) {
        List<MentorshipSessionEntity> sessions = mentorshipSessionService.getIncomingSessionsForUser(userId);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/incoming/mentor/{mentorId}")
    public ResponseEntity<List<MentorshipSessionEntity>> getIncomingSessionsForMentor(@PathVariable Long mentorId) {
        List<MentorshipSessionEntity> sessions = mentorshipSessionService.getIncomingSessionsForMentor(mentorId);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/incoming/mentee/{menteeId}")
    public ResponseEntity<List<MentorshipSessionEntity>> getIncomingSessionsForMentee(@PathVariable Long menteeId) {
        List<MentorshipSessionEntity> sessions = mentorshipSessionService.getIncomingSessionsForMentee(menteeId);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/incoming/dateBased/user/{userId}/{start}/{end}")
    public ResponseEntity<List<MentorshipSessionEntity>> getSessionsDateBasedForUser(@PathVariable Long userId,
                                                                                     @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime  start,
                                                                                     @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime end) {
        System.out.println("Received request for user: " + userId);
        System.out.println("Start time: " + start);
        System.out.println("End time: " + end);

        LocalDateTime startLocal = start.toLocalDateTime();
        LocalDateTime endLocal = end.toLocalDateTime();
        List<MentorshipSessionEntity> sessions = mentorshipSessionService.getSessionsDateBasedForUser(userId, startLocal, endLocal);
        System.out.println("Found " + sessions.size() + " sessions");
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/{sessionId}/status")
    public ResponseEntity<SessionStatus> getSessionStatus(@PathVariable Long sessionId) {
        SessionStatus status = mentorshipSessionService.getStatusForSession(sessionId);
        return ResponseEntity.ok(status);
    }

    @PostMapping("/{sessionId}/accept")
    public ResponseEntity<MentorshipSessionEntity> acceptSession(@PathVariable Long sessionId) {
        MentorshipSessionEntity acceptedSession = mentorshipSessionService.acceptSession(sessionId);
        return ResponseEntity.ok(acceptedSession);
    }

    @PostMapping("/{sessionId}/{userId}/decline")
    public ResponseEntity<MentorshipSessionEntity> declineSession(@PathVariable Long sessionId, @PathVariable Long userId) {
        MentorshipSessionEntity declinedSession = mentorshipSessionService.declineSession(sessionId, userId);
        return ResponseEntity.ok(declinedSession);
    }

    private MentorshipSessionEntity convertToEntity(EventRequestDto eventRequestDto) {
        MentorshipSessionEntity entity = new MentorshipSessionEntity();
        entity.setStartTime(eventRequestDto.getStartTime());
        entity.setEndTime(eventRequestDto.getEndTime());
        entity.setTitle(eventRequestDto.getTitle());
        entity.setLocation(eventRequestDto.getLocation());
        entity.setDescription(eventRequestDto.getDescription());
        entity.setRequestDate(LocalDateTime.now());

        try {
            entity.setStatus(SessionStatus.valueOf(eventRequestDto.getStatus().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid session status: " + eventRequestDto.getStatus());
        }

        UserMentorshipEntity mentor = userMentorshipRepository.findById(eventRequestDto.getMentorId())
                .orElseThrow(() -> new EntityNotFoundException("Mentor not found with ID: " + eventRequestDto.getMentorId()));
        entity.setMentor(mentor);

        List<UserMentorshipEntity> mentees = userMentorshipRepository.findAllById(eventRequestDto.getMenteeIds());
        if (mentees.size() != eventRequestDto.getMenteeIds().size()) {
            throw new EntityNotFoundException("One or more mentees not found");
        }
        entity.setMentees(mentees);

        return entity;
    }


}