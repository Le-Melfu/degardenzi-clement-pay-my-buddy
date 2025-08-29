package com.paymybuddy.services.implementations;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.paymybuddy.models.User;
import com.paymybuddy.models.dtos.UserCredentialsDTO;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.services.interfaces.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User register(User user) {
        // TODO: use bcrypt to hash the password
        return userRepository.save(user);
    }

    @Override
    public Optional<User> login(UserCredentialsDTO userCredentials) {
        Optional<User> userOptional = userRepository.findByEmail(userCredentials.getEmail());
        // TODO: use bcrypt to hash the password
        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();
            if (existingUser.getPassword().equals(userCredentials.getPassword())) {
                return userOptional;
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
