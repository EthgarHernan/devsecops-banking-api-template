package com.example.banking.repository;

import com.example.banking.domain.Account;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class AccountRepository {
    private static final RowMapper<Account> ACCOUNT_ROW_MAPPER = new AccountRowMapper();

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public AccountRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Account> findById(UUID id) {
        String sql = """
                SELECT id, customer_id, account_number, type, status, balance, currency, created_at, updated_at
                FROM accounts
                WHERE id = :id
                """;
        List<Account> results = jdbcTemplate.query(sql, new MapSqlParameterSource("id", id), ACCOUNT_ROW_MAPPER);
        return results.stream().findFirst();
    }

    public List<Account> findByCustomerId(String customerId) {
        String sql = """
                SELECT id, customer_id, account_number, type, status, balance, currency, created_at, updated_at
                FROM accounts
                WHERE customer_id = :customerId
                ORDER BY created_at DESC
                """;
        return jdbcTemplate.query(sql, new MapSqlParameterSource("customerId", customerId), ACCOUNT_ROW_MAPPER);
    }

    public Account save(Account account) {
        String sql = """
                INSERT INTO accounts (
                    id, customer_id, account_number, type, status, balance, currency, created_at, updated_at
                ) VALUES (
                    :id, :customerId, :accountNumber, :type, :status, :balance, :currency, :createdAt, :updatedAt
                )
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", account.id())
                .addValue("customerId", account.customerId())
                .addValue("accountNumber", account.accountNumber())
                .addValue("type", account.type())
                .addValue("status", account.status())
                .addValue("balance", account.balance())
                .addValue("currency", account.currency())
                .addValue("createdAt", Timestamp.from(account.createdAt()))
                .addValue("updatedAt", Timestamp.from(account.updatedAt()));
        jdbcTemplate.update(sql, params);
        return account;
    }

    public boolean existsByAccountNumber(String accountNumber) {
        String sql = "SELECT COUNT(1) FROM accounts WHERE account_number = :accountNumber";
        Integer count = jdbcTemplate.queryForObject(
                sql,
                new MapSqlParameterSource("accountNumber", accountNumber),
                Integer.class
        );
        return count != null && count > 0;
    }

    private static class AccountRowMapper implements RowMapper<Account> {
        @Override
        public Account mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Account(
                    rs.getObject("id", UUID.class),
                    rs.getString("customer_id"),
                    rs.getString("account_number"),
                    rs.getString("type"),
                    rs.getString("status"),
                    rs.getBigDecimal("balance"),
                    rs.getString("currency"),
                    rs.getTimestamp("created_at").toInstant(),
                    rs.getTimestamp("updated_at").toInstant()
            );
        }
    }
}

