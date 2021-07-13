package com.techelevator.tenmo.daos;

import com.techelevator.tenmo.exceptions.AccountNotFoundException;
import com.techelevator.tenmo.exceptions.InsufficientFundsException;
import com.techelevator.tenmo.exceptions.TransferNotFoundException;
import com.techelevator.tenmo.models.Account;
import com.techelevator.tenmo.models.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDAO {

    void sendTransfer(Transfer transfer, String username) throws TransferNotFoundException, InsufficientFundsException;

    void requestTransfer(Transfer transfer, String username) throws TransferNotFoundException;

    void sendTransferSubtractFromSendingAccount(Transfer transfer, Account account) throws TransferNotFoundException, AccountNotFoundException;

    void sendTransferAddToReceivingAccount(Transfer transfer, Account account) throws TransferNotFoundException, AccountNotFoundException;

    List<Transfer> userTransfers( String Username) throws TransferNotFoundException;

    Transfer getTransferById(long transferId) throws TransferNotFoundException;

    String getUsernameOfAccountToByTransferId(long transferId) throws TransferNotFoundException;
}
