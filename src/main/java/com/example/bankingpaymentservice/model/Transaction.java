package com.example.bankingpaymentservice.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Transaction {

    private String id;
    private String accountNumber;
    private Money amount;
    private TransactionType type;
    private TransactionStatus status;
    private LocalDateTime createdAt;

    public Transaction(
            String id,
            String accountNumber,
            Money amount,
            TransactionType type,
            TransactionStatus status,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.type = type;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public Money getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Transaction that)) {
            return false;
        }
        return Objects.equals(id, that.id)
                && Objects.equals(accountNumber, that.accountNumber)
                && Objects.equals(amount, that.amount)
                && type == that.type
                && status == that.status
                && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, accountNumber, amount, type, status, createdAt);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", amount=" + amount +
                ", type=" + type +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}
