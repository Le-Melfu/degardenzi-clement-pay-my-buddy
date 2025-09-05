package com.paymybuddy.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.paymybuddy.models.dtos.UserCredentialsDTO;
import com.paymybuddy.logging.LoggingService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final LoggingService loggingService;

    public AuthenticationService(AuthenticationManager authenticationManager, LoggingService loggingService) {
        this.authenticationManager = authenticationManager;
        this.loggingService = loggingService;
    }

    public boolean authenticate(UserCredentialsDTO userCredentials, HttpServletRequest request) {
        try {
            loggingService.info("AuthenticationService: Authenticating user: " + userCredentials.getEmail());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userCredentials.getEmail(),
                            userCredentials.getPassword()));

            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            return true;
        } catch (Exception e) {
            loggingService.error("AuthenticationService: Authentication failed for user: " + userCredentials.getEmail()
                    + " - " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
