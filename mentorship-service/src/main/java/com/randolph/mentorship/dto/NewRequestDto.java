package com.randolph.mentorship.dto;

import com.mentorship.shared.Enums.UserRole;
import lombok.Data;

@Data
public class NewRequestDto {
    private Long menteeId;
    private Long mentorId;
    private String message;
    private Long senderId;
}
