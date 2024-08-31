package com.randolph.mentorship.entity.relationship;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.mentorship.shared.Enums.MentorshipStatus;
import com.randolph.mentorship.entity.user.UserMentorshipEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "mentorship_relationship")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MentorshipRelationshipEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "mentor_id", nullable = false)
    @JsonIgnoreProperties("menteeRelationships")
    @ToString.Exclude
    private UserMentorshipEntity mentor;

    @OneToOne
    @JoinColumn(name = "mentee_id", nullable = false, unique = true)
//    @JsonManagedReference
    @JsonIgnoreProperties("mentorRelationship")
    private UserMentorshipEntity mentee;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MentorshipStatus status;

    // Constructors, getters, and setters
}
