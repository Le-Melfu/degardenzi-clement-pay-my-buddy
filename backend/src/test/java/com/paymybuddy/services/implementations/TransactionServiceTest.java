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

import com.paymybuddy.logging.LoggingService;
import com.paymybuddy.models.Transaction;
import com.paymybuddy.models.User;
import com.paymybuddy.models.dtos.CreateTransactionRequestDTO;
import com.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LoggingService loggingService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private User sender;
    private User receiver;
    private CreateTransactionRequestDTO transactionRequest;

    @BeforeEach
    void setUp() {
        sender = new User();
        sender.setId(1);
        sender.setUsername("sender");
        sender.setEmail("sender@example.com");
        sender.setPassword("password123");
        sender.setBalanceInCents(10000L);

        receiver = new User();
        receiver.setId(2);
        receiver.setUsername("receiver");
        receiver.setEmail("receiver@example.com");
        receiver.setPassword("password123");
        receiver.setBalanceInCents(5000L);

        transactionRequest = new CreateTransactionRequestDTO();
        transactionRequest.setReceiverId(2);
        transactionRequest.setAmountInCents(1000L);
        transactionRequest.setDescription("Test transaction");
    }

    @Test
    void testCreateTransaction_Successful() {
        Transaction savedTransaction = new Transaction();
        savedTransaction.setId(1);
        savedTransaction.setSender(sender);
        savedTransaction.setReceiver(receiver);
        savedTransaction.setAmountInCents(1000L);
        savedTransaction.setDescription("Test transaction");

        when(userRepository.findById(2)).thenReturn(Optional.of(receiver));
        when(userRepository.save(any(User.class))).thenReturn(sender, receiver);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        Transaction result = transactionService.createTransaction(sender, transactionRequest);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(sender, result.getSender());
        assertEquals(receiver, result.getReceiver());
        assertEquals(1000L, result.getAmountInCents());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testCreateTransaction_InvalidAmountZero() {
        transactionRequest.setAmountInCents(0L);

        Transaction result = transactionService.createTransaction(sender, transactionRequest);

        assertNull(result);
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(loggingService, times(1)).error(contains("Amount must be greater than 0"));
    }

    @Test
    void testCreateTransaction_InvalidAmountNegative() {
        transactionRequest.setAmountInCents(-100L);

        Transaction result = transactionService.createTransaction(sender, transactionRequest);

        assertNull(result);
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(loggingService, times(1)).error(contains("Amount must be greater than 0"));
    }

    @Test
    void testCreateTransaction_InsufficientBalance() {
        sender.setBalanceInCents(500L);
        transactionRequest.setAmountInCents(1000L);

        when(userRepository.findById(2)).thenReturn(Optional.of(receiver));

        Transaction result = transactionService.createTransaction(sender, transactionRequest);

        assertNull(result);
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(loggingService, times(1)).error(contains("Solde insuffisant"));
    }

    @Test
    void testCreateTransaction_ReceiverNotFound() {
        when(userRepository.findById(2)).thenReturn(Optional.empty());

        Transaction result = transactionService.createTransaction(sender, transactionRequest);

        assertNull(result);
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(userRepository, never()).save(any(User.class));
        verify(loggingService, times(1)).error(contains("user not found with ID: 2"));
    }

    @Test
    void testCreateTransaction_BalancesUpdated() {
        Long initialSenderBalance = sender.getBalanceInCents();
        Long initialReceiverBalance = receiver.getBalanceInCents();
        Long transactionAmount = transactionRequest.getAmountInCents();

        Transaction savedTransaction = new Transaction();
        savedTransaction.setId(1);

        when(userRepository.findById(2)).thenReturn(Optional.of(receiver));
        when(userRepository.save(any(User.class))).thenReturn(sender, receiver);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        transactionService.createTransaction(sender, transactionRequest);

        assertEquals(initialSenderBalance - transactionAmount, sender.getBalanceInCents());
        assertEquals(initialReceiverBalance + transactionAmount, receiver.getBalanceInCents());
        verify(userRepository, times(2)).save(any(User.class));
    }

    @Test
    void testCreateTransaction_SavedInRepository() {
        Transaction savedTransaction = new Transaction();
        savedTransaction.setId(1);

        when(userRepository.findById(2)).thenReturn(Optional.of(receiver));
        when(userRepository.save(any(User.class))).thenReturn(sender, receiver);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        transactionService.createTransaction(sender, transactionRequest);

        verify(transactionRepository, times(1)).save(argThat(transaction -> transaction.getSender().equals(sender) &&
                transaction.getReceiver().equals(receiver) &&
                transaction.getAmountInCents().equals(1000L) &&
                transaction.getDescription().equals("Test transaction")));
    }

    @Test
    void testGetTransaction_Found() {
        Transaction transaction = new Transaction();
        transaction.setId(1);
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmountInCents(1000L);

        when(transactionRepository.findById(1)).thenReturn(Optional.of(transaction));

        Optional<Transaction> result = transactionService.getTransaction(1);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
        assertEquals(sender, result.get().getSender());
        assertEquals(receiver, result.get().getReceiver());
    }

    @Test
    void testGetTransaction_NotFound() {
        when(transactionRepository.findById(999)).thenReturn(Optional.empty());

        Optional<Transaction> result = transactionService.getTransaction(999);

        assertFalse(result.isPresent());
    }

    @Test
    void testGetUserTransactions_WithMultipleTransactions() {
        Transaction transaction1 = new Transaction();
        transaction1.setId(1);
        transaction1.setSender(sender);
        transaction1.setReceiver(receiver);
        transaction1.setAmountInCents(1000L);

        Transaction transaction2 = new Transaction();
        transaction2.setId(2);
        transaction2.setSender(sender);
        transaction2.setReceiver(receiver);
        transaction2.setAmountInCents(2000L);

        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);

        when(transactionRepository.findAllTransactionsForUser(1)).thenReturn(transactions);

        List<Transaction> result = transactionService.getUserTransactions(1);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1000L, result.get(0).getAmountInCents());
        assertEquals(2000L, result.get(1).getAmountInCents());
    }

    @Test
    void testGetUserTransactions_WithNoTransactions() {
        when(transactionRepository.findAllTransactionsForUser(1)).thenReturn(new ArrayList<>());

        List<Transaction> result = transactionService.getUserTransactions(1);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetUserTransactions_UserAsSenderAndReceiver() {
        User otherUser = new User();
        otherUser.setId(3);
        otherUser.setUsername("other");

        Transaction sentTransaction = new Transaction();
        sentTransaction.setId(1);
        sentTransaction.setSender(sender);
        sentTransaction.setReceiver(receiver);
        sentTransaction.setAmountInCents(1000L);

        Transaction receivedTransaction = new Transaction();
        receivedTransaction.setId(2);
        receivedTransaction.setSender(otherUser);
        receivedTransaction.setReceiver(sender);
        receivedTransaction.setAmountInCents(2000L);

        List<Transaction> transactions = Arrays.asList(sentTransaction, receivedTransaction);

        when(transactionRepository.findAllTransactionsForUser(1)).thenReturn(transactions);

        List<Transaction> result = transactionService.getUserTransactions(1);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(sender, result.get(0).getSender());
        assertEquals(sender, result.get(1).getReceiver());
    }
}
