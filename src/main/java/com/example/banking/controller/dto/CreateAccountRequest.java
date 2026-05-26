package com.example.banking.controller.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateAccountRequest(
        @NotBlank
        @Size(max = 80)
        String customerId,

        @NotBlank
        @Size(min = 6, max = 34)
        String accountNumber,

        @NotBlank
        @Size(max = 30)
        String type,

        @DecimalMin(value = "0.00")
        BigDecimal initialBalance,

        @NotBlank
        @Pattern(regexp = "^[A-Z]{3}$")
        String currency
) {
}

