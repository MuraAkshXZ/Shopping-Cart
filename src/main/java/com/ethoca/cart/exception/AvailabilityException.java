package com.ethoca.cart.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Collections;
import java.util.List;

public class AvailabilityException extends RuntimeException{

    public AvailabilityException(List<String> s)
    {
        super("Shortage of following items : " + s.toString());
    }

    public AvailabilityException(String s)
    {
        super("Shortage of following item : " + s);
    }
}
