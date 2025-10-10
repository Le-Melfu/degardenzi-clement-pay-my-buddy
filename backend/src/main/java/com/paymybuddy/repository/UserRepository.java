package com.paymybuddy.repository;

import com.paymybuddy.models.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * Update a user's username
     */
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.username = :username WHERE u.id = :id")
    void updateUsername(@Param("id") Integer id, @Param("username") String username);

    /**
     * Update a user's email
     */
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.email = :email WHERE u.id = :id")
    void updateEmail(@Param("id") Integer id, @Param("email") String email);

    /**
     * Update a user's password
     */
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.password = :password WHERE u.id = :id")
    void updatePassword(@Param("id") Integer id, @Param("password") String password);

}
