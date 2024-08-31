package com.example.securityOAuth.dto;


import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpDto {

    private String username;
    private String email;
    private String password;

}
