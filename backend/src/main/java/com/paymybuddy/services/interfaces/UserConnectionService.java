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
    UserConnection addConnection(Integer userId, Integer connectionId);

    /**
     * Remove a connection between two users
     * 
     * @param userId       the user ID
     * @param connectionId the connection user ID
     */
    void removeConnection(Integer userId, Integer connectionId);

    /**
     * Get all connections for a user
     * 
     * @param userId the user ID
     * @return list of user connections
     */
    List<UserConnection> getConnections(Integer userId);

    /**
     * Check if two users are connected
     * 
     * @param userId       the first user ID
     * @param connectionId the second user ID
     * @return true if users are connected
     */
    boolean isConnected(Integer userId, Integer connectionId);
}
