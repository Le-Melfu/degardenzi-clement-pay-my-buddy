package com.paymybuddy.services.interfaces;

import com.paymybuddy.models.Transaction;

import java.util.List;

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
    Transaction createTransaction(Integer senderId, Integer receiverId, Long amountInCents, String description);

    /**
     * Confirm a pending transaction
     * 
     * @param transactionId the transaction ID
     * @return the confirmed transaction
     */
    Transaction confirmTransaction(Integer transactionId);

    /**
     * Cancel a pending transaction
     * 
     * @param transactionId the transaction ID
     * @return the cancelled transaction
     */
    Transaction cancelTransaction(Integer transactionId);

    /**
     * Get all transactions for a user (sent and received)
     * 
     * @param userId the user ID
     * @return list of user transactions
     */
    List<Transaction> getUserTransactions(Integer userId);

    /**
     * Get pending transactions for a user
     * 
     * @param userId the user ID
     * @return list of pending transactions
     */
    List<Transaction> getPendingTransactions(Integer userId);
}
