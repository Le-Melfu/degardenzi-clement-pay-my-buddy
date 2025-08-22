package com.paymybuddy.repository;

import com.paymybuddy.models.UserConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.lang.NonNull;

import java.util.List;

@Repository
public interface UserConnectionRepository extends JpaRepository<UserConnection, Integer> {

    /**
     * Find all connections for a specific user
     * 
     * @param userId the user ID
     * @return list of user connections
     */
    List<UserConnection> findByUserId(Integer userId);

    /**
     * Save a user connection
     * 
     * @param userConnection the connection to save
     * @return the saved connection
     */
    @Override
    @NonNull
    <S extends UserConnection> S save(@NonNull S userConnection);

}
