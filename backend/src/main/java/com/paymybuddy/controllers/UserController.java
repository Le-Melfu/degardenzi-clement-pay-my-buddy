package com.paymybuddy.controllers;

import java.util.Optional;

import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import com.paymybuddy.models.User;
import com.paymybuddy.models.dtos.UserCredentialsDTO;
import com.paymybuddy.services.interfaces.UserService;
import com.paymybuddy.services.AuthenticationService;
import com.paymybuddy.models.dtos.PublicUserDTO;
import com.paymybuddy.models.dtos.UpdateUserRequest;
import com.paymybuddy.logging.LoggingService;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@Tag(name = "User Management", description = "API pour la gestion des utilisateurs")
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
    @Operation(summary = "Enregistrer un nouvel utilisateur", description = "Créer un nouveau compte utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur créé avec succès", content = @Content(schema = @Schema(implementation = PublicUserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Email déjà utilisé")
    })
    public PublicUserDTO register(@RequestBody @Valid User user) {
        if (userService.findByEmail(user.getEmail()).isPresent()) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A user with this email already exists");
        }
        return userService.register(user);
    }

    @PostMapping("/login")
    @Operation(summary = "Connexion utilisateur", description = "Authentifier un utilisateur existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Connexion réussie", content = @Content(schema = @Schema(implementation = PublicUserDTO.class))),
            @ApiResponse(responseCode = "401", description = "Identifiants invalides")
    })
    public ResponseEntity<PublicUserDTO> login(@RequestBody @Valid UserCredentialsDTO userCredentials,
            HttpServletRequest request) {
        if (authenticationService.authenticate(userCredentials, request)) {
            Optional<PublicUserDTO> user = userService.login(userCredentials);
            loggingService.info("UserController: User logged in: " + user.get().getUsername());
            return user.isPresent() ? ResponseEntity.ok(user.get())
                    : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/log-out")
    @Operation(summary = "Déconnexion utilisateur", description = "Déconnecter l'utilisateur actuellement connecté")
    @SecurityRequirement(name = "sessionAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Déconnexion réussie"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<Void> logout(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
            HttpServletRequest request) {
        try {
            if (principal != null) {
                loggingService.info("UserController: Logging out user: " + principal.getUsername());
            } else {
                loggingService.info("UserController: Logout requested but no user authenticated");
            }

            // Clear security context
            org.springframework.security.core.context.SecurityContextHolder.clearContext();

            // Invalidate session and clear cookie
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }

            return ResponseEntity.ok()
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception e) {
            loggingService.error("UserController: Logout error - " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    @GetMapping("/user")
    @Operation(summary = "Obtenir les informations de l'utilisateur connecté", description = "Obtenir les informations de l'utilisateur connecté")
    @SecurityRequirement(name = "sessionAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Informations de l'utilisateur obtenues avec succès"),
            @ApiResponse(responseCode = "401", description = "Utilisateur non authentifié")
    })
    public ResponseEntity<PublicUserDTO> getUser(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {

        Optional<User> user = userService.findByEmail(principal.getUsername());
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity
                .ok(new PublicUserDTO(user.get().getId(), user.get().getUsername(), user.get().getEmail()));
    }

    @PostMapping("/add-money")
    @Operation(summary = "Ajouter de l'argent au compte", description = "Ajouter un montant au solde de l'utilisateur connecté")
    @SecurityRequirement(name = "sessionAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Argent ajouté avec succès"),
            @ApiResponse(responseCode = "401", description = "Utilisateur non authentifié"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<Void> addMoney(@RequestBody @Valid Long amountInCents,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {

        try {
            Optional<User> user = userService.findByEmail(principal.getUsername());
            userService.addMoney(user.get(), amountInCents);
            if (user.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (Exception e) {
            loggingService.error("UserController: Add money error - " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/balance")
    public ResponseEntity<Long> getBalance(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        Optional<User> user = userService.findByEmail(principal.getUsername());
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(user.get().getBalanceInCents());
    }

    @PutMapping("/user")
    @Operation(summary = "Mettre à jour les informations de l'utilisateur", description = "Mettre à jour le nom d'utilisateur, l'email et/ou le mot de passe")
    @SecurityRequirement(name = "sessionAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur mis à jour avec succès", content = @Content(schema = @Schema(implementation = PublicUserDTO.class))),
            @ApiResponse(responseCode = "401", description = "Utilisateur non authentifié"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<PublicUserDTO> updateUser(@RequestBody @Valid UpdateUserRequest updateRequest,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
            HttpServletRequest request) {
        try {
            Optional<User> currentUser = userService.findByEmail(principal.getUsername());
            if (currentUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            boolean emailChanged = false;
            if (updateRequest.getEmail() != null && !updateRequest.getEmail().equals(currentUser.get().getEmail())) {
                Optional<User> existingUser = userService.findByEmail(updateRequest.getEmail());
                if (existingUser.isPresent()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                }
                emailChanged = true;
            }

            User updatedUser = userService.updateUser(
                    currentUser.get().getId(),
                    updateRequest.getUsername(),
                    updateRequest.getEmail(),
                    updateRequest.getPassword());

            // If email changed, invalidate session to force re-authentication with new
            // email
            if (emailChanged) {
                loggingService.info("UserController: Email changed, invalidating session for user ID: "
                        + currentUser.get().getId());
                org.springframework.security.core.context.SecurityContextHolder.clearContext();
                HttpSession session = request.getSession(false);
                if (session != null) {
                    session.invalidate();
                }
            }

            loggingService.info("UserController: User updated successfully - ID: " + currentUser.get().getId());
            return ResponseEntity
                    .ok(new PublicUserDTO(updatedUser.getId(), updatedUser.getUsername(), updatedUser.getEmail()));

        } catch (Exception e) {
            loggingService.error("UserController: Update user error - " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
