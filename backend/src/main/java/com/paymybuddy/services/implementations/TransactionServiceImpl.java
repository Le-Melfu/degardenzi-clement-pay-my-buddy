package com.paymybuddy.services.implementations;

import java.util.List;

import org.springframework.stereotype.Service;

import com.paymybuddy.models.Transaction;
import com.paymybuddy.models.TransactionStatus;
import com.paymybuddy.exceptions.UserNotFoundException;
import com.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.services.interfaces.TransactionService;

import io.micrometer.common.lang.NonNull;

import com.paymybuddy.models.dtos.CreateTransactionRequestDTO;



@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Transaction createTransaction(CreateTransactionRequestDTO transactionRequest) {
        Transaction transaction = new Transaction();
        
        transaction.setStatus(TransactionStatus.PENDING);
        transactionRepository.save(transaction);
        return null;
    }

    @Override
    public Transaction confirmTransaction(Integer transactionId) {
        return null;
    }

    @Override
    public Transaction cancelTransaction(Integer transactionId) {
        return null;
    }

    @Override
    public Transaction getTransaction(@NonNull Integer transactionId){
        return transactionRepository.findById(transactionId);
    }

    @Override
    public List<Transaction> getUserTransactions(Integer userId) {
        return null;
    }

    @Override
    public List<Transaction> getPendingTransactions(Integer userId) {
        return null;
    }
}
