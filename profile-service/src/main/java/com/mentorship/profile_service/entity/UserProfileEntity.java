package com.mentorship.profile_service.entity;

import com.mentorship.shared.Enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name ="user_profile_entity")
@Data
public class UserProfileEntity {
    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private String username;
    private String email;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    @Column(name = "bio")
    private String bio;

    @Column(name = "skills")
    private String skills;

    @Column(name = "interests")
    private String interests;

    @Column(name = "profile_pic_filename")
    private String profilePicFilename;

    @Column(name = "profile_pic_url")
    private String profilePicUrl;
}


