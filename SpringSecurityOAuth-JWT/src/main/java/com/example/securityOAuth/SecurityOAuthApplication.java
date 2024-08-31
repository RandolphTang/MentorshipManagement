package com.example.securityOAuth;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;
import java.io.IOException;

@SpringBootApplication
public class SecurityOAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecurityOAuthApplication.class, args);
	}

}
