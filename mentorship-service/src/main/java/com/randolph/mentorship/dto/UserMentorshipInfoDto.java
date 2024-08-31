package com.randolph.mentorship.dto;

import com.mentorship.shared.Enums.UserRole;
import lombok.Data;

@Data
public class UserMentorshipInfoDto {
    private Long userId;
    private UserRole role;
}
