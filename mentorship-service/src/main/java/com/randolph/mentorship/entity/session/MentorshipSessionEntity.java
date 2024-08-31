package com.randolph.mentorship.entity.session;

import com.mentorship.shared.Enums.SessionStatus;
import com.randolph.mentorship.entity.relationship.MentorshipRelationshipEntity;
import com.randolph.mentorship.entity.user.UserMentorshipEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mentorship_session_entity")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MentorshipSessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "mentor_id")
    private UserMentorshipEntity mentor;

    @ManyToMany
    @JoinTable(name = "session_mentees", joinColumns = @JoinColumn(name = "session_id"),
            inverseJoinColumns = @JoinColumn(name = "mentee_id")
    )
    private List<UserMentorshipEntity> mentees;

    @Column(name = "start_time")
    private LocalDateTime startTime;
    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private SessionStatus status;

    @Column(nullable = false)
    private LocalDateTime requestDate;

//    private String googleEventId;


    private String title;
    private String location;
    private String description;

}
