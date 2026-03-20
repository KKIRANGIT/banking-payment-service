package com.example.bankingpaymentservice;

import com.example.bankingpaymentservice.model.Money;
import com.example.bankingpaymentservice.model.Transaction;
import com.example.bankingpaymentservice.model.TransactionStatus;
import com.example.bankingpaymentservice.model.TransactionType;
import com.example.bankingpaymentservice.service.TransactionAnalyzer;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BankingPaymentServiceApplication {

    public static void main(String[] args) {
        List<Transaction> transactions = createDummyTransactions();

        System.out.println("Top five transactions by amount:");
        TransactionAnalyzer.findTopFiveByAmount(transactions).forEach(System.out::println);

        System.out.println("\nTransactions grouped by status:");
        System.out.println(TransactionAnalyzer.groupByStatus(transactions));

        System.out.println("\nTotal amount by transaction type:");
        System.out.println(TransactionAnalyzer.getTotalAmountByType(transactions));

        System.out.println("\nDuplicate account numbers:");
        System.out.println(TransactionAnalyzer.findDuplicateAccountNumbers(transactions));

        SpringApplication.run(BankingPaymentServiceApplication.class, args);
    }

    private static List<Transaction> createDummyTransactions() {
        return List.of(
                buildTransaction("ACC1001", "1250.75", TransactionType.CREDIT, TransactionStatus.SUCCESS, 10),
                buildTransaction("ACC1002", "320.00", TransactionType.DEBIT, TransactionStatus.PENDING, 9),
                buildTransaction("ACC1003", "875.20", TransactionType.CREDIT, TransactionStatus.SUCCESS, 8),
                buildTransaction("ACC1004", "1450.00", TransactionType.DEBIT, TransactionStatus.FAILED, 7),
                buildTransaction("ACC1001", "2200.00", TransactionType.CREDIT, TransactionStatus.SUCCESS, 6),
                buildTransaction("ACC1005", "99.99", TransactionType.DEBIT, TransactionStatus.PENDING, 5),
                buildTransaction("ACC1006", "1800.10", TransactionType.CREDIT, TransactionStatus.SUCCESS, 4),
                buildTransaction("ACC1002", "430.45", TransactionType.DEBIT, TransactionStatus.FAILED, 3),
                buildTransaction("ACC1007", "760.00", TransactionType.CREDIT, TransactionStatus.SUCCESS, 2),
                buildTransaction("ACC1008", "50.00", TransactionType.DEBIT, TransactionStatus.PENDING, 1)
        );
    }

    private static Transaction buildTransaction(
            String accountNumber,
            String amount,
            TransactionType type,
            TransactionStatus status,
            int hoursAgo
    ) {
        return new Transaction(
                UUID.randomUUID().toString(),
                accountNumber,
                new Money(new BigDecimal(amount), "USD"),
                type,
                status,
                LocalDateTime.now().minusHours(hoursAgo)
        );
    }
}
