package com.example.bankingpaymentservice.controller;

import com.example.bankingpaymentservice.dto.TransactionRequest;
import com.example.bankingpaymentservice.dto.TransactionResponse;
import com.example.bankingpaymentservice.service.TransactionService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /*
    Example:
    curl -X POST http://localhost:8081/api/transactions ^
      -H "Content-Type: application/json" ^
      -d "{\"accountNumber\":\"ACC1001\",\"amount\":500.00,\"type\":\"CREDIT\"}"
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse createTransaction(@Valid @RequestBody TransactionRequest request) {
        return transactionService.createTransaction(request);
    }

    /*
    Example:
    curl http://localhost:8081/api/transactions
     */
    @GetMapping
    public List<TransactionResponse> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    /*
    Example:
    curl http://localhost:8081/api/transactions/1
     */
    @GetMapping("/{id}")
    public TransactionResponse getTransactionById(@PathVariable Long id) {
        return transactionService.getTransactionById(id);
    }

    /*
    Example:
    curl http://localhost:8081/api/transactions/account/ACC1001
     */
    @GetMapping("/account/{accountNumber}")
    public List<TransactionResponse> getTransactionsByAccount(@PathVariable String accountNumber) {
        return transactionService.getTransactionsByAccount(accountNumber);
    }
}
