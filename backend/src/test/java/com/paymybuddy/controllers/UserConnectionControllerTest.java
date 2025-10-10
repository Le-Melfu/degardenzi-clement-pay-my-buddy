package com.paymybuddy.controllers;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymybuddy.logging.LoggingService;
import com.paymybuddy.models.User;
import com.paymybuddy.models.UserConnection;
import com.paymybuddy.models.dtos.AddConnectionRequestDTO;
import com.paymybuddy.services.interfaces.UserConnectionService;
import com.paymybuddy.services.interfaces.UserService;

@WebMvcTest(UserConnectionController.class)
public class UserConnectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserConnectionService userConnectionService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private LoggingService loggingService;

    private User testUser;
    private User connectionUser;
    private UserConnection userConnection;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setUsername("testUser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");

        connectionUser = new User();
        connectionUser.setId(2);
        connectionUser.setUsername("connectionUser");
        connectionUser.setEmail("connection@example.com");
        connectionUser.setPassword("password123");

        userConnection = new UserConnection();
        userConnection.setId(1);
        userConnection.setUser(testUser);
        userConnection.setConnection(connectionUser);
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testAddConnection_Success() throws Exception {
        AddConnectionRequestDTO request = new AddConnectionRequestDTO();
        request.setConnectionEmail("connection@example.com");

        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(userConnectionService.addConnection(1, "connection@example.com")).thenReturn(userConnection);

        mockMvc.perform(post("/add-connection")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.username").value("connectionUser"))
                .andExpect(jsonPath("$.email").value("connection@example.com"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testAddConnection_UserNotFound() throws Exception {
        AddConnectionRequestDTO request = new AddConnectionRequestDTO();
        request.setConnectionEmail("connection@example.com");

        when(userService.findByEmail("test@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(post("/add-connection")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testGetConnections_Success() throws Exception {
        User secondConnection = new User();
        secondConnection.setId(3);
        secondConnection.setUsername("secondConnection");
        secondConnection.setEmail("second@example.com");

        UserConnection userConnection2 = new UserConnection();
        userConnection2.setId(2);
        userConnection2.setUser(testUser);
        userConnection2.setConnection(secondConnection);

        List<UserConnection> connections = Arrays.asList(userConnection, userConnection2);

        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(userConnectionService.getConnections(1)).thenReturn(connections);

        mockMvc.perform(get("/connections"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].username").value("connectionUser"))
                .andExpect(jsonPath("$[1].id").value(3))
                .andExpect(jsonPath("$[1].username").value("secondConnection"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testGetConnections_UserNotFound() throws Exception {
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(get("/connections"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testGetConnections_EmptyList() throws Exception {
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(userConnectionService.getConnections(1)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/connections"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
