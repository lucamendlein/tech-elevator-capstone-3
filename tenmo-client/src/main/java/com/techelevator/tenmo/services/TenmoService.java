package com.techelevator.tenmo.services;

import com.techelevator.tenmo.auth.models.AuthenticatedUser;
import com.techelevator.tenmo.auth.models.User;
import com.techelevator.tenmo.exceptions.AccountNotFoundException;
import com.techelevator.tenmo.exceptions.InsufficientFundsException;
import com.techelevator.tenmo.exceptions.TransferNotFoundException;
import com.techelevator.tenmo.models.Account;
import com.techelevator.tenmo.models.Transfer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class TenmoService {
    private String apiUrl;
    private RestTemplate restTemplate = new RestTemplate();
    private AuthenticatedUser currentUser;

    public TenmoService(String apiUrl, AuthenticatedUser currentUser){
        this.apiUrl = apiUrl;
        this.currentUser = currentUser;
    }

    /**
     * Allows authenticated user to view list of all other users.
     * @return list of all users with exception of current authenticated user.
     */
    public List<User> findAllUsers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity entity = new HttpEntity(headers);
        User[] users = restTemplate.exchange(apiUrl + "users", HttpMethod.GET,
                entity, User[].class).getBody();
        return Arrays.asList(users);
    }

    /**
     * Allows authenticated user to view current balance.
     * @return current balance of authenticated user.
     * @throws AccountNotFoundException
     */
    public Account getBalance() throws AccountNotFoundException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity entity = new HttpEntity(headers);

        Account account = restTemplate.exchange(apiUrl + "users" + "/account",
                HttpMethod.GET, entity, Account.class).getBody();
        return account;
    }

    /**
     * Allows authenticated user to transfer TEBucks to user of choice.
     * @param userId - id of user chosen for transfer
     * @param amount - amount of TEBucks authenticated user selected for transfer
     * @return completed transfer
     * @throws TransferNotFoundException
     * @throws InsufficientFundsException
     */
    public Transfer sendTransfer(long userId, BigDecimal amount) throws TransferNotFoundException, InsufficientFundsException {
        Transfer transfer = new Transfer();
        transfer.setAmount(amount);
        transfer.setAccountFrom(currentUser.getUser().getId());
        transfer.setTransferTypeId(2);
        transfer.setTransferStatusId(2);
        transfer.setAccountTo(userId);

        transfer = restTemplate.exchange(apiUrl + "users/transfers/" + userId, HttpMethod.POST, makeTransferEntity(transfer), Transfer.class).getBody();

        return transfer;
    }

    private HttpEntity<Transfer> makeTransferEntity(Transfer transfer){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity<Transfer> entity = new HttpEntity<Transfer>(transfer,headers);
        return entity;
    }

    /**
     * Allows authenticated user to view list of previous transfers.
     * @return list of authenticated user's transfers
     * @throws TransferNotFoundException
     */
    public List<Transfer> userTransfers() throws TransferNotFoundException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity entity = new HttpEntity(headers);
        Transfer[] transfers = restTemplate.exchange(apiUrl + "users/transfers", HttpMethod.GET, entity, Transfer[].class).getBody();

        return Arrays.asList(transfers);

    }

    /**
     * Allows authenticated user to view transfer details.
     * @param transferId - id of chosen transfer
     * @return completed transfer details
     * @throws TransferNotFoundException
     */
    public Transfer transferById(long transferId) throws TransferNotFoundException{
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity entity = new HttpEntity(headers);
        Transfer transfer = restTemplate.exchange(apiUrl + "users/transfers/" + transferId,
                HttpMethod.GET, entity, Transfer.class).getBody();
        return transfer;
    }

    /**
     * Allows authenticated user to view username associated with user from previous transfer.
     * @param transferId - id of chosen transfer
     * @return username of chosen user
     * @throws AccountNotFoundException
     */
    public String accountToUsernameById(long transferId) throws AccountNotFoundException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity entity = new HttpEntity(headers);
        String username = restTemplate.exchange(apiUrl + "transfers/" + transferId, HttpMethod.GET, entity, String.class).getBody();
        return username;
    }


}
