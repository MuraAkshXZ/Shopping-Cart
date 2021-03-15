package com.ethoca.cart.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;

@Controller
public class UiController {

    private static final Logger log = LoggerFactory.getLogger(UiController.class);

    @GetMapping(value = "/cart")
    public String cart(final HttpSession session) {
        log.info(session.getId() + " : Entering the Swagger UI");
        return "cart";
    }


}
