//package com.randolph.mentorship.config;
//
//import com.google.api.client.auth.oauth2.Credential;
//import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
//import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
//import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
//import com.google.api.client.http.javanet.NetHttpTransport;
//import com.google.api.client.json.gson.GsonFactory;
//import com.google.api.services.calendar.Calendar;
//import com.google.api.services.calendar.CalendarScopes;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.core.io.Resource;
//
//import java.io.IOException;
//import java.security.GeneralSecurityException;
//import java.util.Collections;
//
//@Configuration
//public class GoogleCalendarConfig {
//
//
//    @Value("${google.application-name}")
//    private String applicationName;
//
//    @Value("${google.credentials-file-path}")
//    private String credentialsFilePath;
//
//    //server to server
//
//    @Bean
//    public Calendar calendarService() throws IOException, GeneralSecurityException {
//        Resource resource = new ClassPathResource(credentialsFilePath);
//
//        Credential credential = GoogleCredential.fromStream(resource.getInputStream())
//                .createScoped(Collections.singleton(CalendarScopes.CALENDAR));
//
//        return new Calendar.Builder(
//                GoogleNetHttpTransport.newTrustedTransport(),
//                GsonFactory.getDefaultInstance(),
//                credential)
//                .setApplicationName(applicationName)
//                .build();
//    }
//
//
////    @Value("{google.client.id}")
////    private String clientId;
////
////    @Value("${google.client.secret}")
////    private String clientSecret;
//
////    Oauth2 for user flow
////    @Bean
////    public GoogleAuthorizationCodeFlow authorizationCodeFlow() throws IOException {
////        return new GoogleAuthorizationCodeFlow.Builder(
////                new NetHttpTransport(),
////                GsonFactory.getDefaultInstance(),
////                clientId,
////                clientSecret,
////                Collections.singletonList(CalendarScopes.CALENDAR)
////                )
////                .setAccessType("offline")
////                .build();
////    }
//
//
//}
