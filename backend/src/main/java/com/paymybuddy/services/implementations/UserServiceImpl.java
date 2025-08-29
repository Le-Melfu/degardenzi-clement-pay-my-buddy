package com.paymybuddy.services.implementations;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.paymybuddy.models.User;
import com.paymybuddy.models.UserCredentials;
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
        return userRepository.save(user);
    }

    @Override
    public Optional<User> login(UserCredentials userCredentials) {
        Optional<User> userOptional = userRepository.findByEmail(userCredentials.getEmail());
        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();
            if (existingUser.getPassword().equals(userCredentials.getPassword())) {
                return userOptional;
            }
        }
        return Optional.empty();
    }

    @Override
    public User updateProfile(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteAccount(Integer userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
