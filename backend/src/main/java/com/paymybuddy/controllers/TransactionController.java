package com.paymybuddy.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.paymybuddy.models.Transaction;
import com.paymybuddy.models.User;
import com.paymybuddy.models.dtos.CreateTransactionRequestDTO;
import com.paymybuddy.services.interfaces.TransactionService;
import com.paymybuddy.services.interfaces.UserService;
import com.paymybuddy.logging.LoggingService;

import io.micrometer.common.lang.NonNull;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.Optional;
import java.util.List;

@RestController
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;
    private final LoggingService loggingService;

    public TransactionController(TransactionService transactionService, UserService userService,
            LoggingService loggingService) {
        this.transactionService = transactionService;
        this.userService = userService;
        this.loggingService = loggingService;
    }

    @PostMapping("/transaction")
    public ResponseEntity<Transaction> createTransaction(@RequestBody @Valid CreateTransactionRequestDTO request,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {

        String userEmail = principal.getUsername();
        Optional<User> user = userService.findByEmail(userEmail);

        if (user.isEmpty()) {
            loggingService.error("TransactionController: User not found: " + userEmail);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        loggingService.info("TransactionController: Creating transaction for user: " + user.get().getId()
                + "To receiver: " + request.getReceiverId());
        if (request.getReceiverId() == user.get().getId()) {
            loggingService.error("TransactionController: User not allowed to create transaction: " + userEmail);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Transaction transaction = transactionService.createTransaction(user.get(), request);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/transactions/{transactionId}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable @NonNull Integer transactionId,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {

        String userEmail = principal.getUsername();
        Optional<User> user = userService.findByEmail(userEmail);

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<Transaction> transaction = transactionService.getTransaction(transactionId);
        if (transaction.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Transaction t = transaction.get();
        if (!user.get().getId().equals(t.getSender().getId()) &&
                !user.get().getId().equals(t.getReceiver().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(t);
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getTransactions(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        String userEmail = principal.getUsername();
        Optional<User> user = userService.findByEmail(userEmail);
        return ResponseEntity.ok(transactionService.getUserTransactions(user.get().getId()));
    }
}
