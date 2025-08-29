package com.paymybuddy.services.implementations;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.paymybuddy.models.User;
import com.paymybuddy.models.dtos.UserCredentialsDTO;
import com.paymybuddy.logging.LoggingService;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.services.interfaces.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final LoggingService loggingService;

    public UserServiceImpl(UserRepository userRepository, LoggingService loggingService) {
        this.userRepository = userRepository;
        this.loggingService = loggingService;
    }

    @Override
    public User register(User user) {
        // TODO: use bcrypt to hash the password
        try {
            return userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Failed to register user", e);
        } finally {
            loggingService.info("User " + user.getEmail() + " registered successfully");
        }
    }

    @Override
    public Optional<User> login(UserCredentialsDTO userCredentials) {
        Optional<User> user = userRepository.findByEmail(userCredentials.getEmail());
        // TODO: use bcrypt to hash the password
        if (user.isPresent()) {
            User existingUser = user.get();
            if (existingUser.getPassword().equals(userCredentials.getPassword())) {
                return user;
            }
        }
        return Optional.empty();
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
