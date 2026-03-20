package com.example.bankingpaymentservice.service;

import com.example.bankingpaymentservice.dto.TransactionRequest;
import com.example.bankingpaymentservice.dto.TransactionResponse;
import com.example.bankingpaymentservice.exception.InsufficientFundsException;
import com.example.bankingpaymentservice.exception.InvalidTransactionException;
import com.example.bankingpaymentservice.exception.TransactionNotFoundException;
import com.example.bankingpaymentservice.model.Account;
import com.example.bankingpaymentservice.model.AccountStatus;
import com.example.bankingpaymentservice.model.Transaction;
import com.example.bankingpaymentservice.model.TransactionStatus;
import com.example.bankingpaymentservice.model.TransactionType;
import com.example.bankingpaymentservice.repository.AccountRepository;
import com.example.bankingpaymentservice.repository.TransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);
    private static final String DEFAULT_CURRENCY = "USD";

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request) {
        validateBusinessRules(request);

        String accountNumber = request.getAccountNumber().trim();
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new InvalidTransactionException(
                        "Account not found for account number: " + accountNumber
                ));

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new InvalidTransactionException("Transactions are allowed only for ACTIVE accounts");
        }

        BigDecimal amount = request.getAmount();
        if (request.getType() == TransactionType.DEBIT) {
            if (account.getBalance().compareTo(amount) < 0) {
                throw new InsufficientFundsException("Insufficient funds for account " + accountNumber);
            }
            account.debit(amount);
        } else {
            account.credit(amount);
        }

        Transaction transaction = new Transaction(
                account,
                amount,
                DEFAULT_CURRENCY,
                request.getType(),
                TransactionStatus.SUCCESS,
                LocalDateTime.now()
        );

        Transaction savedTransaction = transactionRepository.save(transaction);
        return toResponse(savedTransaction);
    }

    public List<TransactionResponse> getAllTransactions() {
        return transactionRepository.findAllWithAccount().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public TransactionResponse getTransactionById(Long id) {
        return transactionRepository.findByIdWithAccount(id)
                .map(this::toResponse)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found for id: " + id));
    }

    public List<TransactionResponse> getTransactionsByAccount(String accountNumber) {
        List<TransactionResponse> accountTransactions = transactionRepository
                .findByAccountNumberWithAccount(accountNumber)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        if (accountTransactions.isEmpty()) {
            throw new TransactionNotFoundException(
                    "No transactions found for account number: " + accountNumber
            );
        }

        return accountTransactions;
    }

    public void demonstrateNPlusOneProblem() {
        log.info("N+1 demo: loading transactions without JOIN FETCH");
        List<Transaction> transactions = transactionRepository.findAll();
        transactions.forEach(transaction -> log.info(
                "Transaction {} belongs to account {}",
                transaction.getId(),
                transaction.getAccount().getAccountNumber()
        ));
    }

    public void demonstrateJoinFetchFix() {
        log.info("JOIN FETCH demo: loading transactions with account in one query");
        List<Transaction> transactions = transactionRepository.findAllWithAccount();
        transactions.forEach(transaction -> log.info(
                "Transaction {} belongs to account {}",
                transaction.getId(),
                transaction.getAccount().getAccountNumber()
        ));
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

    private TransactionResponse toResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .accountNumber(transaction.getAccountNumber())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .type(transaction.getType())
                .status(transaction.getStatus())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}
