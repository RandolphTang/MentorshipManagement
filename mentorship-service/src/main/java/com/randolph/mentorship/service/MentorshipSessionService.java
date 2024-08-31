package com.randolph.mentorship.service;//package com.randolph.mentorship.service;
//
//import com.google.api.client.util.DateTime;
//import com.google.api.services.calendar.model.Event;
//import com.google.api.services.calendar.model.EventDateTime;
//import com.mentorship.shared.Enums.MentorshipStatus;
//import com.randolph.mentorship.dto.calendarRequest.EventDetailsDto;
//import com.randolph.mentorship.dto.calendarRequest.EventRecurrenceDto;
//import com.randolph.mentorship.dto.calendarRequest.EventRequestDto;
//import com.randolph.mentorship.entity.relationship.MentorshipRelationshipEntity;
//import com.randolph.mentorship.entity.session.MentorshipSessionEntity;
//import com.randolph.mentorship.entity.user.UserMentorshipEntity;
//import com.randolph.mentorship.exception.UserNotFoundException;
//import com.randolph.mentorship.repository.MentorshipRelationshipRepository;
//import com.randolph.mentorship.repository.MentorshipSessionRepository;
//import com.randolph.mentorship.repository.UserMentorshipRepository;
//import jakarta.persistence.EntityNotFoundException;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.io.IOException;
//import java.security.GeneralSecurityException;
//import java.time.*;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class MentorshipSessionService {
//
//    @Autowired
//    private MentorshipSessionRepository sessionRepository;
//
////    @Autowired
////    private GoogleCalendarService calendarService;
//
//    @Autowired
//    private UserMentorshipService userMentorshipService;
//
//    @Autowired
//    private MentorshipRelationshipRepository mentorshipRelationshipRepository;
//
//    @Autowired
//    private UserMentorshipRepository userMentorshipRepository;
//
//    private static final Logger logger = LoggerFactory.getLogger(MentorshipSessionService.class);
//
//
//    public List<MentorshipSessionEntity> createSession(EventRequestDto eventRequestDto) throws IOException, GeneralSecurityException {
//
//        Long mentorId = eventRequestDto.getMentorId();
//        List<Long> menteeIds = eventRequestDto.getMenteeIds();
//        EventDetailsDto eventDetails = eventRequestDto.getEventDetails();
//        ZoneId timeZone = ZoneId.of(eventDetails.getStart().getTimeZone());
//
//        ZonedDateTime startZdt = ZonedDateTime.parse(eventDetails.getStart().getDateTime() + "[" + timeZone + "]");
//        ZonedDateTime endZdt = ZonedDateTime.parse(eventDetails.getEnd().getDateTime() + "[" + timeZone + "]");
//
//        List<UserMentorshipEntity> mentees = userMentorshipRepository.findAllById(menteeIds);
//        if (mentees.size() != menteeIds.size()) {
//            throw new UserNotFoundException("One or more mentees not found");
//        }
//
//        UserMentorshipEntity mentor = userMentorshipRepository.findById(mentorId)
//                .orElseThrow(() -> new UserNotFoundException("Mentor not found with id: " + mentorId));
//
//        for (UserMentorshipEntity mentee : mentees) {
//            MentorshipRelationshipEntity relationship = mentorshipRelationshipRepository.findByMentorAndMentee(mentor, mentee);
//            if (relationship == null || relationship.getStatus() != MentorshipStatus.ACTIVE) {
//                throw new IllegalStateException("No active mentorship relationship exists between mentor and mentee: " + mentee.getId());
//            }
//        }
//
//        String mentorEmail = mentor.getEmail();
//        List<String> menteeEmails = mentees.stream().map(UserMentorshipEntity::getEmail).collect(Collectors.toList());
//
//        if (endZdt.isBefore(startZdt) || endZdt.isEqual(startZdt)) {
//            throw new IllegalArgumentException("End time must be after start time");
//        }
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
//
//        Event calendarEvent = new Event()
//                .setSummary(eventDetails.getSummary())
//                .setDescription(eventDetails.getDescription() + "\nMentor: " + mentorEmail + "\nMentee: " +String.join(", ", menteeEmails))
//                .setStart(new EventDateTime()
//                        .setDateTime(new DateTime(startZdt.format(formatter)))
//                        .setTimeZone(timeZone.getId()))
//                .setEnd(new EventDateTime()
//                        .setDateTime(new DateTime(endZdt.format(formatter)))
//                        .setTimeZone(timeZone.getId()));
//
//        if (eventDetails.getLocation() != null && !eventDetails.getLocation().isEmpty()) {
//            calendarEvent.setLocation(eventDetails.getLocation());
//        }
//
//        if (eventDetails.getRecurrence() != null) {
//            EventRecurrenceDto recurrence = eventDetails.getRecurrence();
//            String recurrenceRule = formatRecurrenceRule(recurrence, timeZone);
//            System.out.println("Generated RRULE: " + recurrenceRule);
//            calendarEvent.setRecurrence(Collections.singletonList(recurrenceRule));
//        }
//
////        Event createdEvent = calendarService.createMentorshipEvent(calendarEvent);
//
//        List<MentorshipSessionEntity> sessionEntities = new ArrayList<>();
//        for (Long menteeId : menteeIds) {
//            MentorshipSessionEntity sessionEntity = new MentorshipSessionEntity();
//            sessionEntity.setMentorId(mentorId);
//            sessionEntity.setMenteeId(menteeId);
//            sessionEntity.setScheduledTime(new DateTime(eventDetails.getStart().getDateTime()).getValue());
//            sessionEntity.setGoogleEventId(createdEvent.getId());
//            sessionEntities.add(sessionEntity);
//        }
//
////        if (eventDetails.getMentorshipRelationshipId() != null) {
////            sessionEntity.setMentorshipRelationshipId(eventDetails.getMentorshipRelationshipId());
////        }
//
//        return sessionRepository.saveAll(sessionEntities);
//    }
//
//    public MentorshipSessionEntity updateSession(Long sessionId, EventRequestDto eventRequestDto) throws IOException, GeneralSecurityException {
//        MentorshipSessionEntity existingSession = sessionRepository.findById(sessionId)
//                .orElseThrow(() -> new EntityNotFoundException("Session not found"));
//
//        EventDetailsDto eventDetails = eventRequestDto.getEventDetails();
//        ZoneId timeZone = ZoneId.of(eventDetails.getStart().getTimeZone());
//
//        ZonedDateTime startZdt = ZonedDateTime.parse(eventDetails.getStart().getDateTime() + "[" + timeZone + "]");
//        ZonedDateTime endZdt = ZonedDateTime.parse(eventDetails.getEnd().getDateTime() + "[" + timeZone + "]");
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
//
//        Event event = new Event()
//                .setSummary(eventDetails.getSummary())
//                .setDescription(eventDetails.getDescription())
//                .setStart(new EventDateTime()
//                        .setDateTime(new DateTime(startZdt.format(formatter)))
//                        .setTimeZone(timeZone.getId()))
//                .setEnd(new EventDateTime()
//                        .setDateTime(new DateTime(endZdt.format(formatter)))
//                        .setTimeZone(timeZone.getId()));
//
//        if (eventDetails.getLocation() != null) {
//            event.setLocation(eventDetails.getLocation());
//        }
//
//        if (eventDetails.getRecurrence() != null) {
//            EventRecurrenceDto recurrence = eventDetails.getRecurrence();
//            String recurrenceRule = formatRecurrenceRule(recurrence, timeZone);
//            event.setRecurrence(Collections.singletonList(recurrenceRule));
//        }
//
////        calendarService.updateMentorshipEvent(existingSession.getGoogleEventId(), event);
//
//        existingSession.setScheduledTime(new DateTime(eventDetails.getStart().getDateTime()).getValue());
//
//        return sessionRepository.save(existingSession);
//    }
//
//    public void deleteSession(Long sessionId) throws IOException, GeneralSecurityException {
//        MentorshipSessionEntity session = sessionRepository.findById(sessionId)
//                .orElseThrow(() -> new EntityNotFoundException("Session not found"));
//
////        calendarService.deleteMentorshipEvent(session.getGoogleEventId());
//
//        sessionRepository.delete(session);
//    }
//
//    public MentorshipSessionEntity getSessionById(Long sessionId) {
//        return sessionRepository.findById(sessionId)
//                .orElseThrow(() -> new EntityNotFoundException("Session not found"));
//    }
//
//    public List<MentorshipSessionEntity> getSessionsForMentor(Long mentorId) {
//        return sessionRepository.findMentorshipSessionEntitiesByMentorId(mentorId);
//    }
//
//    public List<MentorshipSessionEntity> getSessionsForMentee(Long menteeId) {
//        return sessionRepository.findMentorshipSessionEntitiesByMenteeId(menteeId);
//    }
//
//    public List<MentorshipSessionEntity> getIncomingSessionsForMentor(Long mentorId) {
//        Long now = Instant.now().toEpochMilli();
//        return sessionRepository.findIncomingSessionsForMentor(mentorId, now);
//    }
//
//    public List<MentorshipSessionEntity> getIncomingSessionsForMentee(Long menteeId) {
//        Long now = Instant.now().toEpochMilli();
//        return sessionRepository.findIncomingSessionsForMentee(menteeId, now);
//    }
//
//    private String formatRecurrenceRule(EventRecurrenceDto recurrence, ZoneId timeZone) {
//        StringBuilder rrule = new StringBuilder("RRULE:FREQ=" + recurrence.getFrequency());
//
//        if (recurrence.getInterval() != null) {
//            rrule.append(";INTERVAL=").append(recurrence.getInterval());
//        }
//        if (recurrence.getCount() != null) {
//            rrule.append(";COUNT=").append(recurrence.getCount());
//        }
//        if (recurrence.getUntil() != null) {
//            LocalDateTime untilLocal = LocalDateTime.parse(recurrence.getUntil());
//            ZonedDateTime untilZdt = untilLocal.atZone(timeZone);
//            String formattedUntil = untilZdt.withZoneSameInstant(ZoneOffset.UTC)
//                    .format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'"));
//            rrule.append(";UNTIL=").append(formattedUntil);
//        }
//
//        return rrule.toString();
//    }
//
//    public List<MentorshipSessionEntity> getIncomingSessionsForUser(Long userId) {
//        Long now = Instant.now().toEpochMilli();
//        return sessionRepository.findIncomingSessionsForUser(userId, now);
//    }
//}


import com.mentorship.shared.Enums.SessionStatus;
import com.randolph.mentorship.entity.relationship.MentorshipRelationshipEntity;
import com.randolph.mentorship.entity.session.MentorshipSessionEntity;
import com.randolph.mentorship.entity.user.UserMentorshipEntity;
import com.randolph.mentorship.repository.MentorshipRelationshipRepository;
import com.randolph.mentorship.repository.MentorshipSessionRepository;
import com.randolph.mentorship.repository.UserMentorshipRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MentorshipSessionService {

    @Autowired
    private MentorshipSessionRepository mentorshipSessionRepository;

    @Autowired
    private UserMentorshipRepository userMentorshipRepository;

    @Autowired
    private MentorshipRelationshipRepository mentorshipRelationshipRepository;

    @Transactional
    public MentorshipSessionEntity createSession(MentorshipSessionEntity session) {
        UserMentorshipEntity mentor = session.getMentor();
        List<UserMentorshipEntity> mentees = session.getMentees();

        for (UserMentorshipEntity mentee : mentees) {
            MentorshipRelationshipEntity relationship = mentorshipRelationshipRepository.findByMentorAndMentee(mentor, mentee);
            if (relationship == null) {
                throw new IllegalStateException("Mentor and mentee are not in a mentorship relationship: Mentor ID " + mentor.getId() + ", Mentee ID " + mentee.getId());
            }
        }

        return mentorshipSessionRepository.save(session);
    }

    public List<MentorshipSessionEntity> getIncomingSessionsForUser(Long userId) {
        return mentorshipSessionRepository.findIncomingSessionsForUser(userId, LocalDateTime.now());
    }

    public List<MentorshipSessionEntity> getIncomingSessionsForMentor(Long mentorId) {
        return mentorshipSessionRepository.findIncomingSessionsForMentor(mentorId, LocalDateTime.now());
    }

    public List<MentorshipSessionEntity> getIncomingSessionsForMentee(Long menteeId) {
        return mentorshipSessionRepository.findIncomingSessionsForMentee(menteeId, LocalDateTime.now());
    }

    public SessionStatus getStatusForSession(Long sessionId) {
        MentorshipSessionEntity session = mentorshipSessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found with ID: " + sessionId));
        return session.getStatus();
    }

    @Transactional
    public MentorshipSessionEntity acceptSession(Long sessionId) {
        MentorshipSessionEntity session = mentorshipSessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found with ID: " + sessionId));

        if (session.getStatus() != SessionStatus.PENDING) {
            throw new IllegalStateException("Cannot accept a session that is not in PENDING status");
        }

        session.setStatus(SessionStatus.ACTIVE);
        return mentorshipSessionRepository.save(session);
    }

    @Transactional
    public MentorshipSessionEntity declineSession(Long sessionId, Long userId) {
        MentorshipSessionEntity session = mentorshipSessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));

        UserMentorshipEntity user = userMentorshipRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (session.getMentor().getId().equals(userId)) {
            return handleMentorDecline(session);
        } else if (session.getMentees().contains(user)) {
            return handleMenteeDecline(session, user);
        } else {
            throw new IllegalArgumentException("User is not a participant in this session");
        }
    }

    private MentorshipSessionEntity handleMentorDecline(MentorshipSessionEntity session) {
        session.setStatus(SessionStatus.CANCELLED);
        return mentorshipSessionRepository.save(session);
    }

    private MentorshipSessionEntity handleMenteeDecline(MentorshipSessionEntity session, UserMentorshipEntity mentee) {
        session.getMentees().remove(mentee);

        if (session.getMentees().isEmpty()) {
            session.setStatus(SessionStatus.CANCELLED);
        }

        return mentorshipSessionRepository.save(session);
    }

    public List<MentorshipSessionEntity> getSessionsDateBasedForUser(Long userId, LocalDateTime start, LocalDateTime end) {
        UserMentorshipEntity user = userMentorshipRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        System.out.println("Fetching sessions for user: " + userId);
        System.out.println("Start time: " + start);
        System.out.println("End time: " + end);

        List<MentorshipSessionEntity> sessions = mentorshipSessionRepository.findSessionsForUserInDateRange(userId, start, end);

        System.out.println("Found " + sessions.size() + " sessions");

        return sessions;
    }
}
