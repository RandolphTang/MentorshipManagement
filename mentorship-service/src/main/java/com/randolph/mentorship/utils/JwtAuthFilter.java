package com.randolph.mentorship.utils;


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String jwt = parseJwt(request);
            if(jwt != null && jwtUtils.validateJwtToken(jwt)) {
                Long userId = jwtUtils.getUserIdFromJwtToken(jwt);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userId,null, Collections.emptyList());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }else {
                logger.info("No valid JWT token found, uri: {}", request.getRequestURI());
            }
        } catch (ExpiredJwtException e) {
            request.setAttribute("expired", e.getMessage());
            logger.error("Cannot set user authentication: {}", e.getMessage());
        } catch (SignatureException e) {
            request.setAttribute("invalid", e.getMessage());
            logger.error("Cannot set user authentication: {}", e.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String jwt = null;
        String headerAuth = request.getHeader("Authorization");

        logger.info("Received Authorization header: {}", headerAuth);
        if(StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            jwt = headerAuth.substring(7);
            logger.info("JWT found in Authorization header");
        } else {
            logger.info("No JWT found in Authorization header");
        }

        if (jwt == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                logger.info("Number of cookies: {}", cookies.length);
                for (Cookie cookie : cookies) {
                    logger.info("Cookie name: {}, value: {}", cookie.getName(), cookie.getValue());
                    if ("jwt".equals(cookie.getName())) {
                        jwt = cookie.getValue();
                        logger.info("Found JWT in cookie");
                        break;
                    }
                }
            }else {
                logger.info("No cookies found in the request");
            }
        }

        if (jwt == null) {
            logger.info("No JWT found in either header or cookies");
        } else {
            logger.info("JWT found (first 10 characters): {}", jwt.substring(0, Math.min(jwt.length(), 10)));
        }

        return jwt;
    }
}