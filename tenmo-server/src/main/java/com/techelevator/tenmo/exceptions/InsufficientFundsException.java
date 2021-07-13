package com.techelevator.tenmo.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Insufficient funds.")
public class InsufficientFundsException extends Exception {

    private static final long serialVersionUID = 1L;
    public InsufficientFundsException(){
        super ("Insufficient funds.");
    }
}

