package com.paymybuddy.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.paymybuddy.models.User;
import com.paymybuddy.models.UserConnection;
import com.paymybuddy.models.dtos.AddConnectionRequestDTO;
import com.paymybuddy.services.interfaces.UserConnectionService;
import com.paymybuddy.services.interfaces.UserService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
public class UserConnectionController {

    private final UserConnectionService userConnectionService;
    private final UserService userService;

    public UserConnectionController(UserConnectionService userConnectionService, UserService userService) {
        this.userConnectionService = userConnectionService;
        this.userService = userService;
    }

    @PostMapping("/add-connection")
    public UserConnection addConnection(@RequestBody @Valid AddConnectionRequestDTO request) {

        // TODO: check if user is authenticated
        return userConnectionService.addConnection(request.getUser().getId(), request.getConnectionEmail());
    }

    @GetMapping("/connections/{userId}")
    public List<UserConnection> getConnections(@PathVariable Integer userId) {
        if (userService.findById(userId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        // TODO: check if user is authenticated
        return userConnectionService.getConnections(userId);
    }
}