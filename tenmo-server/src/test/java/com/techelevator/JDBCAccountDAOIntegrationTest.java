package com.techelevator;

import com.techelevator.tenmo.auth.dao.JdbcUserDAO;
import com.techelevator.tenmo.auth.dao.UserDAO;
import com.techelevator.tenmo.auth.model.User;
import com.techelevator.tenmo.daos.AccountDAO;
import com.techelevator.tenmo.daos.jdbc.JDBCAccountDAO;
import com.techelevator.tenmo.exceptions.AccountNotFoundException;
import com.techelevator.tenmo.models.Account;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.math.BigDecimal;

public class JDBCAccountDAOIntegrationTest extends DAOIntegrationTest {

    private AccountDAO accountDAO;
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup() {
        accountDAO = new JDBCAccountDAO(getDataSource());
        jdbcTemplate = new JdbcTemplate(getDataSource());

    }

    @Test
    public void get_account_by_user_id() throws AccountNotFoundException {

        User user = getUser("test", "test");
        Account account = accountDAO.showBalance("user");
        insertNewTestUser(user);
        Account account1 = accountDAO.showBalance(user.getUsername());
        Assert.assertNotNull("Account was null", account);
        Assert.assertNotEquals(account,account1);

    }

//    public void retrieve_contact_by_contact_id() {
//        // Arrange - create a contact and store it, then insert it into the database
//        Contact contact = getContact("testFirstName", "testLastName");
//
//        contactDao.create( contact );
//
//        // Act
//        Contact contactFromDatabase = contactDao.getById( contact.getContactId() );
//
//        // Assert
//        Assert.assertNotNull("Contact was null",contactFromDatabase);
//        Assert.assertEquals("Contacts not equal", contact, contactFromDatabase);
//
//    }
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

}
