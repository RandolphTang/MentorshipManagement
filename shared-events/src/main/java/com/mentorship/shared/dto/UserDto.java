package com.mentorship.shared.dto;

import com.mentorship.shared.Enums.UserRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private Long userId;
    private String username;
    private String email;
    private UserRole role;
}
