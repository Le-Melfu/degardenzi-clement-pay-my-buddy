package com.paymybuddy.services.implementations;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.paymybuddy.exceptions.ConnectionException;
import com.paymybuddy.exceptions.UserNotFoundException;
import com.paymybuddy.models.User;
import com.paymybuddy.models.UserConnection;
import com.paymybuddy.repository.UserConnectionRepository;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.services.interfaces.UserConnectionService;

@Service
public class UserConnectionServiceImpl implements UserConnectionService {

    private final UserConnectionRepository userConnectionRepository;
    private final UserRepository userRepository;

    public UserConnectionServiceImpl(UserConnectionRepository userConnectionRepository, UserRepository userRepository) {
        this.userConnectionRepository = userConnectionRepository;
        this.userRepository = userRepository;
    }

    @Override
    public UserConnection addConnection(Integer userId, String connectionEmail) {
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            throw new UserNotFoundException("User not found");
        }

        Optional<User> connectionUser = userRepository.findByEmail(connectionEmail);
        if (!connectionUser.isPresent()) {
            throw new UserNotFoundException("No user found with this email");
        }

        if (user.get().getId().equals(connectionUser.get().getId())) {
            throw new ConnectionException("Cannot add yourself as a connection");
        }

        List<UserConnection> existingConnections = userConnectionRepository.findByUserId(userId);
        for (UserConnection existingConnection : existingConnections) {
            if (existingConnection.getConnection().getId().equals(connectionUser.get().getId())) {
                throw new ConnectionException("Connection already exists");
            }
        }

        UserConnection newConnection = new UserConnection();
        newConnection.setUser(user.get());
        newConnection.setConnection(connectionUser.get());

        return userConnectionRepository.save(newConnection);
    }

    @Override
    public List<UserConnection> getConnections(Integer userId) {
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            throw new UserNotFoundException("User not found");
        }

        return userConnectionRepository.findByUserId(userId);
    }
}
