package com.example.bankingpaymentservice.repository;

import com.example.bankingpaymentservice.model.Transaction;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionRepository {

    private final List<Transaction> transactions = new ArrayList<>();

    public List<Transaction> findAll() {
        return List.copyOf(transactions);
    }

    public void saveAll(List<Transaction> newTransactions) {
        transactions.addAll(newTransactions);
    }
}
