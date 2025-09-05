package com.paymybuddy.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.paymybuddy.models.dtos.AddConnectionRequestDTO;
import com.paymybuddy.models.dtos.PublicUserDTO;
import com.paymybuddy.models.User;
import com.paymybuddy.models.UserConnection;
import com.paymybuddy.services.interfaces.UserConnectionService;
import com.paymybuddy.services.interfaces.UserService;
import com.paymybuddy.logging.LoggingService;

import jakarta.validation.Valid;
import java.util.stream.Collectors;

@RestController
public class UserConnectionController {

    private final UserConnectionService userConnectionService;
    private final UserService userService;
    private final LoggingService loggingService;

    public UserConnectionController(UserConnectionService userConnectionService, UserService userService,
            LoggingService loggingService) {
        this.userConnectionService = userConnectionService;
        this.userService = userService;
        this.loggingService = loggingService;
    }

    @PostMapping("/add-connection")
    public ResponseEntity<PublicUserDTO> addConnection(
            @RequestBody @Valid AddConnectionRequestDTO request,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {

        String userEmail = principal.getUsername();
        loggingService.info("UserConnectionController: Looking for user with email: " + userEmail);
        Optional<User> user = userService.findByEmail(userEmail);
        loggingService.info("UserConnectionController: User found: " + user.get().getUsername());

        if (user.isEmpty() || user.get() == null) {
            loggingService.error("UserConnectionController: User not found with email: " + userEmail);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserConnection connection = userConnectionService.addConnection(user.get().getId(),
                request.getConnectionEmail());
        loggingService.info("UserConnectionController: Connection added: " + connection.getConnection().getUsername());
        return ResponseEntity.ok(new PublicUserDTO(connection.getConnection().getId(),
                connection.getConnection().getUsername(), connection.getConnection().getEmail()));
    }

    @GetMapping("/connections")
    public ResponseEntity<List<PublicUserDTO>> getConnections(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {

        String userEmail = principal.getUsername();
        Optional<User> user = userService.findByEmail(userEmail);

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<UserConnection> connections = userConnectionService.getConnections(user.get().getId());
        return ResponseEntity.ok(connections.stream()
                .map(connection -> new PublicUserDTO(connection.getConnection().getId(),
                        connection.getConnection().getUsername(), connection.getConnection().getEmail()))
                .collect(Collectors.toList()));
    }
}