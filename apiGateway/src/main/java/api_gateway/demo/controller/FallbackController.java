package api_gateway.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @GetMapping("/profileFallback")
    public ResponseEntity<String> profileFallback() {
        return ResponseEntity.ok("Profile service is currently unavailable. Please try again later.");
    }

    @GetMapping("/mentorshipFallback")
    public ResponseEntity<String> mentorshipFallback() {
        return ResponseEntity.ok("Mentorship service is currently unavailable. Please try again later.");
    }

    @GetMapping("/authenticationFallback")
    public ResponseEntity<String> authenticationFallback() {
        return ResponseEntity.ok("authentication service is currently unavailable. Please try again later.");
    }


}
