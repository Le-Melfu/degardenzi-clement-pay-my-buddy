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
     * Add money to a user
     * 
     * @param user          the user to add money to
     * @param amountInCents the amount to add
     */
    void addMoney(User user, Long amountInCents);

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

    /**
     * Update user information
     * 
     * @param userId   the user ID to update
     * @param username the new username (optional)
     * @param email    the new email (optional)
     * @param password the new password (optional)
     * @return the updated user
     */
    User updateUser(Integer userId, String username, String email, String password);
}
