package com.mentorship.profile_service.controller;

import com.mentorship.profile_service.dto.RoleSelectionDto;
import com.mentorship.profile_service.dto.UserProfileDto;
import com.mentorship.profile_service.entity.UserProfileEntity;
import com.mentorship.profile_service.repository.UserProfileRepository;
import com.mentorship.profile_service.service.FileUploadService;
import com.mentorship.profile_service.service.ProfilePicService;
import com.mentorship.profile_service.service.UserProfileService;
import com.mentorship.profile_service.utils.JwtUtils;
import com.mentorship.shared.Enums.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/profiles")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private ProfilePicService profilePicService;

    private static final Logger logger = LoggerFactory.getLogger(UserProfileController.class);

    @PostMapping("/role")
    public ResponseEntity<UserProfileDto> selectRole(@RequestHeader("Authorization") String authHeader, @RequestBody RoleSelectionDto roleDto){
        logger.info("Received role selection request");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Invalid Authorization header");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = authHeader.replace("Bearer ", "");
        logger.debug("Extracted token: {}", token);
        Long userId = jwtUtils.getUserIdFromJwtToken(token);
        logger.info("Extracted userId: {}", userId);


        if (userId == null) {
            logger.warn("Failed to extract userId from token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserProfileEntity profile = userProfileService.createProfile(userId, roleDto.getRole());
        logger.info("Created profile for userId: {}", userId);
        return ResponseEntity.ok(convertToDto(profile));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileDto> getProfile(@PathVariable Long userId){
        UserProfileEntity profile = userProfileService.getProfile(userId);
        return ResponseEntity.ok(convertToDto(profile));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserProfileDto> updateProfile(@PathVariable Long userId, @RequestBody UserProfileDto profileDto){
        UserProfileEntity profile = convertToEntity(profileDto);
        UserProfileEntity existingProfile = userProfileRepository.findById(userId)
                .orElse(new UserProfileEntity());

        profile.setId(userId);
        profile.setProfilePicFilename(existingProfile.getProfilePicFilename());
        profile.setProfilePicUrl(existingProfile.getProfilePicUrl());
        profile = userProfileService.updateProfile(profile);

        return ResponseEntity.ok(convertToDto(profile));
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = fileUploadService.uploadFile(file);
            return ResponseEntity.ok("File uploaded successfully: " + fileName);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Could not upload the file: " + e.getMessage());
        }
    }

    @PostMapping("/upload-pic")
    public ResponseEntity<?> uploadProfilePic(@RequestParam("file") MultipartFile file,
                                              @RequestParam("userId") Long userId) {
        try {
            String fileName = profilePicService.updateProfilePic(userId, file);
            return ResponseEntity.ok("Profile picture updated: " + fileName);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Could not update profile picture: " + e.getMessage());
        }
    }

    @GetMapping("/pic/{userId}")
    public ResponseEntity<?> getProfilePic(@PathVariable Long userId) {
        try {
            byte[] imageData = profilePicService.getProfilePic(userId);
            if (imageData != null && imageData.length > 0) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(imageData);
            } else {
                return ResponseEntity.noContent().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving profile picture");
        }
    }

    @DeleteMapping("/pic/{userId}")
    public ResponseEntity<?> deleteProfilePic(@PathVariable Long userId) {
        try {
            profilePicService.deleteProfilePic(userId);
            return ResponseEntity.ok("Profile picture deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Could not delete profile picture: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserProfileDto>> searchProfiles(
            @RequestParam(required = false) UserRole role,
            @RequestParam String skill) {

        List<UserProfileEntity> profiles;
        if (role != null) {
            profiles = userProfileRepository.findByRoleAndSkillsContainingIgnoreCase(role, skill);
        } else {
            profiles = userProfileRepository.findBySkillsContainingIgnoreCase(skill);
        }

        List<UserProfileDto> profileDtos = profiles.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(profileDtos);
    }

    private UserProfileDto convertToDto(UserProfileEntity profile) {
        UserProfileDto dto = new UserProfileDto();
        dto.setUserId(profile.getId());
        dto.setRole(profile.getRole());
        dto.setUsername(profile.getUsername());
        dto.setEmail(profile.getEmail());
        dto.setBio(profile.getBio());
        dto.setSkills(profile.getSkills());
        dto.setInterests(profile.getInterests());
        return dto;
    }

    private UserProfileEntity convertToEntity(UserProfileDto dto) {
        UserProfileEntity profile = new UserProfileEntity();
        profile.setId(dto.getUserId());
        profile.setRole(dto.getRole());
        profile.setUsername(dto.getUsername());
        profile.setEmail(dto.getEmail());
        profile.setSkills(dto.getSkills());
        profile.setBio(dto.getBio());
        profile.setInterests(dto.getInterests());
        return profile;
    }


}
