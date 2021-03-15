package com.ethoca.cart.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EmptyCartException extends RuntimeException{
    public EmptyCartException(String s){
        super(s);
    }
}
