package com.paymybuddy.services.interfaces;

import org.springframework.lang.NonNull;

import com.paymybuddy.models.User;

import java.util.Optional;

public interface UserService {

    /**
     * Register a new user
     * 
     * @param user the user to register
     * @return the registered user
     */
    @NonNull
    User register(User user);

    /**
     * Authenticate a user
     * 
     * @param email    the user email
     * @param password the user password
     * @return the authenticated user if successful
     */
    Optional<User> login(User user);

    /**
     * Update user profile
     * 
     * @param user the updated user data
     * @return the updated user
     */
    User updateProfile(User user);

    /**
     * Delete user account
     * 
     * @param userId the user ID to delete
     */
    void deleteAccount(Integer userId);

    /**
     * Find user by email
     * 
     * @param email the user email
     * @return the user if found
     */
    Optional<User> findByEmail(String email);
}
