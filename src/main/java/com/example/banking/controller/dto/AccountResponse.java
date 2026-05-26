package com.example.banking.controller.dto;

import com.example.banking.domain.Account;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AccountResponse(
        UUID id,
        String customerId,
        String maskedAccountNumber,
        String type,
        String status,
        BigDecimal balance,
        String currency,
        Instant createdAt,
        Instant updatedAt
) {
    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.id(),
                account.customerId(),
                mask(account.accountNumber()),
                account.type(),
                account.status(),
                account.balance(),
                account.currency(),
                account.createdAt(),
                account.updatedAt()
        );
    }

    private static String mask(String value) {
        if (value == null || value.length() <= 4) {
            return "****";
        }
        return "****" + value.substring(value.length() - 4);
    }
}

