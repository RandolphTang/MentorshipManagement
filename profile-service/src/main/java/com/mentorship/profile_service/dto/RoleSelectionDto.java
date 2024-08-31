package com.mentorship.profile_service.dto;

import com.mentorship.shared.Enums.UserRole;
import lombok.Data;

@Data
public class RoleSelectionDto {
    private UserRole role;
}
