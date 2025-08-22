package com.paymybuddy.repository;

import com.paymybuddy.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

        /**
         * Find all transactions where user is sender
         * 
         * @param senderId the sender user ID
         * @return list of transactions sent by the user
         */
        List<Transaction> findBySenderId(Integer senderId);

        /**
         * Find all transactions where user is receiver
         * 
         * @param receiverId the receiver user ID
         * @return list of transactions received by the user
         */
        List<Transaction> findByReceiverId(Integer receiverId);

        /**
         * Find all transactions for a user (both sent and received)
         * 
         * @param userId the user ID
         * @return list of all transactions for the user
         */
        @Query("SELECT t FROM Transaction t WHERE t.sender.id = :userId OR t.receiver.id = :userId ORDER BY t.id DESC")
        List<Transaction> findAllTransactionsForUser(@Param("userId") Integer userId);

        /**
         * Find pending transactions for a user
         * 
         * @param userId the user ID
         * @return list of pending transactions
         */
        @Query("SELECT t FROM Transaction t WHERE (t.sender.id = :userId OR t.receiver.id = :userId) AND t.status = 'PENDING'")
        List<Transaction> findPendingTransactionsForUser(@Param("userId") Integer userId);

        /**
         * Find transaction by ID with validation status
         * 
         * @param transactionId the transaction ID
         * @return optional containing the transaction if found
         */
        Optional<Transaction> findByIdAndStatus(Integer transactionId, String status);

        /**
         * Create a new transaction between two users
         * 
         * @param senderId      the sender user ID
         * @param receiverId    the receiver user ID
         * @param amountInCents the amount in cents
         * @param description   the transaction description
         * @return the created transaction
         */
        @Query("INSERT INTO Transaction (sender_id, receiver_id, amount_in_cents, description, status) VALUES (:senderId, :receiverId, :amountInCents, :description, 'PENDING')")
        Transaction createTransaction(@Param("senderId") Integer senderId,
                        @Param("receiverId") Integer receiverId,
                        @Param("amountInCents") Long amountInCents,
                        @Param("description") String description);

        /**
         * Update transaction status
         * 
         * @param transactionId the transaction ID
         * @param status        the new status
         * @return the updated transaction
         */
        @Query("UPDATE Transaction t SET t.status = :status WHERE t.id = :transactionId")
        Transaction updateTransactionStatus(@Param("transactionId") Integer transactionId,
                        @Param("status") String status);

        /**
         * Save a transaction
         * 
         * @param transaction the transaction to save
         * @return the saved transaction
         */
        @Override
        @NonNull
        <S extends Transaction> S save(@NonNull S transaction);

}
