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

    /**
     * Allows authenticated user to view list of current users for potential transfer.
     * @param principal - authenticated user
     * @return list of users excluding current user
     *
     */
    @RequestMapping(path = "users", method = RequestMethod.GET)
    public List<User> list(Principal principal){
        return userDAO.findAll(principal.getName());
    }


    /**
     * Allows authenticated user to view current balance.
     * @param principal - authenticated user
     * @return current authenticated user's balance
     * @throws AccountNotFoundException
     */
    @RequestMapping(path = "users/account", method = RequestMethod.GET )
    public Account showBalance(Principal principal) throws AccountNotFoundException {
        return accountDAO.showBalance(principal.getName());
    }

    /**
     * Allows authenticated user to transfer TEBucks to user of choice.
     * @param transfer - information required to complete transfer
     * @param userId - id of user receiving transfer
     * @param principal - authenticated user
     * @return full completed transfer
     * @throws TransferNotFoundException
     * @throws AccountNotFoundException
     * @throws InsufficientFundsException
     */
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "users/transfers/{id}", method = RequestMethod.POST)
    public Transfer add(@RequestBody Transfer transfer,@PathVariable(name = "id") long userId, Principal principal) throws TransferNotFoundException, AccountNotFoundException, InsufficientFundsException {
        if (accountDAO.showBalance(principal.getName()).getBalance().doubleValue() < transfer.getAmount().doubleValue()){
            throw new  InsufficientFundsException();
        }
        this.transferDAO.sendTransfer(transfer, principal.getName());
        this.transferDAO.sendTransferSubtractFromSendingAccount(transfer,accountDAO.findAccountByUsername(principal.getName()));
        this.transferDAO.sendTransferAddToReceivingAccount(transfer,accountDAO.showBalance(principal.getName()));
        return transfer;
    }

    /**
     * Allows authenticated user to view a list of past transfers.
     * @param principal - authenticated user
     * @return list of transfers sent and received
     * @throws TransferNotFoundException
     */
    @RequestMapping(path = "users/transfers", method = RequestMethod.GET)
    public List<Transfer> userTransfers(Principal principal) throws TransferNotFoundException {
        return this.transferDAO.userTransfers(principal.getName());
    }

    /**
     * Allows authenticated user to view specific details from chosen past transfer.
     * @param transferId - id of completed transfer
     * @return transfer details
     * @throws TransferNotFoundException
     */
    @RequestMapping(path = "users/transfers/{id}", method = RequestMethod.GET)
    public Transfer specificTransfer(@PathVariable(name = "id") long transferId) throws TransferNotFoundException {
        return transferDAO.getTransferById(transferId);

    }

    /**
     * Allows authenticated user to view username associated with past transfer.
     * @param transferId - id of completed transfer
     * @return - username of account associated with transfer
     * @throws TransferNotFoundException
     */
    @RequestMapping(path = "transfers/{id}", method = RequestMethod.GET)
    public String getAccountToUsername(@PathVariable(name = "id") long transferId) throws TransferNotFoundException {
        return transferDAO.getUsernameOfAccountToByTransferId(transferId);
    }



}
