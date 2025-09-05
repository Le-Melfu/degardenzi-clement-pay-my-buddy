package com.paymybuddy.services.interfaces;

import com.paymybuddy.models.User;
import com.paymybuddy.models.dtos.UserCredentialsDTO;
import com.paymybuddy.models.dtos.PublicUserDTO;

import java.util.Optional;

public interface UserService {

    /**
     * Register a new user
     * 
     * @param user the user to register
     * @return the registered user
     */
    PublicUserDTO register(User user);

    /**
     * Authenticate a user
     * 
     * @param userCredentials the user credentials
     * @return the authenticated user if successful
     */
    Optional<PublicUserDTO> login(UserCredentialsDTO userCredentials);

    /**
     * Find user by ID
     * 
     * @param userId the user ID to check
     * @return the user if found
     */
    Optional<User> findById(Integer userId);

    /**
     * Find user by email
     * 
     * @param email the user email
     * @return the user if found
     */
    Optional<User> findByEmail(String email);
}
