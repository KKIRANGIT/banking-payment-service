package com.example.bankingpaymentservice.dto;

import com.example.bankingpaymentservice.model.TransactionStatus;
import com.example.bankingpaymentservice.model.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TransactionResponse {

    private final String id;
    private final String accountNumber;
    private final BigDecimal amount;
    private final String currency;
    private final TransactionType type;
    private final TransactionStatus status;
    private final LocalDateTime createdAt;
}
