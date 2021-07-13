package com.techelevator;

import com.techelevator.tenmo.auth.model.User;
import com.techelevator.tenmo.daos.TransferDAO;
import com.techelevator.tenmo.daos.jdbc.JDBCTransferDAO;
import com.techelevator.tenmo.exceptions.AccountNotFoundException;
import com.techelevator.tenmo.exceptions.InsufficientFundsException;
import com.techelevator.tenmo.exceptions.TransferNotFoundException;
import com.techelevator.tenmo.models.Account;
import com.techelevator.tenmo.models.Transfer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.math.BigDecimal;

public class JDBCTransferDAOIntegrationTest extends DAOIntegrationTest {
    private TransferDAO transferDAO;
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup(){
        transferDAO = new JDBCTransferDAO(getDataSource());
        jdbcTemplate = new JdbcTemplate(getDataSource());
    }

    @Test
    public void creating_new_transfer() throws TransferNotFoundException {
        Transfer transfer = getTransfer(2,2,2002,1005, BigDecimal.valueOf(500));
        User user = getUser("test", "test");
        insertNewTestUser(user);
        transferDAO.requestTransfer(transfer, "user");

        Assert.assertTrue(transfer.getTransferId() > 0);

        String sql = "select transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, " +
                "amount from transfers where transfer_id = ?";

        Transfer transferFromDatabase = new Transfer();

        SqlRowSet row = jdbcTemplate.queryForRowSet(sql,transfer.getTransferId());
        if (row.next()) {
            transferFromDatabase.setTransferId(row.getLong("transfer_id"));
            transferFromDatabase.setTransferTypeId(row.getLong("transfer_type_id"));
            transferFromDatabase.setTransferStatusId(row.getLong("transfer_status_id"));
            transferFromDatabase.setAccountFrom(row.getLong("account_from"));
            transferFromDatabase.setAccountTo(row.getLong("account_to"));
            transferFromDatabase.setAmount(row.getBigDecimal("amount"));
        }
        Assert.assertEquals(transferFromDatabase.getTransferId(), transfer.getTransferId());



    }
    @Test
    public void send_transfer_should_change_balance_on_from_account() throws TransferNotFoundException, AccountNotFoundException, InsufficientFundsException {



        String sql = "select account_id, user_id, balance " +
                "from accounts " +
                "where account_id = ?";
        Account account = new Account();
        SqlRowSet row = jdbcTemplate.queryForRowSet(sql,1001);
        if (row.next()){
            account.setAccountId(row.getLong("account_id"));
            account.setUserId(row.getLong("user_id"));
            account.setBalance(row.getBigDecimal("balance"));
        }
        Transfer transfer = getTransfer(2,2,1001,1005, BigDecimal.valueOf(500));

        transferDAO.sendTransfer(transfer, "user");
        transferDAO.sendTransferSubtractFromSendingAccount(transfer, account);



        Assert.assertEquals((transfer.getAmount()).doubleValue(),account.getBalance().doubleValue(),.99);



    }
    @Test
    public void send_transfer_should_change_balance_of_to_account() throws TransferNotFoundException, AccountNotFoundException, InsufficientFundsException {

        String sql = "select account_id, user_id, balance " +
                "from accounts " +
                "where account_id = ?";
        Account account = new Account();
        SqlRowSet row = jdbcTemplate.queryForRowSet(sql,2002);
        if (row.next()){
            account.setAccountId(row.getLong("account_id"));
            account.setUserId(row.getLong("user_id"));
            account.setBalance(row.getBigDecimal("balance"));
        }
        Transfer transfer = getTransfer(2,2,2001,1005, BigDecimal.valueOf(500));

        transferDAO.sendTransfer(transfer, "user");
        transferDAO.sendTransferAddToReceivingAccount(transfer,account);

        Assert.assertEquals(BigDecimal.valueOf(1500.00).doubleValue(),account.getBalance().doubleValue(), .99);


    }



    private Transfer getTransfer(long transferTypeId, long transferStatusId,
                                 long accountFrom, long accountTo, BigDecimal amount){
        Transfer transfer = new Transfer();

        transfer.setTransferTypeId(transferTypeId);
        transfer.setTransferStatusId(transferStatusId);
        transfer.setAccountFrom(accountFrom);
        transfer.setAccountTo(accountTo);
        transfer.setAmount(amount);

        return transfer;
    }
    private void insertNewTestUser(User newUser) {
        String sql = "INSERT INTO users (user_id, username, password_hash) " +
                "VALUES (DEFAULT, ?, ?) " +
                "RETURNING user_id";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sql, newUser.getUsername(), newUser.getPassword());
        row.next();
        newUser.setId(row.getLong("user_id"));
    }
    private User getUser(String userName, String password){
        User user = new User();
        user.setUsername(userName);
        user.setPassword(password);
        return user;
    }



//    @Test
//    public void insert_contact() {
//        // Arrange
//        Contact newContact = getContact("testFirst", "testLast");
//
//        // Act
//        contactDao.create(newContact);
//
//        // Assert
//        Assert.assertTrue(newContact.getContactId() > 0);
//
//        // retrieve the contact from the database
//        Contact contactFromDatabase = contactDao.getById(newContact.getContactId());
//
//        Assert.assertEquals(newContact, contactFromDatabase);
//private Contact retrieveContactById(long contactId) {
//    Contact contact = null;
//
//    String sql = "SELECT contact_id, first_name, last_name, date_added FROM contact " +
//            "WHERE contact_id = ?";
//    SqlRowSet row = jdbcTemplate.queryForRowSet(sql, contactId);
//
//    if (row.next()) {
//        contact = new Contact();
//        contact.setContactId(row.getLong("contact_id"));
//        contact.setFirstName(row.getString("first_name"));
//        contact.setLastName(row.getString("last_name"));
//        contact.setDateAdded(row.getDate("date_added").toLocalDate());
//
//    }

//    return contact;



}
