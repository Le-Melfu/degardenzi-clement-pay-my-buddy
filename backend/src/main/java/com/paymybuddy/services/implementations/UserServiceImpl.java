package com.paymybuddy.services.implementations;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public UserServiceImpl(UserRepository userRepository, LoggingService loggingService,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.loggingService = loggingService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public PublicUserDTO register(User user) {
        try {
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
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findById(Integer userId) {
        return userRepository.findById(userId);
    }
}
