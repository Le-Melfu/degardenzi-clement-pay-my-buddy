package com.paymybuddy.services.implementations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.paymybuddy.logging.LoggingService;
import com.paymybuddy.models.User;
import com.paymybuddy.models.dtos.PublicUserDTO;
import com.paymybuddy.models.dtos.UserCredentialsDTO;
import com.paymybuddy.repository.UserRepository;

import jakarta.persistence.EntityManager;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private LoggingService loggingService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setUsername("testUser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setBalanceInCents(5000L);

        ReflectionTestUtils.setField(userService, "entityManager", entityManager);
    }

    @Test
    void testRegister_Successful() {
        User newUser = new User();
        newUser.setUsername("newUser");
        newUser.setEmail("newuser@example.com");
        newUser.setPassword("plainPassword");

        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1);
            return savedUser;
        });

        PublicUserDTO result = userService.register(newUser);

        assertNotNull(result);
        assertEquals("newUser", result.getUsername());
        assertEquals("newuser@example.com", result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
        verify(loggingService, times(1)).info(anyString());
    }

    @Test
    void testRegister_PasswordIsProperlyEncoded() {
        User newUser = new User();
        newUser.setUsername("newUser");
        newUser.setEmail("newuser@example.com");
        newUser.setPassword("plainPassword");

        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        userService.register(newUser);

        verify(passwordEncoder, times(1)).encode("plainPassword");
        assertEquals("encodedPassword", newUser.getPassword());
    }

    @Test
    void testRegister_InitialBalanceIsSet() {
        User newUser = new User();
        newUser.setUsername("newUser");
        newUser.setEmail("newuser@example.com");
        newUser.setPassword("plainPassword");
        newUser.setBalanceInCents(0L);

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        userService.register(newUser);

        assertEquals(10000L, newUser.getBalanceInCents());
        verify(userRepository, times(1)).save(argThat(user -> user.getBalanceInCents() == 10000L));
    }

    @Test
    void testRegister_RegistrationFailureHandling() {
        User newUser = new User();
        newUser.setUsername("newUser");
        newUser.setEmail("newuser@example.com");
        newUser.setPassword("plainPassword");

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.register(newUser);
        });

        assertEquals("Failed to register user", exception.getMessage());
        verify(loggingService, times(1)).info(anyString());
    }

    @Test
    void testLogin_Successful() {
        UserCredentialsDTO credentials = new UserCredentialsDTO();
        credentials.setEmail("test@example.com");
        credentials.setPassword("password123");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        Optional<PublicUserDTO> result = userService.login(credentials);

        assertTrue(result.isPresent());
        assertEquals(testUser.getId(), result.get().getId());
        assertEquals(testUser.getUsername(), result.get().getUsername());
        assertEquals(testUser.getEmail(), result.get().getEmail());
    }

    @Test
    void testLogin_InvalidEmail() {
        UserCredentialsDTO credentials = new UserCredentialsDTO();
        credentials.setEmail("invalid@example.com");
        credentials.setPassword("password123");

        when(userRepository.findByEmail("invalid@example.com")).thenReturn(Optional.empty());

        Optional<PublicUserDTO> result = userService.login(credentials);

        assertFalse(result.isPresent());
    }

    @Test
    void testAddMoney_BalanceUpdated() {
        testUser.setBalanceInCents(5000L);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.addMoney(testUser, 3000L);

        assertEquals(8000L, testUser.getBalanceInCents());
        verify(loggingService, times(1)).info(anyString());
    }

    @Test
    void testAddMoney_UserSavedInRepository() {
        testUser.setBalanceInCents(5000L);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.addMoney(testUser, 3000L);

        verify(userRepository, times(1)).save(argThat(user -> user.getId().equals(testUser.getId()) &&
                user.getBalanceInCents() == 8000L));
    }

    @Test
    void testFindByEmail_UserFound() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.findByEmail("test@example.com");

        assertTrue(result.isPresent());
        assertEquals(testUser.getId(), result.get().getId());
        assertEquals(testUser.getEmail(), result.get().getEmail());
    }

    @Test
    void testFindByEmail_UserNotFound() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        Optional<User> result = userService.findByEmail("notfound@example.com");

        assertFalse(result.isPresent());
    }

    @Test
    void testFindById_UserFound() {
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.findById(1);

        assertTrue(result.isPresent());
        assertEquals(testUser.getId(), result.get().getId());
        assertEquals(testUser.getUsername(), result.get().getUsername());
    }

    @Test
    void testFindById_UserNotFound() {
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        Optional<User> result = userService.findById(999);

        assertFalse(result.isPresent());
    }

    @Test
    void testUpdateUser_UpdateUsernameOnly() {
        User updatedUser = new User();
        updatedUser.setId(1);
        updatedUser.setUsername("updatedUsername");
        updatedUser.setEmail(testUser.getEmail());
        updatedUser.setPassword(testUser.getPassword());

        doNothing().when(userRepository).updateUsername(1, "updatedUsername");
        doNothing().when(entityManager).flush();
        doNothing().when(entityManager).clear();
        when(userRepository.findById(1)).thenReturn(Optional.of(updatedUser));

        User result = userService.updateUser(1, "updatedUsername", null, null);

        assertNotNull(result);
        assertEquals("updatedUsername", result.getUsername());
        verify(userRepository, times(1)).updateUsername(1, "updatedUsername");
        verify(userRepository, never()).updateEmail(anyInt(), anyString());
        verify(userRepository, never()).updatePassword(anyInt(), anyString());
    }

    @Test
    void testUpdateUser_UpdateEmailOnly() {
        User updatedUser = new User();
        updatedUser.setId(1);
        updatedUser.setUsername(testUser.getUsername());
        updatedUser.setEmail("newemail@example.com");
        updatedUser.setPassword(testUser.getPassword());

        doNothing().when(userRepository).updateEmail(1, "newemail@example.com");
        doNothing().when(entityManager).flush();
        doNothing().when(entityManager).clear();
        when(userRepository.findById(1)).thenReturn(Optional.of(updatedUser));

        User result = userService.updateUser(1, null, "newemail@example.com", null);

        assertNotNull(result);
        assertEquals("newemail@example.com", result.getEmail());
        verify(userRepository, never()).updateUsername(anyInt(), anyString());
        verify(userRepository, times(1)).updateEmail(1, "newemail@example.com");
        verify(userRepository, never()).updatePassword(anyInt(), anyString());
    }

    @Test
    void testUpdateUser_UpdatePasswordOnly() {
        User updatedUser = new User();
        updatedUser.setId(1);
        updatedUser.setUsername(testUser.getUsername());
        updatedUser.setEmail(testUser.getEmail());
        updatedUser.setPassword("encodedNewPassword");

        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        doNothing().when(userRepository).updatePassword(1, "encodedNewPassword");
        doNothing().when(entityManager).flush();
        doNothing().when(entityManager).clear();
        when(userRepository.findById(1)).thenReturn(Optional.of(updatedUser));

        User result = userService.updateUser(1, null, null, "newPassword");

        assertNotNull(result);
        verify(passwordEncoder, times(1)).encode("newPassword");
        verify(userRepository, never()).updateUsername(anyInt(), anyString());
        verify(userRepository, never()).updateEmail(anyInt(), anyString());
        verify(userRepository, times(1)).updatePassword(1, "encodedNewPassword");
    }

    @Test
    void testUpdateUser_UpdateAllFields() {
        User updatedUser = new User();
        updatedUser.setId(1);
        updatedUser.setUsername("newUsername");
        updatedUser.setEmail("newemail@example.com");
        updatedUser.setPassword("encodedNewPassword");

        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        doNothing().when(userRepository).updateUsername(1, "newUsername");
        doNothing().when(userRepository).updateEmail(1, "newemail@example.com");
        doNothing().when(userRepository).updatePassword(1, "encodedNewPassword");
        doNothing().when(entityManager).flush();
        doNothing().when(entityManager).clear();
        when(userRepository.findById(1)).thenReturn(Optional.of(updatedUser));

        User result = userService.updateUser(1, "newUsername", "newemail@example.com", "newPassword");

        assertNotNull(result);
        assertEquals("newUsername", result.getUsername());
        assertEquals("newemail@example.com", result.getEmail());
        verify(userRepository, times(1)).updateUsername(1, "newUsername");
        verify(userRepository, times(1)).updateEmail(1, "newemail@example.com");
        verify(userRepository, times(1)).updatePassword(1, "encodedNewPassword");
    }

    @Test
    void testUpdateUser_WithEmptyValues() {
        doNothing().when(entityManager).flush();
        doNothing().when(entityManager).clear();
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));

        User result = userService.updateUser(1, "", "", "");

        assertNotNull(result);
        verify(userRepository, never()).updateUsername(anyInt(), anyString());
        verify(userRepository, never()).updateEmail(anyInt(), anyString());
        verify(userRepository, never()).updatePassword(anyInt(), anyString());
    }

    @Test
    void testUpdateUser_UserNotFoundAfterUpdate() {
        doNothing().when(userRepository).updateUsername(1, "newUsername");
        doNothing().when(entityManager).flush();
        doNothing().when(entityManager).clear();
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUser(1, "newUsername", null, null);
        });

        assertEquals("Failed to update user", exception.getMessage());
        assertNotNull(exception.getCause());
        assertEquals("User not found after update", exception.getCause().getMessage());
        verify(loggingService, times(1)).error(anyString());
    }
}
