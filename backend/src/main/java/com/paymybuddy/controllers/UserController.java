package com.paymybuddy.controllers;

import java.util.Optional;

import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import com.paymybuddy.models.User;
import com.paymybuddy.models.dtos.UserCredentialsDTO;
import com.paymybuddy.services.interfaces.UserService;
import com.paymybuddy.services.AuthenticationService;
import com.paymybuddy.models.dtos.PublicUserDTO;
import com.paymybuddy.logging.LoggingService;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
public class UserController {

    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final LoggingService loggingService;

    public UserController(UserService userService, AuthenticationService authenticationService,
            LoggingService loggingService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
        this.loggingService = loggingService;
    }

    @PostMapping("/register")
    public PublicUserDTO register(@RequestBody @Valid User user) {
        if (userService.findByEmail(user.getEmail()).isPresent()) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A user with this email already exists");
        }
        return userService.register(user);
    }

    @PostMapping("/login")
    public ResponseEntity<PublicUserDTO> login(@RequestBody @Valid UserCredentialsDTO userCredentials,
            HttpServletRequest request) {
        if (authenticationService.authenticate(userCredentials, request)) {
            Optional<PublicUserDTO> user = userService.login(userCredentials);
            return user.isPresent() ? ResponseEntity.ok(user.get())
                    : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        try {

            loggingService.info("UserController: Logout requested for user: " + principal.getUsername());
            org.springframework.security.core.context.SecurityContextHolder.clearContext();
            loggingService.info("UserController: Security context cleared");

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            loggingService.error("UserController: Logout error - " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
