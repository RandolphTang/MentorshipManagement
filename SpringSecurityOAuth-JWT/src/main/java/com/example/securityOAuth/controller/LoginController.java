package com.example.securityOAuth.controller;


import com.example.securityOAuth.dto.*;
import com.example.securityOAuth.dto.LoginDto;
import com.example.securityOAuth.dto.securityChange.changePasswordDTO;
import com.example.securityOAuth.dto.securityChange.changeEmailDTO;
import com.example.securityOAuth.entity.RefreshToken.RefreshTokenEntity;
import com.example.securityOAuth.entity.User.UserEntity;
import com.example.securityOAuth.exception.EmailAlreadyExistsException;
import com.example.securityOAuth.exception.InvalidPasswordException;
import com.example.securityOAuth.repository.UserEntityRepository;
import com.example.securityOAuth.security.jwt.JwtUtils;
import com.example.securityOAuth.security.services.RefreshTokenService;
import com.example.securityOAuth.security.services.impl.UserDetailsImpl;
import com.example.securityOAuth.security.services.impl.UserDetailsServiceImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Controller
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserEntityRepository userEntityRepository;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @GetMapping("/checkLoginState")
    public ResponseEntity<?> checkLoginState(@RequestHeader("Authorization") String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            if (jwtUtils.validateJwtToken(token)) {

                String email = jwtUtils.getEmailFromJwtToken(token);

                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                Map<String, Object> response = new HashMap<>();
                response.put("isAuthenticated", true);
                response.put("user", userDetails);

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(401).body("Invalid or expired token");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(401).body("Invalid or expired token");
        }
    }

    @PostMapping("/Login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto loginDto) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = jwtUtils.generateJwtToken(authentication);

            System.out.println(jwt);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            String refreshToken = refreshTokenService.createRefreshToken(userDetails.getEmail()).getToken();

            ResponseCookie jwtCookie = ResponseCookie.from("jwt", jwt)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(Duration.ofHours(24))
                    .sameSite("Lax")
                    .build();

            ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                    .httpOnly(true)
                    .secure(true)
                    .path("/auth/refresh-token")
                    .maxAge(Duration.ofDays(7))
                    .sameSite("Lax")
                    .build();

            Date expirationDate = jwtUtils.getExpirationDateFromJwtToken(jwt);
            long expiresIn = (expirationDate.getTime() - System.currentTimeMillis()) / 1000;

            Map<String, Object> response = new HashMap<>();
            Map<String, Object> userResponse = new HashMap<>();
            userResponse.put("id", userDetails.getId());
            userResponse.put("email", userDetails.getEmail());
            userResponse.put("username", userDetails.getUsername());

            response.put("user", userResponse);
            response.put("expires_in", expiresIn);

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Authentication failed: " + e.getMessage());
        }

    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshDto request) {
        try {
            String requestRefreshToken = request.getRefreshToken();

            return refreshTokenService.findByToken(requestRefreshToken)
                    .map(refreshTokenService::verifyExpiration)
                    .map(RefreshTokenEntity::getEmail)
                    .map(email -> {
                        String jwt = jwtUtils.generateTokenFromEmail(email);
                        Date expirationDate = jwtUtils.getExpirationDateFromJwtToken(jwt);
                        long expiresIn = (expirationDate.getTime() - System.currentTimeMillis()) / 1000;

                        Map<String, Object> response = new HashMap<>();
                        response.put("access token", jwt);
                        response.put("refresh token", requestRefreshToken);

                        UserEntity user = userEntityRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));


                        Map<String, Object> userResponse = new HashMap<>();
                        userResponse.put("id", user.getId());
                        userResponse.put("email", user.getEmail());
                        userResponse.put("username", user.getUsername());

                        response.put("user", userResponse);
                        response.put("type", "Bearer");
                        response.put("expires_in", expiresIn);

                        return ResponseEntity.ok(response);
                    })
                    .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
        } catch (Exception e) {
            e.printStackTrace();  // Log the full stack trace for inspection
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token refresh failed: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpDto signUpDto) {

        if (userEntityRepository.existsByEmail(signUpDto.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponseDto("Error: Email is already in use!"));
        }

        UserEntity userEntity = new UserEntity(signUpDto.getUsername(),
                signUpDto.getEmail(),
                encoder.encode(signUpDto.getPassword()));

        userDetailsService.save(userEntity);

        String jwt = jwtUtils.generateTokenFromEmail(userEntity.getEmail());

        String refreshToken = refreshTokenService.createRefreshToken(userEntity.getEmail()).getToken();

        ResponseCookie jwtCookie = ResponseCookie.from("jwt", jwt)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofHours(24))
                .sameSite("Lax")
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/auth/refresh-token")
                .maxAge(Duration.ofDays(7))
                .sameSite("Lax")
                .build();


        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully!");
        response.put("userId", userEntity.getId());
        response.put("token", jwt);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(response);
    }

    @Transactional
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletRequest request,
                                        @CookieValue(name = "refreshToken", required = false) String refreshToken) {

        Cookie jwtCookie = WebUtils.getCookie(request, "jwt");
        if (jwtCookie == null) {
            return ResponseEntity.status(401).body("No JWT cookie found");
        }
        String token = jwtCookie.getValue();

        try {
            if (jwtUtils.validateJwtToken(token)) {
                String email = jwtUtils.getEmailFromJwtToken(token);
                userEntityRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("User not found"));

                refreshTokenService.deleteByEmail(email);
            } else {
                return ResponseEntity.status(401).body("Invalid or expired token");
            }

            ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                    .httpOnly(true)
                    .secure(true)
                    .path("/auth/refresh-token")
                    .maxAge(0)
                    .build();

            logger.info(cookie.toString());


            ResponseCookie jwt = ResponseCookie.from("jwt", "")
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(0)
                    .build();


            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .header(HttpHeaders.SET_COOKIE, jwt.toString())
                    .body("Logged out successfully");
        } catch (Exception e) {
            logger.error("Error during logout: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during logout");
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> getAuthStatus(HttpServletRequest request) {
        String jwt = jwtUtils.getJwtFromCookies(request);
        if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
            return ResponseEntity.ok().body("Authenticated");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
    }

    @Transactional
    @DeleteMapping("/deleteUser")
    public ResponseEntity<?> deleteUser(HttpServletRequest request){
        logger.info("Received user deletion request");

        String jwt = jwtUtils.getJwtFromCookies(request);


        try {
            if (jwtUtils.validateJwtToken(jwt)) {
                String email = jwtUtils.getEmailFromJwtToken(jwt);
                Long userId = jwtUtils.getUserIdFromJwtToken(jwt);
                userEntityRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("User not found"));

                userDetailsService.deleteUserById(userId);

                refreshTokenService.deleteByEmail(email);
            } else {
                return ResponseEntity.status(401).body("Invalid or expired token");
            }

            ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                    .httpOnly(true)
                    .secure(true)
                    .path("/auth/refresh-token")
                    .maxAge(0)
                    .build();

            logger.info(cookie.toString());


            ResponseCookie jwtCookie = ResponseCookie.from("jwt", "")
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(0)
                    .build();


            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .body("User Deleted successfully");
        } catch (Exception e) {
            logger.error("Error during logout: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during logout");
        }

    }

    @GetMapping("/{userId}/account-age")
    public ResponseEntity<Integer> getAccountAge(@PathVariable Long userId) {
        int accountAge = userDetailsService.getAccountAgeInDays(userId);
        return ResponseEntity.ok(accountAge);
    }

    @PutMapping("/{userId}/change-password")
    public ResponseEntity<?> changePassword(@PathVariable Long userId, @RequestBody changePasswordDTO changePasswordDTO) {
        try {
            userEntityRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            userDetailsService.changePassword(userId, changePasswordDTO);
            return ResponseEntity.ok().build();
        } catch (InvalidPasswordException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Current password is incorrect");
        } catch (EmailAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already in use");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while changing email");
        }
    }

    @PutMapping("/{userId}/change-email")
    public ResponseEntity<?> changeEmail(@PathVariable Long userId, @RequestBody changeEmailDTO changeEmailDTO) {
        try {
            userEntityRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            userDetailsService.changeEmail(userId, changeEmailDTO);
            return ResponseEntity.ok().build();
        } catch (InvalidPasswordException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Current password is incorrect");
        } catch (EmailAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already in use");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while changing email");
        }
    }

}
