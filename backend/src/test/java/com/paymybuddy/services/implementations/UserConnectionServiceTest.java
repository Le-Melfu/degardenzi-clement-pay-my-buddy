package com.paymybuddy.services.implementations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.paymybuddy.exceptions.ConnectionException;
import com.paymybuddy.exceptions.UserNotFoundException;
import com.paymybuddy.logging.LoggingService;
import com.paymybuddy.models.User;
import com.paymybuddy.models.UserConnection;
import com.paymybuddy.repository.UserConnectionRepository;
import com.paymybuddy.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserConnectionServiceTest {

    @Mock
    private UserConnectionRepository userConnectionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LoggingService loggingService;

    @InjectMocks
    private UserConnectionServiceImpl userConnectionService;

    private User testUser;
    private User connectionUser;
    private UserConnection testConnection;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setUsername("testUser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setBalanceInCents(10000L);

        connectionUser = new User();
        connectionUser.setId(2);
        connectionUser.setUsername("connectionUser");
        connectionUser.setEmail("connection@example.com");
        connectionUser.setPassword("password123");
        connectionUser.setBalanceInCents(10000L);

        testConnection = new UserConnection();
        testConnection.setId(1);
        testConnection.setUser(testUser);
        testConnection.setConnection(connectionUser);
    }

    @Test
    void testAddConnection_Successful() {
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail("connection@example.com")).thenReturn(Optional.of(connectionUser));
        when(userConnectionRepository.findByUserId(1)).thenReturn(new ArrayList<>());
        when(userConnectionRepository.save(any(UserConnection.class))).thenReturn(testConnection);

        UserConnection result = userConnectionService.addConnection(1, "connection@example.com");

        assertNotNull(result);
        assertEquals(testUser, result.getUser());
        assertEquals(connectionUser, result.getConnection());
        verify(userConnectionRepository, times(1)).save(any(UserConnection.class));
        verify(loggingService, times(1)).info(anyString());
    }

    @Test
    void testAddConnection_UserNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userConnectionService.addConnection(1, "connection@example.com");
        });

        assertEquals("User not found", exception.getMessage());
        verify(userConnectionRepository, never()).save(any());
    }

    @Test
    void testAddConnection_ConnectionUserNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail("invalid@example.com")).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userConnectionService.addConnection(1, "invalid@example.com");
        });

        assertEquals("No user found with this email", exception.getMessage());
        verify(userConnectionRepository, never()).save(any());
        verify(loggingService, times(1)).error(anyString());
    }

    @Test
    void testAddConnection_CannotAddSelfAsConnection() {
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        ConnectionException exception = assertThrows(ConnectionException.class, () -> {
            userConnectionService.addConnection(1, "test@example.com");
        });

        assertEquals("Cannot add yourself as a connection", exception.getMessage());
        verify(userConnectionRepository, never()).save(any());
    }

    @Test
    void testAddConnection_ConnectionAlreadyExists() {
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail("connection@example.com")).thenReturn(Optional.of(connectionUser));
        when(userConnectionRepository.findByUserId(1)).thenReturn(Arrays.asList(testConnection));

        ConnectionException exception = assertThrows(ConnectionException.class, () -> {
            userConnectionService.addConnection(1, "connection@example.com");
        });

        assertEquals("Connection already exists", exception.getMessage());
        verify(userConnectionRepository, never()).save(any());
    }

    @Test
    void testAddConnection_VerifyConnectionSavedInRepository() {
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail("connection@example.com")).thenReturn(Optional.of(connectionUser));
        when(userConnectionRepository.findByUserId(1)).thenReturn(new ArrayList<>());
        when(userConnectionRepository.save(any(UserConnection.class))).thenReturn(testConnection);

        userConnectionService.addConnection(1, "connection@example.com");

        verify(userConnectionRepository, times(1)).save(argThat(connection -> connection.getUser().equals(testUser) &&
                connection.getConnection().equals(connectionUser)));
    }

    @Test
    void testGetConnections_UserWithMultipleConnections() {
        User secondConnection = new User();
        secondConnection.setId(3);
        secondConnection.setUsername("secondConnection");
        secondConnection.setEmail("second@example.com");

        UserConnection connection1 = new UserConnection();
        connection1.setId(1);
        connection1.setUser(testUser);
        connection1.setConnection(connectionUser);

        UserConnection connection2 = new UserConnection();
        connection2.setId(2);
        connection2.setUser(testUser);
        connection2.setConnection(secondConnection);

        List<UserConnection> connections = Arrays.asList(connection1, connection2);

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(userConnectionRepository.findByUserId(1)).thenReturn(connections);

        List<UserConnection> result = userConnectionService.getConnections(1);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(connectionUser, result.get(0).getConnection());
        assertEquals(secondConnection, result.get(1).getConnection());
        verify(userConnectionRepository, times(1)).findByUserId(1);
    }

    @Test
    void testGetConnections_UserWithNoConnections() {
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(userConnectionRepository.findByUserId(1)).thenReturn(new ArrayList<>());

        List<UserConnection> result = userConnectionService.getConnections(1);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userConnectionRepository, times(1)).findByUserId(1);
    }

    @Test
    void testGetConnections_UserNotFound() {
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userConnectionService.getConnections(999);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userConnectionRepository, never()).findByUserId(anyInt());
    }
}
