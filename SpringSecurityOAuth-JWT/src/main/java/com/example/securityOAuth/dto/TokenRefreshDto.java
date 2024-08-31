package com.example.securityOAuth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class TokenRefreshDto {

    @NotBlank
    private String refreshToken;
}
