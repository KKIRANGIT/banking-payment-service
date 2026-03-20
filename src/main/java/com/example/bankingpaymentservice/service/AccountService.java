package com.example.bankingpaymentservice.service;

import com.example.bankingpaymentservice.dto.AccountResponse;
import com.example.bankingpaymentservice.exception.InvalidTransactionException;
import com.example.bankingpaymentservice.model.Account;
import com.example.bankingpaymentservice.repository.AccountRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Cacheable(cacheNames = "accounts", key = "#accountNumber")
    public AccountResponse getAccountByNumber(String accountNumber) {
        return mapToResponse(findAccountEntityByNumber(accountNumber));
    }

    @Transactional
    @CachePut(cacheNames = "accounts", key = "#result.accountNumber")
    public AccountResponse createAccount(Account account) {
        return mapToResponse(accountRepository.save(account));
    }

    @Transactional
    @CacheEvict(cacheNames = "accounts", key = "#account.accountNumber")
    public void updateAccount(Account account) {
        accountRepository.save(account);
    }

    public Account findAccountEntityByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new InvalidTransactionException(
                        "Account not found for account number: " + accountNumber
                ));
    }

    private AccountResponse mapToResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .accountHolderName(account.getAccountHolderName())
                .balance(account.getBalance())
                .status(account.getStatus())
                .version(account.getVersion())
                .build();
    }
}
