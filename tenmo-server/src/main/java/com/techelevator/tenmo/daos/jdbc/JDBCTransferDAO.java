package com.techelevator.tenmo.daos.jdbc;

import com.techelevator.tenmo.daos.TransferDAO;
import com.techelevator.tenmo.exceptions.InsufficientFundsException;
import com.techelevator.tenmo.models.Account;
import com.techelevator.tenmo.models.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JDBCTransferDAO implements TransferDAO {

    private JdbcTemplate jdbcTemplate;

    public JDBCTransferDAO(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Override
    public void sendTransfer(Transfer transfer, String username) throws InsufficientFundsException {
//        username = "user";

        String sql1 = "INSERT INTO transfers (transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES (DEFAULT, (select transfer_type_id from transfer_types where transfer_type_id = ?), (select transfer_status_id from transfer_statuses where transfer_status_id = ?) " +
                ", (select account_id from accounts join users on users.user_id = accounts.user_id where users.username = ?), (select account_id from accounts where user_id = ?), ?) returning transfer_id";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql1, transfer.getTransferTypeId(), transfer.getTransferStatusId(), username, transfer.getAccountTo(), transfer.getAmount());
        rows.next();
        transfer.setTransferId(rows.getLong("transfer_id"));

    }

    @Override
    public void sendTransferSubtractFromSendingAccount(Transfer transfer, Account account) {
        String sql = "update accounts set balance = balance -? where user_id = ? returning balance";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, transfer.getAmount(),transfer.getAccountFrom());
        rows.next();
        account.setBalance(rows.getBigDecimal("balance"));

    }

    @Override
    public void sendTransferAddToReceivingAccount(Transfer transfer, Account account) {
        String sql = "update accounts set balance = balance + ? where user_id = ? returning balance";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, transfer.getAmount(), transfer.getAccountTo());
        rows.next();
        account.setBalance(rows.getBigDecimal("balance"));
    }

    @Override
    public List<Transfer> userTransfers(String username) {
        Transfer transfer = new Transfer();
        String sql = "select transfer_id, account_from, account_to, transfer_type_id, transfer_status_id, amount, username, (select username from users join accounts on accounts.user_id = users.user_id where account_id = ?) " +
                "from transfers " +
                "join accounts on accounts.account_id = transfers.account_from " +
                "join users on users.user_id = accounts.user_id " +
                "where username = ?";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql,transfer.getAccountTo(),username);
        List<Transfer> userTransfers = new ArrayList<Transfer>();
        while (rows.next()){
            transfer = mapRowToTransfer(rows);
            userTransfers.add(transfer);
        }

        return userTransfers;
    }

    @Override
    public Transfer getTransferById(long transferId) {
        String sql = "select transfer_id, account_from, account_to, transfer_type_id, transfer_status_id, amount " +
                "from transfers " +
                "where transfer_id = ?";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql,transferId);
        rows.next();
        Transfer transfer = mapRowToTransfer(rows);
        return transfer;
    }

    @Override
    public String getUsernameOfAccountToByTransferId(long transferId) {
        String sql = "select username " +
                "from transfers " +
                "join accounts ON accounts.account_id = transfers.account_to " +
                "join users ON users.user_id = accounts.user_id " +
                "where transfer_id = ?";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, transferId);
        rows.next();
        Transfer transfer = new Transfer();
        transfer.setAccountToUsername(rows.getString("username"));
        String username = transfer.getAccountToUsername();
        return username;
    }

    @Override
    public void requestTransfer(Transfer transfer, String username) {
        String sql = "INSERT INTO transfers (transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES (DEFAULT, (select transfer_type_id from transfer_types where transfer_type_id = ?), (select transfer_status_id from transfer_statuses where transfer_status_id = ?) " +
                ", (select account_id from accounts join users on users.user_id = accounts.user_id where users.username = ? and balance > ?), (select account_id from accounts where user_id = ?), ?) returning transfer_id";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, transfer.getTransferTypeId(), transfer.getTransferStatusId(), username, transfer.getAmount(), transfer.getAccountTo(), transfer.getAmount());
        rows.next();
        transfer.setTransferId(rows.getLong("transfer_id"));

    }


    private Transfer mapRowToTransfer(SqlRowSet row){
        Transfer transfer = new Transfer();
        transfer.setTransferId(row.getLong("transfer_id"));
        transfer.setTransferTypeId(row.getLong("transfer_type_id"));
        transfer.setTransferStatusId(row.getLong("transfer_status_id"));
        transfer.setAccountFrom(row.getLong("account_from"));
        transfer.setAccountTo(row.getLong("account_to"));
        transfer.setAmount(row.getBigDecimal("amount"));

        return transfer;
    }

}
