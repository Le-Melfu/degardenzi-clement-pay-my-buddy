package com.paymybuddy.services.interfaces;

import com.paymybuddy.models.Transaction;
import com.paymybuddy.models.User;
import com.paymybuddy.models.dtos.CreateTransactionRequestDTO;

import java.util.List;
import com.paymybuddy.models.dtos.PublicTransactionDTO;

public interface TransactionService {

    /**
     * Create a new transaction
     * 
     * @param sender             the sender user
     * @param transactionRequest the transaction request containing receiver ID,
     *                           amount, and description
     * @return the created transaction
     */
    Transaction createTransaction(User sender, CreateTransactionRequestDTO transactionRequest);

    /**
     * Get all transactions for a user (sent and received)
     * 
     * @param userId the user ID
     * @return list of user transactions
     */
    List<PublicTransactionDTO> getUserTransactions(Integer userId);
}
