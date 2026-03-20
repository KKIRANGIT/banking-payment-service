package com.example.bankingpaymentservice.service;

import com.example.bankingpaymentservice.dto.TransactionRequest;
import com.example.bankingpaymentservice.dto.TransactionResponse;
import com.example.bankingpaymentservice.exception.InsufficientFundsException;
import com.example.bankingpaymentservice.exception.InvalidTransactionException;
import com.example.bankingpaymentservice.exception.TransactionNotFoundException;
import com.example.bankingpaymentservice.model.Money;
import com.example.bankingpaymentservice.model.Transaction;
import com.example.bankingpaymentservice.model.TransactionStatus;
import com.example.bankingpaymentservice.model.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private static final String DEFAULT_CURRENCY = "USD";

    private final List<Transaction> transactions = new ArrayList<>();

    public synchronized TransactionResponse createTransaction(TransactionRequest request) {
        validateBusinessRules(request);

        if (request.getType() == TransactionType.DEBIT) {
            BigDecimal availableBalance = calculateBalance(request.getAccountNumber());
            if (availableBalance.compareTo(request.getAmount()) < 0) {
                throw new InsufficientFundsException(
                        "Insufficient funds for account " + request.getAccountNumber()
                );
            }
        }

        Transaction transaction = new Transaction(
                UUID.randomUUID().toString(),
                request.getAccountNumber().trim(),
                new Money(request.getAmount(), DEFAULT_CURRENCY),
                request.getType(),
                TransactionStatus.SUCCESS,
                LocalDateTime.now()
        );

        transactions.add(transaction);
        return toResponse(transaction);
    }

    public synchronized List<TransactionResponse> getAllTransactions() {
        return transactions.stream()
                .sorted(Comparator.comparing(Transaction::getCreatedAt).reversed())
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public synchronized TransactionResponse getTransactionById(String id) {
        return transactions.stream()
                .filter(transaction -> transaction.getId().equals(id))
                .findFirst()
                .map(this::toResponse)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found for id: " + id));
    }

    public synchronized List<TransactionResponse> getTransactionsByAccount(String accountNumber) {
        List<TransactionResponse> accountTransactions = transactions.stream()
                .filter(transaction -> transaction.getAccountNumber().equalsIgnoreCase(accountNumber))
                .sorted(Comparator.comparing(Transaction::getCreatedAt).reversed())
                .map(this::toResponse)
                .collect(Collectors.toList());

        if (accountTransactions.isEmpty()) {
            throw new TransactionNotFoundException(
                    "No transactions found for account number: " + accountNumber
            );
        }

        return accountTransactions;
    }

    private void validateBusinessRules(TransactionRequest request) {
        String normalizedAccountNumber = request.getAccountNumber() == null ? "" : request.getAccountNumber().trim();
        if (normalizedAccountNumber.length() < 4) {
            throw new InvalidTransactionException("Account number must contain at least 4 characters");
        }
        if (request.getType() != TransactionType.DEBIT && request.getType() != TransactionType.CREDIT) {
            throw new InvalidTransactionException("Transaction type must be DEBIT or CREDIT");
        }
    }

    private BigDecimal calculateBalance(String accountNumber) {
        return transactions.stream()
                .filter(transaction -> transaction.getAccountNumber().equalsIgnoreCase(accountNumber))
                .map(transaction -> transaction.getType() == TransactionType.CREDIT
                        ? transaction.getAmount().getAmount()
                        : transaction.getAmount().getAmount().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private TransactionResponse toResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .accountNumber(transaction.getAccountNumber())
                .amount(transaction.getAmount().getAmount())
                .currency(transaction.getAmount().getCurrency())
                .type(transaction.getType())
                .status(transaction.getStatus())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}
