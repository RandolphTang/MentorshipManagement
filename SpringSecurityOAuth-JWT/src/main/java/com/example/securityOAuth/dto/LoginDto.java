package com.example.securityOAuth.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class LoginDto {

    private String email;
    private String password;

}
