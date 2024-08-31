package com.randolph.mentorship.controller;

import com.randolph.mentorship.dto.RoleSelectionDto;
import com.randolph.mentorship.dto.UserMentorshipInfoDto;
import com.randolph.mentorship.entity.user.UserMentorshipEntity;
import com.randolph.mentorship.exception.UserNotFoundException;
import com.randolph.mentorship.repository.UserMentorshipRepository;
import com.randolph.mentorship.service.UserMentorshipService;
import com.randolph.mentorship.utils.JwtUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import java.util.List;

@RestController
@RequestMapping("/mentorship/user")
public class UserMentorshipController {

    private static final Logger logger = LoggerFactory.getLogger(UserMentorshipController.class);

    @Autowired
    private UserMentorshipService userMentorshipService;

    @Autowired
    private UserMentorshipRepository userMentorshipRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/select-role")
    public ResponseEntity<?> selectRole(@RequestBody RoleSelectionDto roleSelectionDto, HttpServletRequest request) {


        try {
            Cookie jwtCookie = WebUtils.getCookie(request, "jwt");
            if (jwtCookie == null) {
                return ResponseEntity.status(401).body("No JWT cookie found");
            }
            String token = jwtCookie.getValue();

            if (jwtUtils.validateJwtToken(token)) {
                Long userId = jwtUtils.getUserIdFromJwtToken(token);
                userMentorshipRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found"));

                roleSelectionDto.setUserId(userId);

                userMentorshipService.selectUserRole(roleSelectionDto);

                return ResponseEntity.ok("Role selected successfully");
            } else {
                return ResponseEntity.status(401).body("Invalid or expired token");
            }
        } catch (Exception e) {
            logger.error("select role error", e);
            return ResponseEntity.status(500).body("Error selecting role: " + e.getMessage());
        }
    }

    @GetMapping("/info/{userId}")
    public ResponseEntity<UserMentorshipEntity> getUserMentorshipInfo(@PathVariable Long userId, HttpServletRequest request) {
        Cookie jwt = WebUtils.getCookie(request, "jwt");
        System.out.println("Received JWT: " + (jwt != null ? "Present" : "Not present"));
        UserMentorshipEntity info = userMentorshipService.getUserById(userId);
        System.out.println(info);
        return ResponseEntity.ok(info);
    }

    @PostMapping("/toggle-role/{userId}")
    public ResponseEntity<UserMentorshipEntity> toggleUserRole(@PathVariable Long userId) {
        try {
            UserMentorshipEntity updatedUser = userMentorshipService.toggleUserRole(userId);
            return ResponseEntity.ok(updatedUser);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/info/search")
    public ResponseEntity<?> searchUserMentorshipInfo(@RequestParam String term,
                                                      HttpServletRequest request) {
        Cookie jwt = WebUtils.getCookie(request, "jwt");
        System.out.println("Received JWT: " + (jwt != null ? "Present" : "Not present"));
        List<UserMentorshipEntity> users = userMentorshipService.searchUsers(term);
        return ResponseEntity.ok(users);
    }

//    @PostMapping("/{id}/toggle-role")
//    public ResponseEntity<UserMentorshipEntity> toggleUserRole(@PathVariable Long id) {
//        UserMentorshipEntity updatedUser = userMentorshipService.toggleMentorshipStatus(id);
//        return ResponseEntity.ok(updatedUser);
//    }
}
