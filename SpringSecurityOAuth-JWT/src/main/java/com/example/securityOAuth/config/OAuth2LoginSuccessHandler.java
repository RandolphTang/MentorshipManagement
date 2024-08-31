package com.example.securityOAuth.config;



import com.example.securityOAuth.entity.User.UserEntity;
import com.example.securityOAuth.repository.UserEntityRepository;
import com.example.securityOAuth.security.jwt.JwtUtils;
import com.example.securityOAuth.security.services.RefreshTokenService;
import com.example.securityOAuth.security.services.UserService;
import com.example.securityOAuth.security.services.impl.UserDetailsServiceImpl;
import com.mentorship.shared.Enums.RegistrationSource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Value("${frontend.url}")
    private String frontendUrl;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {

        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = principal.getAttributes();

        String providerId = oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();
        String userNameAttributeName = providerId.equals("google") ? "sub" : "id";

//        if(providerId.equals("google")) System.out.println("Attributes from Google: " + attributes);
//        else System.out.println("Attributes from GitHub: " + attributes);

        Object emailObj = attributes.getOrDefault("email", "");
        String email = (emailObj == null) ? "" : emailObj.toString();

        Object nameObj = attributes.getOrDefault("name", "");
        String name = (nameObj == null) ? "" : nameObj.toString();

        try {
            System.out.println("is current user present");
            final boolean oldUser = userDetailsService.findByEmail(email).isPresent();
            UserEntity user = userDetailsService.findByEmail(email)
                    .orElseGet(() -> createNewUser(email, name, providerId));

            String jwt = jwtUtils.generateTokenFromEmail(user.getEmail());
            String refreshToken = refreshTokenService.createRefreshToken(user.getEmail()).getToken();

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

            response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            System.out.println("JWT Cookie set: " + jwtCookie.toString());

            String targetUrl = determineTargetUrl(oldUser, user);
            System.out.println("Target URL determined: " + targetUrl);
            this.setDefaultTargetUrl(targetUrl);

            super.onAuthenticationSuccess(request, response, authentication);
        } catch (Exception e) {
            logger.error("Error during OAuth2 login success handling", e);
            throw new ServletException("Authentication failed", e);
        }


//        userDetailsService.findByEmail(email)
//                .ifPresentOrElse(user -> {
//                    authenticateExistingUser(user, attributes, userNameAttributeName, oAuth2AuthenticationToken);
//                }, () ->{
//                    UserEntity userEntity = new UserEntity();
//                    userEntity.setEmail(email);
//                    userEntity.setUsername(name);
//                    userEntity.setSource(RegistrationSource.valueOf(providerId.toUpperCase()));
//                    userDetailsService.save(userEntity);
//
//                    DefaultOAuth2User newUser = new DefaultOAuth2User(Collections.singletonList(new SimpleGrantedAuthority("USER")),
//                            attributes, userNameAttributeName);
//
//
//                    Authentication securityAuth = new OAuth2AuthenticationToken(newUser, newUser.getAuthorities(),
//                            oAuth2AuthenticationToken.getAuthorizedClientRegistrationId());
//
//
//                    SecurityContextHolder.getContext().setAuthentication(securityAuth);
//                });
//
//        this.setDefaultTargetUrl("https://www.youtube.com/");
//        this.setAlwaysUseDefaultTargetUrl(false);
//        super.onAuthenticationSuccess(request, response, authentication);
    }

    private UserEntity createNewUser(String email, String name, String providerId) {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);
        userEntity.setUsername(name);
        userEntity.setSource(RegistrationSource.valueOf(providerId.toUpperCase()));
        userDetailsService.save(userEntity);
        return userEntity;
    }

    private String determineTargetUrl(boolean oldUser, UserEntity user) {
        if (!oldUser) {
            System.out.println("new");
            return UriComponentsBuilder.fromUriString(frontendUrl + "/select-role")
                    .queryParam("userId", user.getId())
                    .build().toUriString();
        } else {
            System.out.println("old");
            return UriComponentsBuilder.fromUriString(frontendUrl + "/mentorship")
                    .queryParam("userId", user.getId())
                    .build().toUriString();
        }
    }

}
