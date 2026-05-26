package com.example.banking;

import com.example.banking.controller.dto.CreateAccountRequest;
import com.example.banking.domain.Account;
import com.example.banking.repository.AccountRepository;
import com.example.banking.service.AccountService;
import com.example.banking.service.AccountService.AccountNotFoundException;
import com.example.banking.service.AccountService.DuplicateAccountException;
import com.example.banking.service.AccountService.InvalidAccountRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void createAccountOk() {
        CreateAccountRequest request = new CreateAccountRequest(
                "customer-demo-001",
                "ACC000001",
                "SAVINGS",
                new BigDecimal("100.00"),
                "USD"
        );
        when(accountRepository.existsByAccountNumber("ACC000001")).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Account created = accountService.createAccount(request);

        assertThat(created.id()).isNotNull();
        assertThat(created.status()).isEqualTo("ACTIVE");
        assertThat(created.currency()).isEqualTo("USD");
        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(captor.capture());
        assertThat(captor.getValue().accountNumber()).isEqualTo("ACC000001");
    }

    @Test
    void rejectNegativeBalance() {
        CreateAccountRequest request = new CreateAccountRequest(
                "customer-demo-001",
                "ACC000002",
                "SAVINGS",
                new BigDecimal("-1.00"),
                "USD"
        );

        assertThatThrownBy(() -> accountService.createAccount(request))
                .isInstanceOf(InvalidAccountRequestException.class);
    }

    @Test
    void rejectDuplicateAccountNumber() {
        CreateAccountRequest request = new CreateAccountRequest(
                "customer-demo-001",
                "ACC000003",
                "CHECKING",
                BigDecimal.ZERO,
                "USD"
        );
        when(accountRepository.existsByAccountNumber("ACC000003")).thenReturn(true);

        assertThatThrownBy(() -> accountService.createAccount(request))
                .isInstanceOf(DuplicateAccountException.class);
    }

    @Test
    void getAccountNotFound() {
        UUID id = UUID.randomUUID();
        when(accountRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.getAccount(id))
                .isInstanceOf(AccountNotFoundException.class);
    }

    static Account sampleAccount(UUID id) {
        Instant now = Instant.parse("2026-01-01T00:00:00Z");
        return new Account(
                id,
                "customer-demo-001",
                "ACC000001",
                "SAVINGS",
                "ACTIVE",
                new BigDecimal("100.00"),
                "USD",
                now,
                now
        );
    }
}

