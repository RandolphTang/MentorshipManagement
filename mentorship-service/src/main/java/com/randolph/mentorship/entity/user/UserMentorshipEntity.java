package com.randolph.mentorship.entity.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.mentorship.shared.Enums.MentorshipStatus;
import com.mentorship.shared.Enums.UserRole;
import com.randolph.mentorship.entity.relationship.MentorshipRelationshipEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name ="user_mentorship_entity")
@Data
public class UserMentorshipEntity {

    @Id
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name ="role")
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToMany(mappedBy = "mentor")
//    @JsonManagedReference
    @JsonIgnoreProperties("mentor")
    private List<MentorshipRelationshipEntity> menteeRelationships;

    @OneToOne(mappedBy = "mentee")
//    @JsonBackReference
    @JsonIgnoreProperties("mentee")
    @ToString.Exclude
    private MentorshipRelationshipEntity mentorRelationship;

}
