package com.paymybuddy.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.paymybuddy.logging.LoggingService;
import com.paymybuddy.models.dtos.UserCredentialsDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private LoggingService loggingService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthenticationService authenticationService;

    private UserCredentialsDTO credentials;

    @BeforeEach
    void setUp() {
        credentials = new UserCredentialsDTO();
        credentials.setEmail("test@example.com");
        credentials.setPassword("password123");
    }

    @Test
    void testAuthenticate_Successful() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(request.getSession(true)).thenReturn(session);

        boolean result = authenticationService.authenticate(credentials, request);

        assertTrue(result);
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(request, times(1)).getSession(true);
        verify(session, times(1)).setAttribute(eq("SPRING_SECURITY_CONTEXT"), any());
        verify(loggingService, times(1)).info(contains("Authenticating user: test@example.com"));
    }

    @Test
    void testAuthenticate_WithCorrectCredentials() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(request.getSession(true)).thenReturn(session);

        authenticationService.authenticate(credentials, request);

        verify(authenticationManager, times(1)).authenticate(
                argThat(token -> token.getPrincipal().equals("test@example.com") &&
                        token.getCredentials().equals("password123")));
    }

    @Test
    void testAuthenticate_Failed_BadCredentials() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        boolean result = authenticationService.authenticate(credentials, request);

        assertFalse(result);
        verify(request, never()).getSession(anyBoolean());
        verify(loggingService, times(1)).error(contains("Authentication failed for user: test@example.com"));
    }

    @Test
    void testAuthenticate_Failed_Exception() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Authentication error"));

        boolean result = authenticationService.authenticate(credentials, request);

        assertFalse(result);
        verify(request, never()).getSession(anyBoolean());
        verify(loggingService, times(1)).error(contains("Authentication failed"));
    }

    @Test
    void testAuthenticate_SessionCreated() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(request.getSession(true)).thenReturn(session);

        authenticationService.authenticate(credentials, request);

        verify(request, times(1)).getSession(true);
        verify(session, times(1)).setAttribute(eq("SPRING_SECURITY_CONTEXT"), any());
    }

    @Test
    void testAuthenticate_LoggingInfo() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(request.getSession(true)).thenReturn(session);

        authenticationService.authenticate(credentials, request);

        verify(loggingService, times(1)).info("AuthenticationService: Authenticating user: test@example.com");
        verify(loggingService, never()).error(anyString());
    }

    @Test
    void testAuthenticate_LoggingError() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        authenticationService.authenticate(credentials, request);

        verify(loggingService, times(1)).info(anyString());
        verify(loggingService, times(1)).error(contains("Authentication failed for user: test@example.com"));
    }

    @Test
    void testAuthenticate_DifferentUser() {
        credentials.setEmail("another@example.com");
        credentials.setPassword("differentPassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(request.getSession(true)).thenReturn(session);

        boolean result = authenticationService.authenticate(credentials, request);

        assertTrue(result);
        verify(loggingService, times(1)).info(contains("another@example.com"));
    }
}
