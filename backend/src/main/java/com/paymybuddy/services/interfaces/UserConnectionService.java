package com.paymybuddy.services.interfaces;

import com.paymybuddy.models.UserConnection;

import java.util.List;

public interface UserConnectionService {

    /**
     * Add a connection between two users
     * 
     * @param userId       the user ID
     * @param connectionId the connection user ID
     * @return the created connection
     */
    UserConnection addConnection(Integer userId, String connectionEmail);

    /**
     * Get all connections for a user
     * 
     * @param userId the user ID
     * @return list of user connections
     */
    List<UserConnection> getConnections(Integer userId);
}
