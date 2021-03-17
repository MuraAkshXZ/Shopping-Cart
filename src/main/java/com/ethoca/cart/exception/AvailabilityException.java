package com.ethoca.cart.exception;

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
