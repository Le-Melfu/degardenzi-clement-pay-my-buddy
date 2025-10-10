package com.paymybuddy.services.implementations;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.paymybuddy.exceptions.InsufficientBalanceException;
import com.paymybuddy.exceptions.InvalidAmountException;
import com.paymybuddy.exceptions.UserNotFoundException;
import com.paymybuddy.logging.LoggingService;
import com.paymybuddy.models.Transaction;
import com.paymybuddy.models.User;
import com.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.services.interfaces.TransactionService;

import com.paymybuddy.models.dtos.CreateTransactionRequestDTO;
import com.paymybuddy.models.dtos.PublicTransactionDTO;
import com.paymybuddy.models.dtos.PublicUserDTO;
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
    @Transactional
    public Transaction createTransaction(User sender, CreateTransactionRequestDTO transactionRequest) {
        Transaction finalTransaction = null;
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

            // Balance check
            if (sender.getBalanceInCents() < transactionRequest.getAmountInCents()) {
                throw new InsufficientBalanceException("Solde insuffisant");
            }
            loggingService.info("Balance check passed. Sender balance: " + sender.getBalanceInCents() + " cents");

            // Create transaction
            Transaction transaction = new Transaction();
            transaction.setAmountInCents(transactionRequest.getAmountInCents());
            transaction.setDescription(transactionRequest.getDescription());
            transaction.setSender(sender);
            transaction.setReceiver(receiver);

            // Update balances
            sender.setBalanceInCents(sender.getBalanceInCents() - transactionRequest.getAmountInCents());
            receiver.setBalanceInCents(receiver.getBalanceInCents() + transactionRequest.getAmountInCents());
            userRepository.save(sender);
            userRepository.save(receiver);
            loggingService.info("Balances updated. New sender balance: " + sender.getBalanceInCents()
                    + " cents, New receiver balance: " + receiver.getBalanceInCents() + " cents");

            finalTransaction = transactionRepository.save(transaction);
            loggingService.info("Transaction created successfully with ID: " + finalTransaction.getId());
        } catch (Exception e) {
            loggingService.error("Transaction failed before creation - " + e.getMessage());
        }
        return finalTransaction;
    }

    @Override
    public List<PublicTransactionDTO> getUserTransactions(Integer userId) {
        return transactionRepository.findAllTransactionsForUser(userId).stream()
                .map(transaction -> new PublicTransactionDTO(transaction.getId(),
                        new PublicUserDTO(transaction.getSender().getId(), transaction.getSender().getUsername(),
                                transaction.getSender().getEmail()),
                        new PublicUserDTO(transaction.getReceiver().getId(), transaction.getReceiver().getUsername(),
                                transaction.getReceiver().getEmail()),
                        transaction.getDescription(), transaction.getAmountInCents()))
                .collect(Collectors.toList());
    }

}
