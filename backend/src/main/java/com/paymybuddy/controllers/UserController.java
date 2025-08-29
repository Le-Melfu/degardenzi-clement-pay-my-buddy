package com.paymybuddy.controllers;

import java.util.Optional;

import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.paymybuddy.models.User;
import com.paymybuddy.models.dtos.UserCredentialsDTO;
import com.paymybuddy.services.interfaces.UserService;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public User register(@RequestBody @Valid User user) {
        if (userService.findByEmail(user.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A user with this email already exists");
        }
        return userService.register(user);
    }

    @PostMapping("/login")
    public Optional<User> login(@RequestBody @Valid UserCredentialsDTO userCredentials) {
        if (!userService.findByEmail(userCredentials.getEmail()).isPresent()
                || !userService.login(userCredentials).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid credentials");
        }

        return userService.login(userCredentials);
    }
}
