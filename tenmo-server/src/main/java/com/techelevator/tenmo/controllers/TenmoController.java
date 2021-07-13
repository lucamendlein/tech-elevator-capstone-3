package com.techelevator.tenmo.controllers;

import com.techelevator.tenmo.auth.dao.UserDAO;
import com.techelevator.tenmo.auth.model.User;
import com.techelevator.tenmo.daos.AccountDAO;
import com.techelevator.tenmo.daos.TransferDAO;
import com.techelevator.tenmo.exceptions.AccountNotFoundException;
import com.techelevator.tenmo.exceptions.InsufficientFundsException;
import com.techelevator.tenmo.exceptions.TransferNotFoundException;
import com.techelevator.tenmo.models.Account;
import com.techelevator.tenmo.models.Transfer;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
public class TenmoController {
    private String API_BASE_URL = "http://localhost:8080/";

    private AccountDAO accountDAO;
    private UserDAO userDAO;
    private TransferDAO transferDAO;

    public TenmoController(AccountDAO accountDAO, UserDAO userDAO, TransferDAO transferDAO){
    this.accountDAO = accountDAO;
    this.userDAO = userDAO;
    this.transferDAO = transferDAO;
    }

    @RequestMapping(path = "users", method = RequestMethod.GET)
    public List<User> list(Principal principal){
        return userDAO.findAll(principal.getName());
    }

    @RequestMapping(path = "users/account", method = RequestMethod.GET )
    public Account showBalance(Principal principal) throws AccountNotFoundException {
        return accountDAO.showBalance(principal.getName());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "users/transfers/{id}", method = RequestMethod.POST)
    public Transfer add(@RequestBody Transfer transfer,@PathVariable(name = "id") long userId, Principal principal) throws TransferNotFoundException, AccountNotFoundException, InsufficientFundsException {
        this.transferDAO.sendTransfer(transfer, principal.getName());
        this.transferDAO.sendTransferSubtractFromSendingAccount(transfer,accountDAO.findAccountByUsername(principal.getName()));
        this.transferDAO.sendTransferAddToReceivingAccount(transfer,accountDAO.showBalance(principal.getName()));
        return transfer;
    }

    @RequestMapping(path = "users/transfers", method = RequestMethod.GET)
    public List<Transfer> userTransfers(Principal principal) throws TransferNotFoundException {
        return this.transferDAO.userTransfers(principal.getName());
    }
    @RequestMapping(path = "users/transfers/{id}", method = RequestMethod.GET)
    public Transfer specificTransfer(@PathVariable(name = "id") long transferId) throws TransferNotFoundException {
        return transferDAO.getTransferById(transferId);

    }

    @RequestMapping(path = "transfers/{id}", method = RequestMethod.GET)
    public String getAccountToUsername(@PathVariable(name = "id") long transferId) throws TransferNotFoundException {
        return transferDAO.getUsernameOfAccountToByTransferId(transferId);
    }

//    @RequestMapping(path = "transfers/{id}", method = RequestMethod.POST)
//    public Transfer updateBalanceForUser(@RequestBody Transfer transfer, @PathVariable(name = "id") long userId){
//        this.transferDAO.sendTransferSubtractFromSendingAccount(transfer,accountDAO.showBalance(userId));
//        return transfer;
//    }


}
