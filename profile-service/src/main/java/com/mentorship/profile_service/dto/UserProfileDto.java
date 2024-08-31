package com.mentorship.profile_service.dto;

import com.mentorship.shared.Enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserProfileDto {


    private Long userId;
    private UserRole role;
    @NotBlank(message = "username is required")
    private String username;
    @Email(message = "Invalid email format")
    private String email;
    private String bio;
    private String skills;
    private String interests;
}
