package com.randolph.mentorship.entity.request;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.randolph.mentorship.entity.RequestStatus;
import com.randolph.mentorship.entity.user.UserMentorshipEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "mentorship_request_entity")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MentorshipRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "mentee_id", nullable = false)
    @JsonManagedReference
    private UserMentorshipEntity mentee;

    @ManyToOne
    @JoinColumn(name = "mentor_id", nullable = false)
    private UserMentorshipEntity mentor;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus requestStatus;

    @Column(nullable = false)
    private LocalDateTime requestDate;

    private String message;


    public MentorshipRequestEntity(UserMentorshipEntity mentee, UserMentorshipEntity mentor, String message, Long senderId, LocalDateTime requestDate, RequestStatus requestStatus) {
        this.mentee = mentee;
        this.mentor = mentor;
        this.message = message;
        this.senderId = senderId;
        this.requestDate = requestDate;
        this.requestStatus = requestStatus;
    }

}
