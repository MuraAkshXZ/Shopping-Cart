package com.ethoca.cart.exception;

import java.util.List;

public class ProductNotFoundException extends RuntimeException{

    public ProductNotFoundException(String exception) {
        super(exception);
    }

    public ProductNotFoundException(List<String> exception) {
        super("Invalid cart update : " + exception.toString());
    }
}
