package com.paymybuddy.services.interfaces;

import com.paymybuddy.models.Transaction;
import com.paymybuddy.models.dtos.CreateTransactionRequestDTO;

import java.util.List;
import java.util.Optional;

public interface TransactionService {

    /**
     * Create a new transaction (PENDING status)
     * 
     * @param senderId      the sender user ID
     * @param receiverId    the receiver user ID
     * @param amountInCents the amount in cents
     * @param description   the transaction description
     * @return the created transaction
     */
    Transaction createTransaction(CreateTransactionRequestDTO transactionRequest);

    /**
     * Get a specific transaction
     * 
     * @param transactionId
     * @return a specific transaction
     */
    Optional<Transaction> getTransaction(Integer transactionId);

    /**
     * Get all transactions for a user (sent and received)
     * 
     * @param userId the user ID
     * @return list of user transactions
     */
    List<Transaction> getUserTransactions(Integer userId);
}
