package com.example.securityOAuth.dto.securityChange;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class changeEmailDTO {
    private String password;
    private String newEmail;
}
