package com.ethoca.cart.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

//@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductNotFoundException extends RuntimeException{

    public ProductNotFoundException(String exception) {
        super(exception);
    }

    public ProductNotFoundException(List<String> exception) {
        super(exception.toString());
    }
}
