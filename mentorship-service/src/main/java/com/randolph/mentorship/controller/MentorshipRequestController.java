package com.randolph.mentorship.controller;

import com.randolph.mentorship.dto.NewRequestDto;
import com.randolph.mentorship.entity.RequestStatus;
import com.randolph.mentorship.entity.request.MentorshipRequestEntity;
import com.randolph.mentorship.exception.RequestExistedException;
import com.randolph.mentorship.exception.UserNotFoundException;
import com.randolph.mentorship.service.MentorshipRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mentorship/requests")
public class MentorshipRequestController {

    @Autowired
    private MentorshipRequestService mentorshipRequestService;

    @PostMapping
    public ResponseEntity<?> createRequest(@RequestBody NewRequestDto requestDto) {
        try {
            MentorshipRequestEntity newRequest = mentorshipRequestService.createRequest(
                    requestDto.getMenteeId(),
                    requestDto.getMentorId(),
                    requestDto.getSenderId(),
                    requestDto.getMessage()
            );
            return ResponseEntity.ok(newRequest);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RequestExistedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/mentee/{menteeId}")
    public ResponseEntity<List<MentorshipRequestEntity>> getRequestsForMentee(@PathVariable Long menteeId) {
        List<MentorshipRequestEntity> requests = mentorshipRequestService.getRequestsForMentee(menteeId);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/mentor/{mentorId}")
    public ResponseEntity<List<MentorshipRequestEntity>> getRequestsForMentor(@PathVariable Long mentorId) {
        List<MentorshipRequestEntity> requests = mentorshipRequestService.getRequestsForMentor(mentorId);
        return ResponseEntity.ok(requests);
    }

//    @PutMapping("/{requestId}/status")
//    public ResponseEntity<MentorshipRequestEntity> updateRequestStatus(@PathVariable Long requestId,
//                                                                 @RequestParam RequestStatus newStatus) {
//        MentorshipRequestEntity updatedRequest = mentorshipRequestService.updateRequestStatus(requestId, newStatus);
//        return ResponseEntity.ok(updatedRequest);
//    }

    @GetMapping("/{requestId}/status")
    public ResponseEntity<RequestStatus> updateRequestStatus(@PathVariable Long requestId) {
        RequestStatus requestStatus = mentorshipRequestService.getStatusForRequest(requestId);
        return ResponseEntity.ok(requestStatus);
    }

    @PostMapping("/{requestId}/accept")
    public ResponseEntity<MentorshipRequestEntity> acceptRequest(@PathVariable Long requestId) {
        MentorshipRequestEntity acceptedRequest = mentorshipRequestService.acceptRequest(requestId);
        return ResponseEntity.ok(acceptedRequest);
    }

    @PostMapping("/{requestId}/decline")
    public ResponseEntity<MentorshipRequestEntity> declineRequest(@PathVariable Long requestId) {
        MentorshipRequestEntity declinedRequest = mentorshipRequestService.rejectRequest(requestId);
        return ResponseEntity.ok(declinedRequest);
    }
}
