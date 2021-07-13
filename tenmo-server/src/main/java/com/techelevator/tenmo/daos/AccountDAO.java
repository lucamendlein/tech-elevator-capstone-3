package com.techelevator.tenmo.daos;

import com.techelevator.tenmo.exceptions.AccountNotFoundException;
import com.techelevator.tenmo.models.Account;

import java.math.BigDecimal;

public interface AccountDAO {

    Account showBalance(String username) throws AccountNotFoundException;

    Account findAccountByUsername(String username) throws AccountNotFoundException;
}
