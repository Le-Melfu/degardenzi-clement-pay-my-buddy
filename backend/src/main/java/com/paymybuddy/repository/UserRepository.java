package com.paymybuddy.repository;

import com.paymybuddy.models.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Find a user by email (case insensitive)
     * 
     * @param email the user email
     * @return optional containing the user if found
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email)")
    Optional<User> findByEmail(@Param("email") String email);

    /**
     * Get password for a specific email
     * 
     * @param email the user email
     * @return the password
     */
    @Query("SELECT u.password FROM User u WHERE u.email = :email")
    String getPassword(@Param("email") String email);

}
