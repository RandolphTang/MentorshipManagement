package com.mentorship.profile_service.service;

import com.mentorship.profile_service.entity.UserProfileEntity;
import com.mentorship.profile_service.repository.UserProfileRepository;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ProfilePicService {

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Value("${spring.cloud.gcp.storage.bucket}")
    private String bucketName;

    public String updateProfilePic(Long userId, MultipartFile file) throws IOException {
        UserProfileEntity user = userProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getProfilePicFilename() != null) {
            fileUploadService.deleteFile(user.getProfilePicFilename());
        }

        String fileName = fileUploadService.uploadFile(file);

        user.setProfilePicFilename(fileName);
        user.setProfilePicUrl("https://storage.googleapis.com/" + bucketName + "/" + fileName);
        userProfileRepository.save(user);

        return fileName;
    }

    public byte[] getProfilePic(Long userId) throws IOException {
        UserProfileEntity user = userProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getProfilePicFilename() == null) {
            throw new RuntimeException("User has no profile picture");
        }

        return fileUploadService.getFile(user.getProfilePicFilename());
    }

    public void deleteProfilePic(Long userId) {
        UserProfileEntity user = userProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getProfilePicFilename() != null) {
            fileUploadService.deleteFile(user.getProfilePicFilename());
            user.setProfilePicFilename(null);
            user.setProfilePicUrl(null);
            userProfileRepository.save(user);
        }
    }
}
