package com.paymybuddy.controllers;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import com.paymybuddy.logging.LoggingService;
import com.paymybuddy.models.User;
import com.paymybuddy.models.dtos.PublicUserDTO;
import com.paymybuddy.services.AuthenticationService;
import com.paymybuddy.services.interfaces.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UserController.class)
@ContextConfiguration(classes = { UserController.class, UserControllerTest.TestSecurityConfig.class })
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AuthenticationService authenticationService;

    @MockitoBean
    private LoggingService loggingService;

    private User testUser;
    private PublicUserDTO publicUserDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setUsername("testUser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setBalanceInCents(10000L);

        publicUserDTO = new PublicUserDTO(1, "testUser", "test@example.com");
    }

    @Test
    void testRegister_Success() throws Exception {
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userService.register(any(User.class))).thenReturn(publicUserDTO);

        mockMvc.perform(post("/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void testRegister_EmailAlreadyExists() throws Exception {
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        mockMvc.perform(post("/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testGetUser_Success() throws Exception {
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testGetUser_Unauthorized() throws Exception {
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(get("/user"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testGetBalance_Success() throws Exception {
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/user/balance"))
                .andExpect(status().isOk())
                .andExpect(content().string("10000"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testGetBalance_Unauthorized() throws Exception {
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(get("/user/balance"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testAddMoney_Success() throws Exception {
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        doNothing().when(userService).addMoney(any(User.class), anyLong());

        mockMvc.perform(post("/add-money")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("5000"))
                .andExpect(status().isOk());

        verify(userService, times(1)).addMoney(any(User.class), eq(5000L));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testLogout_Success() throws Exception {
        mockMvc.perform(post("/log-out")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Configuration
    @EnableWebSecurity
    static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/register", "/login").permitAll()
                            .anyRequest().authenticated())
                    .csrf(csrf -> csrf.disable());
            return http.build();
        }
    }
}
