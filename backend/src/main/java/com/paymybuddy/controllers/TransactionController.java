package com.paymybuddy.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.paymybuddy.models.Transaction;
import com.paymybuddy.models.dtos.CreateTransactionRequestDTO;
import com.paymybuddy.services.interfaces.TransactionService;

import io.micrometer.common.lang.NonNull;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transactions")
    public Transaction createTransaction(@RequestBody @Valid CreateTransactionRequestDTO request) {
        // TODO: check if user is authenticated
        return transactionService.createTransaction(request);
    }

    @GetMapping("/transactions/{transactionId}")
    public Transaction getTransaction(@PathVariable @NonNull Integer transactionId) {
        // TODO: check if user is authenticated
        return transactionService.getTransaction(transactionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"));
    }

}
