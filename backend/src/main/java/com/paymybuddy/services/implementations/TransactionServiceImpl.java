package com.paymybuddy.services.implementations;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.paymybuddy.exceptions.InsufficientBalanceException;
import com.paymybuddy.exceptions.InvalidAmountException;
import com.paymybuddy.exceptions.UserNotFoundException;
import com.paymybuddy.logging.LoggingService;
import com.paymybuddy.models.Transaction;
import com.paymybuddy.models.TransactionStatus;
import com.paymybuddy.models.User;
import com.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.services.interfaces.TransactionService;

import io.micrometer.common.lang.NonNull;

import com.paymybuddy.models.dtos.CreateTransactionRequestDTO;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final LoggingService loggingService;

    public TransactionServiceImpl(TransactionRepository transactionRepository, UserRepository userRepository,
            LoggingService loggingService) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.loggingService = loggingService;
    }

    @Override
    public Transaction createTransaction(CreateTransactionRequestDTO transactionRequest) {
        Transaction savedTransaction = null;

        try {
            // Starting transaction creation
            loggingService.info("Starting transaction creation for amount: " + transactionRequest.getAmountInCents()
                    + " cents between user " + transactionRequest.getSenderId() + " and user "
                    + transactionRequest.getReceiverId());

            // Amount validation
            if (transactionRequest.getAmountInCents() <= 0) {
                throw new InvalidAmountException("Amount must be greater than 0");
            }

            // Get users first
            User sender = userRepository.findById(transactionRequest.getSenderId())
                    .orElseThrow(() -> new UserNotFoundException(
                            "Sender not found with ID: " + transactionRequest.getSenderId()));

            User receiver = userRepository.findById(transactionRequest.getReceiverId())
                    .orElseThrow(() -> new UserNotFoundException(
                            "Receiver not found with ID: " + transactionRequest.getReceiverId()));

            // Create transaction record with all required fields
            Transaction transaction = new Transaction();
            transaction.setStatus(TransactionStatus.PENDING);
            transaction.setAmountInCents(transactionRequest.getAmountInCents());
            transaction.setDescription(transactionRequest.getDescription());
            transaction.setSender(sender);
            transaction.setReceiver(receiver);

            // Save initial transaction record
            savedTransaction = transactionRepository.save(transaction);

            // Balance check
            if (sender.getBalanceInCents() < transactionRequest.getAmountInCents()) {
                savedTransaction.setStatus(TransactionStatus.FAILED);
                savedTransaction.setDescription("Insufficient balance. Available: " + sender.getBalanceInCents()
                        + " cents, Required: " + transactionRequest.getAmountInCents() + " cents");
                transactionRepository.save(savedTransaction);
                throw new InsufficientBalanceException("Insufficient balance. Available: " + sender.getBalanceInCents()
                        + " cents, Required: " + transactionRequest.getAmountInCents() + " cents");
            }

            // Balance check passed
            loggingService.info("Balance check passed. Sender balance: " + sender.getBalanceInCents() + " cents");

            // Update balances
            sender.setBalanceInCents(sender.getBalanceInCents() - transactionRequest.getAmountInCents());
            receiver.setBalanceInCents(receiver.getBalanceInCents() + transactionRequest.getAmountInCents());

            // Save users
            userRepository.save(sender);
            userRepository.save(receiver);

            // Balances updated
            loggingService.info("Balances updated. New sender balance: " + sender.getBalanceInCents()
                    + " cents, New receiver balance: " + receiver.getBalanceInCents() + " cents");

            // Update transaction to SUCCESS
            savedTransaction.setStatus(TransactionStatus.SUCCESS);
            savedTransaction.setDescription(transactionRequest.getDescription());
            Transaction finalTransaction = transactionRepository.save(savedTransaction);

            // Transaction created successfully
            loggingService.info("Transaction created successfully with ID: " + finalTransaction.getId());

            return finalTransaction;

        } catch (Exception e) {
            // Update transaction to FAILED if not already set
            if (savedTransaction != null && savedTransaction.getStatus() != TransactionStatus.FAILED) {
                savedTransaction.setStatus(TransactionStatus.FAILED);
                savedTransaction.setDescription("Transaction failed: " + e.getMessage());
                transactionRepository.save(savedTransaction);
                loggingService
                        .error("Transaction failed with ID: " + savedTransaction.getId() + " - " + e.getMessage());
            } else {
                loggingService.error("Transaction failed before creation - " + e.getMessage());
            }
            throw e;
        }
    }

    @Override
    public Optional<Transaction> getTransaction(@NonNull Integer transactionId) {
        return transactionRepository.findById(transactionId);
    }

    @Override
    public List<Transaction> getUserTransactions(Integer userId) {
        return transactionRepository.findAllTransactionsForUser(userId);
    }

}
