package com.example.banking.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record Account(
        UUID id,
        String customerId,
        String accountNumber,
        String type,
        String status,
        BigDecimal balance,
        String currency,
        Instant createdAt,
        Instant updatedAt
) {
}

