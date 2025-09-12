package com.paymybuddy.services.implementations;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

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
import org.springframework.transaction.annotation.Transactional;

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
    public Transaction createTransaction(User sender, CreateTransactionRequestDTO transactionRequest) {
        Transaction savedTransaction = null;

        try {
            loggingService.info("Starting transaction creation for amount: " + transactionRequest.getAmountInCents()
                    + " cents from user " + sender.getId() + " to user "
                    + transactionRequest.getReceiverId());

            // Amount validation
            if (transactionRequest.getAmountInCents() <= 0) {
                throw new InvalidAmountException("Amount must be greater than 0");
            }

            User receiver = userRepository.findById(transactionRequest.getReceiverId())
                    .orElseThrow(() -> new UserNotFoundException(
                            "user not found with ID: " + transactionRequest.getReceiverId()));

            Transaction transaction = new Transaction();
            transaction.setStatus(TransactionStatus.PENDING);
            transaction.setAmountInCents(transactionRequest.getAmountInCents());
            transaction.setDescription(transactionRequest.getDescription());
            transaction.setSender(sender);
            transaction.setReceiver(receiver);

            // Save transaction
            savedTransaction = transactionRepository.save(transaction);

            // Balance check
            if (sender.getBalanceInCents() < transactionRequest.getAmountInCents()) {
                // this message is sent to the client
                throw new InsufficientBalanceException("Solde insuffisant");
            }
            loggingService.info("Balance check passed. Sender balance: " + sender.getBalanceInCents() + " cents");

            // Update balances
            sender.setBalanceInCents(sender.getBalanceInCents() - transactionRequest.getAmountInCents());
            receiver.setBalanceInCents(receiver.getBalanceInCents() + transactionRequest.getAmountInCents());
            userRepository.save(sender);
            userRepository.save(receiver);
            loggingService.info("Balances updated. New sender balance: " + sender.getBalanceInCents()
                    + " cents, New receiver balance: " + receiver.getBalanceInCents() + " cents");

            savedTransaction.setStatus(TransactionStatus.SUCCESS);
            savedTransaction.setDescription(transactionRequest.getDescription());
            Transaction finalTransaction = transactionRepository.save(savedTransaction);
            loggingService.info("Transaction created successfully with ID: " + finalTransaction.getId());

            return finalTransaction;

        } catch (Exception e) {
            // Update transaction to FAILED if it exists
            if (savedTransaction != null) {
                savedTransaction.setStatus(TransactionStatus.FAILED);
                // this message is sent to the client
                savedTransaction.setDescription("Transaction échouée: " + e.getMessage());
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
