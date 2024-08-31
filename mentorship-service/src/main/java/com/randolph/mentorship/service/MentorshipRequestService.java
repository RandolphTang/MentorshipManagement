package com.randolph.mentorship.service;

import com.mentorship.shared.Enums.MentorshipStatus;
import com.mentorship.shared.Enums.UserRole;
import com.randolph.mentorship.entity.RequestStatus;
import com.randolph.mentorship.entity.relationship.MentorshipRelationshipEntity;
import com.randolph.mentorship.entity.request.MentorshipRequestEntity;
import com.randolph.mentorship.entity.session.MentorshipSessionEntity;
import com.randolph.mentorship.entity.user.UserMentorshipEntity;
import com.randolph.mentorship.exception.RequestExistedException;
import com.randolph.mentorship.exception.RequestNotFoundException;
import com.randolph.mentorship.exception.UserNotFoundException;
import com.randolph.mentorship.repository.MentorshipRelationshipRepository;
import com.randolph.mentorship.repository.MentorshipRequestRepository;
import com.randolph.mentorship.repository.UserMentorshipRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MentorshipRequestService {

    @Autowired
    private MentorshipRequestRepository mentorshipRequestRepository;

    @Autowired
    private UserMentorshipRepository userMentorshipRepository;

    @Autowired
    private MentorshipRelationshipRepository mentorshipRelationshipRepository;

//    private static final Logger logger = LoggerFactory.getLogger(MentorshipSessionService.class);

    @Transactional
    public MentorshipRequestEntity createRequest(Long menteeId, Long mentorId, Long senderId, String message) {
        UserMentorshipEntity mentee = userMentorshipRepository.findById(menteeId)
                .orElseThrow(() -> new UserNotFoundException("Mentee not found"));
        UserMentorshipEntity mentor = userMentorshipRepository.findById(mentorId)
                .orElseThrow(() -> new UserNotFoundException("Mentor not found"));


        List<MentorshipRequestEntity> existingRequests = mentorshipRequestRepository.findAllByMentorIdAndMenteeId(mentorId, menteeId);
        if(!existingRequests.isEmpty()) {
            throw new RequestExistedException("A request already exists between this mentor and mentee");
        }

        MentorshipRequestEntity request = new MentorshipRequestEntity(mentee, mentor, message, senderId, LocalDateTime.now(), RequestStatus.PENDING);
        return mentorshipRequestRepository.save(request);
    }

    public List<MentorshipRequestEntity> getRequestsForMentee(Long menteeId) {
        UserMentorshipEntity mentee = userMentorshipRepository.findById(menteeId)
                .orElseThrow(() -> new UserNotFoundException("Mentee not found"));
        return mentorshipRequestRepository.findByMentee(mentee);
    }

    public List<MentorshipRequestEntity> getRequestsForMentor(Long mentorId) {
        UserMentorshipEntity mentor = userMentorshipRepository.findById(mentorId)
                .orElseThrow(() -> new UserNotFoundException("Mentor not found"));
        return mentorshipRequestRepository.findByMentor(mentor);
    }

    public RequestStatus getStatusForRequest(Long requestId) {
        MentorshipRequestEntity mentorshipRequestEntity = mentorshipRequestRepository.findById(requestId)
                .orElseThrow(() -> new UserNotFoundException("Request not found"));
        return mentorshipRequestRepository.findByRequestStatus(mentorshipRequestEntity);
    }

//    @Transactional
//    public MentorshipRequestEntity updateRequestStatus(Long requestId, RequestStatus newStatus) {
//        MentorshipRequestEntity request = mentorshipRequestRepository.findById(requestId)
//                .orElseThrow(() -> new RequestNotFoundException("Request not found"));
//
//        if (request.getRequestStatus() != RequestStatus.PENDING) {
//            throw new IllegalStateException("Can only update pending requests");
//        }
//
//        request.setRequestStatus(newStatus);
//        return mentorshipRequestRepository.save(request);
//    }

    @Transactional
    public MentorshipRequestEntity acceptRequest(Long requestId) {
        MentorshipRequestEntity request = mentorshipRequestRepository.findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException("Request not found with id: " + requestId));

        if (request.getRequestStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Can only accept pending requests");
        }

        request.setRequestStatus(RequestStatus.ACCEPTED);
        MentorshipRequestEntity savedRequest = mentorshipRequestRepository.save(request);

        UserMentorshipEntity mentor = userMentorshipRepository.findById(request.getMentor().getId())
                .orElseThrow(() -> new UserNotFoundException("Mentor not found with id: " + request.getMentor().getId()));
        UserMentorshipEntity mentee = userMentorshipRepository.findById(request.getMentee().getId())
                .orElseThrow(() -> new UserNotFoundException("Mentee not found with id: " + request.getMentee().getId()));

        if (mentorshipRelationshipRepository.findByMentee(mentee) != null) {
            throw new IllegalStateException("Mentee already has a mentor");
        }

        MentorshipRelationshipEntity relationship = new MentorshipRelationshipEntity();
        relationship.setMentor(mentor);
        relationship.setMentee(mentee);
        relationship.setStartDate(LocalDateTime.now());
        relationship.setStatus(MentorshipStatus.ACTIVE);
        MentorshipRelationshipEntity savedRelationship = mentorshipRelationshipRepository.save(relationship);

        mentor.getMenteeRelationships().add(savedRelationship);
        mentee.setMentorRelationship(savedRelationship);
        userMentorshipRepository.save(mentor);
        userMentorshipRepository.save(mentee);

        return savedRequest;
    }

    @Transactional
    public MentorshipRequestEntity rejectRequest(Long requestId) {
        MentorshipRequestEntity request = mentorshipRequestRepository.findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException("Request not found with id: " + requestId));

        if (request.getRequestStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Can only decline pending requests");
        }

        request.setRequestStatus(RequestStatus.REJECTED);
        return mentorshipRequestRepository.save(request);
    }

}
