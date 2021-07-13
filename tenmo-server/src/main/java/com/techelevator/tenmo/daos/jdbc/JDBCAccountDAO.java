package com.techelevator.tenmo.daos.jdbc;

import com.techelevator.tenmo.daos.AccountDAO;
import com.techelevator.tenmo.models.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;

@Component
public class JDBCAccountDAO implements AccountDAO {

    private JdbcTemplate jdbcTemplate;

    public JDBCAccountDAO(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Account showBalance(String username) {
        String sql = "SELECT account_id, accounts.user_id, balance " +
                "FROM accounts " +
                "JOIN users on users.user_id = accounts.user_id " +
                "WHERE username = ?";

        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, username);

        Account account = new Account();
        if (rows.next()) {
            account = mapRowToAccount(rows);
        }
        return account;
    }

    @Override
    public Account findAccountByUsername(String username) {
        String sql = "select account_id, accounts.user_id, balance " +
                "from accounts " +
                "join users on users.user_id = accounts.user_id " +
                "where username = ?";
        Account account = new Account();
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql,username);
        if (rows.next()){
            account = mapRowToAccount(rows);
        }
        return account;
    }

    private Account mapRowToAccount(SqlRowSet rows) {
        Account account = new Account();
        account.setAccountId(rows.getLong("account_id"));
        account.setUserId(rows.getLong("user_id"));
        account.setBalance(rows.getBigDecimal("balance"));

        return account;

    }
}
