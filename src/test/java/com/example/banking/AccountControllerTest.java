package com.example.banking;

import com.example.banking.controller.AccountController;
import com.example.banking.domain.Account;
import com.example.banking.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.hamcrest.Matchers.endsWith;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
@Import(com.example.banking.config.SecurityConfig.class)
@TestPropertySource(properties = {
        "security.demo.username=demo-api-user",
        "security.demo.password=local-demo-only"
})
class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountService accountService;

    @Test
    @WithMockUser
    void getAccountOk() throws Exception {
        UUID id = UUID.randomUUID();
        when(accountService.getAccount(id)).thenReturn(sampleAccount(id));

        mockMvc.perform(get("/api/accounts/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.maskedAccountNumber", endsWith("0001")));
    }

    @Test
    @WithMockUser
    void postValidationError() throws Exception {
        CreateAccountPayload payload = new CreateAccountPayload(
                "customer-demo-001",
                "123",
                "SAVINGS",
                new BigDecimal("-5.00"),
                "USD"
        );

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void apiRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/accounts/{id}", UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }

    private static Account sampleAccount(UUID id) {
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

    private record CreateAccountPayload(
            String customerId,
            String accountNumber,
            String type,
            BigDecimal initialBalance,
            String currency
    ) {
    }
}
