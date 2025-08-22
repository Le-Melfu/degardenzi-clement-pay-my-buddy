package com.paymybuddy.repository.interfaces;

import com.paymybuddy.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Find a user by email
     * 
     * @param email the user email
     * @return optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Get password for a specific email
     * 
     * @param email the user email
     * @return the password
     */
    String getPassword(String email);

}
