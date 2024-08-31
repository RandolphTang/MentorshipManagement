package com.randolph.mentorship.dto;

import com.mentorship.shared.Enums.UserRole;
import lombok.Data;

@Data
public class RoleSelectionDto {
    private Long userId;
    private UserRole userRole;
}
