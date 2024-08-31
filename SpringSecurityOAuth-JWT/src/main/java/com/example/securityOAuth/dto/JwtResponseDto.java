package com.example.securityOAuth.dto;

import lombok.*;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class JwtResponseDto {

    private String token;
    private String type = "Bearer";
    private Long id;
    private String email;

    public JwtResponseDto(String accessToken, Long id, String email) {
        this.token = accessToken;
        this.id = id;
        this.email = email;
    }
}
