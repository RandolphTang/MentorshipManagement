package com.example.securityOAuth.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class UserDto {

    private Long id;
    private String name;
    private String email;
    private String token;
}
