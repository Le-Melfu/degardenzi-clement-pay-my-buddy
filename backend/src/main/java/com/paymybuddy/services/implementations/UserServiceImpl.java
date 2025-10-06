package com.paymybuddy.services.implementations;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.paymybuddy.models.User;
import com.paymybuddy.models.dtos.UserCredentialsDTO;
import com.paymybuddy.models.dtos.PublicUserDTO;
import com.paymybuddy.logging.LoggingService;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.services.interfaces.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final LoggingService loggingService;
    private final PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager;

    public UserServiceImpl(UserRepository userRepository, LoggingService loggingService,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.loggingService = loggingService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public PublicUserDTO register(User user) {
        try {
            // TODO: Améliorer le chiffrement - Ajouter un salt personnalisé et vérifier la
            // force du mot de passe
            // Encode the password with BCrypt
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setBalanceInCents(10000L);
            user.setPassword(encodedPassword);
            userRepository.save(user);
            return new PublicUserDTO(user.getId(), user.getUsername(), user.getEmail());
        } catch (Exception e) {
            throw new RuntimeException("Failed to register user", e);
        } finally {
            loggingService.info("User " + user.getEmail() + " registered successfully");
        }
    }

    @Override
    public Optional<PublicUserDTO> login(UserCredentialsDTO userCredentials) {
        return userRepository.findByEmail(userCredentials.getEmail())
                .map(user -> new PublicUserDTO(user.getId(), user.getUsername(), user.getEmail()));
    }

    @Override
    public void addMoney(User user, Long amountInCents) {
        user.setBalanceInCents(user.getBalanceInCents() + amountInCents);
        userRepository.save(user);
        loggingService.info("UserService: Money added to user ID: " + user.getId() + " amount: " + amountInCents);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findById(Integer userId) {
        return userRepository.findById(userId);
    }

    @Override
    @Transactional
    public User updateUser(Integer userId, String username, String email, String password) {
        try {
            // Update username if provided
            if (username != null && !username.trim().isEmpty()) {
                userRepository.updateUsername(userId, username.trim());
            }

            // Update email if provided
            if (email != null && !email.trim().isEmpty()) {
                userRepository.updateEmail(userId, email.trim());
            }

            // Update password if provided
            if (password != null && !password.trim().isEmpty()) {
                String encodedPassword = passwordEncoder.encode(password.trim());
                userRepository.updatePassword(userId, encodedPassword);
            }

            // Force flush to ensure all updates are written to database
            entityManager.flush();
            entityManager.clear();

            // Get updated user
            Optional<User> updatedUser = userRepository.findById(userId);
            if (updatedUser.isEmpty()) {
                throw new RuntimeException("User not found after update");
            }

            loggingService.info("UserService: User updated successfully - ID: " + userId +
                    (username != null ? ", Username: " + username : "") +
                    (email != null ? ", Email: " + email : "") +
                    (password != null ? ", Password: updated" : ""));
            return updatedUser.get();
        } catch (Exception e) {
            loggingService.error("UserService: Failed to update user ID: " + userId + " - " + e.getMessage());
            throw new RuntimeException("Failed to update user", e);
        }
    }
}
