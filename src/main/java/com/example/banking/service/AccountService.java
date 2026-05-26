package com.example.banking.service;

import com.example.banking.controller.dto.CreateAccountRequest;
import com.example.banking.domain.Account;
import com.example.banking.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account getAccount(UUID id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found."));
    }

    public List<Account> listCustomerAccounts(String customerId) {
        if (customerId == null || customerId.isBlank()) {
            throw new InvalidAccountRequestException("customerId must not be blank.");
        }
        return accountRepository.findByCustomerId(customerId.trim());
    }

    public Account createAccount(CreateAccountRequest request) {
        validateRequest(request);
        String accountNumber = request.accountNumber().trim();
        if (accountRepository.existsByAccountNumber(accountNumber)) {
            throw new DuplicateAccountException("Account number already exists.");
        }
        Instant now = Instant.now();
        Account account = new Account(
                UUID.randomUUID(),
                request.customerId().trim(),
                accountNumber,
                request.type().trim().toUpperCase(Locale.ROOT),
                "ACTIVE",
                request.initialBalance(),
                request.currency().trim().toUpperCase(Locale.ROOT),
                now,
                now
        );
        return accountRepository.save(account);
    }

    private static void validateRequest(CreateAccountRequest request) {
        if (request.customerId() == null || request.customerId().isBlank()) {
            throw new InvalidAccountRequestException("customerId must not be blank.");
        }
        if (request.accountNumber() == null || request.accountNumber().isBlank()) {
            throw new InvalidAccountRequestException("accountNumber must not be blank.");
        }
        if (request.initialBalance() == null || request.initialBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidAccountRequestException("initialBalance must be non-negative.");
        }
        if (!"USD".equalsIgnoreCase(request.currency())) {
            throw new InvalidAccountRequestException("Only USD is enabled in this local template.");
        }
    }

    public static class AccountNotFoundException extends RuntimeException {
        public AccountNotFoundException(String message) {
            super(message);
        }
    }

    public static class DuplicateAccountException extends RuntimeException {
        public DuplicateAccountException(String message) {
            super(message);
        }
    }

    public static class InvalidAccountRequestException extends RuntimeException {
        public InvalidAccountRequestException(String message) {
            super(message);
        }
    }
}

