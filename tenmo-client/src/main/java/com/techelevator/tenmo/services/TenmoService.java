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
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.sql.SQLException;
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

    public List<User> findAllUsers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity entity = new HttpEntity(headers);
        User[] users = restTemplate.exchange(apiUrl + "users", HttpMethod.GET,
                entity, User[].class).getBody();
        return Arrays.asList(users);
    }

    public Account getBalance() throws AccountNotFoundException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity entity = new HttpEntity(headers);

        Account account = restTemplate.exchange(apiUrl + "users" + "/account",
                HttpMethod.GET, entity, Account.class).getBody();
        return account;
    }
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
    public List<Transfer> userTransfers() throws TransferNotFoundException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity entity = new HttpEntity(headers);
        Transfer[] transfers = restTemplate.exchange(apiUrl + "users/transfers", HttpMethod.GET, entity, Transfer[].class).getBody();

        return Arrays.asList(transfers);

    }
    public Transfer transferById(long transferId) throws TransferNotFoundException{
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity entity = new HttpEntity(headers);
        Transfer transfer = restTemplate.exchange(apiUrl + "users/transfers/" + transferId,
                HttpMethod.GET, entity, Transfer.class).getBody();
        return transfer;
    }

    public String accountToUsernameById(long transferId) throws AccountNotFoundException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity entity = new HttpEntity(headers);
        String username = restTemplate.exchange(apiUrl + "transfers/" + transferId, HttpMethod.GET, entity, String.class).getBody();
        return username;
    }




//    private HttpEntity<Reservation> makeReservationEntity(Reservation reservation) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setBearerAuth(AUTH_TOKEN);
//        HttpEntity<Reservation> entity = new HttpEntity<>(reservation, headers);
//        return entity;
//    }
//    public Reservation updateReservation(String CSV) throws HotelServiceException {
//    Reservation reservation = makeReservation(CSV);
//    if (reservation == null) {
//      throw new HotelServiceException(INVALID_RESERVATION_MSG);
//    }
//    try {
//      restTemplate.exchange(BASE_URL + "reservations/" + reservation.getId(), HttpMethod.PUT,
//          makeReservationEntity(reservation), Reservation.class);
//    } catch (RestClientResponseException ex) {
//      throw new HotelServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
//    }
//    return reservation;
//  }

}
