//package com.randolph.mentorship.service;
//
//import com.google.api.client.auth.oauth2.Credential;
//import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
//import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
//import com.google.api.client.http.javanet.NetHttpTransport;
//import com.google.api.client.json.JsonFactory;
//import com.google.api.client.json.gson.GsonFactory;
//import com.google.api.client.util.DateTime;
//import com.google.api.services.calendar.Calendar;
//import com.google.api.services.calendar.model.Event;
//import com.google.api.services.calendar.model.EventAttendee;
//import com.google.api.services.calendar.model.EventDateTime;
//import com.google.api.services.calendar.model.Events;
//import com.mentorship.shared.Enums.UserRole;
//import com.randolph.mentorship.dto.calendarRequest.EventDetailsDto;
//import com.randolph.mentorship.dto.calendarRequest.EventRecurrenceDto;
//import com.randolph.mentorship.dto.calendarRequest.EventRequestDto;
//import com.randolph.mentorship.entity.session.MentorshipSessionEntity;
//import com.randolph.mentorship.repository.MentorshipSessionRepository;
//import org.checkerframework.checker.units.qual.A;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.security.GeneralSecurityException;
//import java.util.*;
//import java.util.stream.Collectors;
//
//
//@Service
//public class GoogleCalendarService {
//
//    private static final Logger logger = LoggerFactory.getLogger(GoogleCalendarService.class);
//    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
//    private static final String PROJECT_CALENDAR_ID = "1f3a3f5572e3837ec862f08867655485e562ced012d2227ecd8244a1c20d5d7f@group.calendar.google.com";
//
////    @Autowired
////    private GoogleAuthorizationCodeFlow flow;
//
//    @Autowired
//    private Calendar calendarService;
//
////    public Calendar getCalendarService(String userId) throws IOException, GeneralSecurityException {
////        Credential credential = flow.loadCredential(userId);
////        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
////        return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
////                .setApplicationName("MentorShip")
////                .build();
////    }
//
//    public Event createMentorshipEvent(Event calendarEvent) throws IOException {
//        return calendarService.events().insert(PROJECT_CALENDAR_ID, calendarEvent).execute();
//    }
//
//    public Event updateMentorshipEvent(String eventId, Event calendarEvent) throws IOException {
//        return calendarService.events().update(PROJECT_CALENDAR_ID, eventId, calendarEvent).execute();
//    }
//
//    public void deleteMentorshipEvent(String eventId) throws IOException, GeneralSecurityException {
////        Calendar service = getCalendarService("dubmentorship@mentorship-431823.iam.gserviceaccount.com");
//        calendarService.events().delete(PROJECT_CALENDAR_ID, eventId).execute();
//    }
//
//    public Event getMentorshipEvent(String eventId) throws IOException {
//        return calendarService.events().get(PROJECT_CALENDAR_ID, eventId).execute();
//    }
//
//    public List<Event> getEventsForMentor(Long mentorId, DateTime timeMin, DateTime timeMax) throws IOException {
//        Events events = calendarService.events().list(PROJECT_CALENDAR_ID)
//                .setTimeMin(timeMin)
//                .setTimeMax(timeMax)
//                .setPrivateExtendedProperty(Collections.singletonList("mentorId=" + mentorId))
//                .execute();
//        return events.getItems();
//    }
//
//    public List<Event> getEventsForMentee(Long menteeId, DateTime timeMin, DateTime timeMax) throws IOException {
//        Events events = calendarService.events().list(PROJECT_CALENDAR_ID)
//                .setTimeMin(timeMin)
//                .setTimeMax(timeMax)
//                .setPrivateExtendedProperty(Collections.singletonList("menteeId=" + menteeId))
//                .execute();
//        return events.getItems();
//    }
//
//}