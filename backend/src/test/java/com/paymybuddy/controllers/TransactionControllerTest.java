package com.paymybuddy.controllers;

import static org.mockito.ArgumentMatchers.*;
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
import com.paymybuddy.models.Transaction;
import com.paymybuddy.models.User;
import com.paymybuddy.models.dtos.CreateTransactionRequestDTO;
import com.paymybuddy.services.interfaces.TransactionService;
import com.paymybuddy.services.interfaces.UserService;

@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private LoggingService loggingService;

    private User sender;
    private User receiver;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        sender = new User();
        sender.setId(1);
        sender.setUsername("sender");
        sender.setEmail("sender@example.com");
        sender.setBalanceInCents(10000L);

        receiver = new User();
        receiver.setId(2);
        receiver.setUsername("receiver");
        receiver.setEmail("receiver@example.com");
        receiver.setBalanceInCents(5000L);

        transaction = new Transaction();
        transaction.setId(1);
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmountInCents(1000L);
        transaction.setDescription("Test transaction");
    }

    @Test
    @WithMockUser(username = "sender@example.com")
    void testCreateTransaction_Success() throws Exception {
        CreateTransactionRequestDTO request = new CreateTransactionRequestDTO();
        request.setReceiverId(2);
        request.setAmountInCents(1000L);
        request.setDescription("Test transaction");

        when(userService.findByEmail("sender@example.com")).thenReturn(Optional.of(sender));
        when(transactionService.createTransaction(any(User.class), any(CreateTransactionRequestDTO.class)))
                .thenReturn(transaction);

        mockMvc.perform(post("/transaction")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amountInCents").value(1000))
                .andExpect(jsonPath("$.description").value("Test transaction"));
    }

    @Test
    @WithMockUser(username = "sender@example.com")
    void testCreateTransaction_UserNotFound() throws Exception {
        CreateTransactionRequestDTO request = new CreateTransactionRequestDTO();
        request.setReceiverId(2);
        request.setAmountInCents(1000L);
        request.setDescription("Test transaction");

        when(userService.findByEmail("sender@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(post("/transaction")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "sender@example.com")
    void testCreateTransaction_SelfTransaction() throws Exception {
        CreateTransactionRequestDTO request = new CreateTransactionRequestDTO();
        request.setReceiverId(1);
        request.setAmountInCents(1000L);
        request.setDescription("Test transaction");

        when(userService.findByEmail("sender@example.com")).thenReturn(Optional.of(sender));

        mockMvc.perform(post("/transaction")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "sender@example.com")
    void testGetTransaction_Success() throws Exception {
        when(userService.findByEmail("sender@example.com")).thenReturn(Optional.of(sender));
        when(transactionService.getTransaction(1)).thenReturn(Optional.of(transaction));

        mockMvc.perform(get("/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amountInCents").value(1000));
    }

    @Test
    @WithMockUser(username = "sender@example.com")
    void testGetTransaction_NotFound() throws Exception {
        when(userService.findByEmail("sender@example.com")).thenReturn(Optional.of(sender));
        when(transactionService.getTransaction(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/transactions/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "other@example.com")
    void testGetTransaction_Forbidden() throws Exception {
        User otherUser = new User();
        otherUser.setId(3);
        otherUser.setEmail("other@example.com");

        when(userService.findByEmail("other@example.com")).thenReturn(Optional.of(otherUser));
        when(transactionService.getTransaction(1)).thenReturn(Optional.of(transaction));

        mockMvc.perform(get("/transactions/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "sender@example.com")
    void testGetTransactions_Success() throws Exception {
        Transaction transaction2 = new Transaction();
        transaction2.setId(2);
        transaction2.setSender(sender);
        transaction2.setReceiver(receiver);
        transaction2.setAmountInCents(2000L);

        List<Transaction> transactions = Arrays.asList(transaction, transaction2);

        when(userService.findByEmail("sender@example.com")).thenReturn(Optional.of(sender));
        when(transactionService.getUserTransactions(1)).thenReturn(transactions);

        mockMvc.perform(get("/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].amountInCents").value(1000))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].amountInCents").value(2000));
    }

    @Test
    @WithMockUser(username = "sender@example.com")
    void testGetTransactions_EmptyList() throws Exception {
        when(userService.findByEmail("sender@example.com")).thenReturn(Optional.of(sender));
        when(transactionService.getUserTransactions(1)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
