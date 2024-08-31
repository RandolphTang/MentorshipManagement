package com.randolph.mentorship.controller;

import com.randolph.mentorship.entity.relationship.MentorshipRelationshipEntity;
import com.randolph.mentorship.service.MentorshipRelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mentorship/relationships")
public class MentorshipRelationshipController {

    @Autowired
    private MentorshipRelationshipService relationshipService;

    @PostMapping
    public ResponseEntity<MentorshipRelationshipEntity> createMentorshipRelationship(
            @RequestParam Long mentorId,
            @RequestParam Long menteeId) {
        MentorshipRelationshipEntity relationship = relationshipService.createMentorshipRelationship(mentorId, menteeId);
        return ResponseEntity.ok(relationship);
    }

    @GetMapping("/mentor/{mentorId}")
    public ResponseEntity<List<MentorshipRelationshipEntity>> getMentorshipsByMentor(@PathVariable Long mentorId) {
        List<MentorshipRelationshipEntity> relationships = relationshipService.getMentorshipsByMentor(mentorId);
        return ResponseEntity.ok(relationships);
    }

    @GetMapping("/mentee/{menteeId}")
    public ResponseEntity<MentorshipRelationshipEntity> getMentorshipByMentee(@PathVariable Long menteeId) {
        MentorshipRelationshipEntity relationship = relationshipService.getMentorshipByMentee(menteeId);
        return ResponseEntity.ok(relationship);
    }


}
