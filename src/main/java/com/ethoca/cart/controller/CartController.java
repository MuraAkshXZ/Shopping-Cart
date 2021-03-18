package com.ethoca.cart.controller;

import com.ethoca.cart.exception.AvailabilityException;
import com.ethoca.cart.exception.ProductNotFoundException;
import com.ethoca.cart.model.CartProduct;
import com.ethoca.cart.model.OrderProduct;
import com.ethoca.cart.model.db.OrderConfirmation;
import com.ethoca.cart.model.db.Product;
import com.ethoca.cart.service.ProdService;
import com.ethoca.cart.utils.ControllerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.*;

@RestController
@Validated
@CrossOrigin
public class CartController {

    private static final String CART_SESSION_CONSTANT = "CART_SESSION";
    private static final Logger log = LoggerFactory.getLogger(CartController.class);

    private final ProdService prodService;

    @Autowired
    public CartController(ProdService prodService){
        this.prodService = prodService;
    }

    //List all the products in the website containing the keyword
    @GetMapping("/list/{name}")
    public ResponseEntity<List<Product>> getProd(@PathVariable String name, final HttpSession session) {
        log.info(session.getId() + " : Retrieving products with given keyword");
        return ResponseEntity.status(HttpStatus.OK).body(prodService.getProdList(name));
    }

    //Retrieve all the products in the market
    @GetMapping("/retrieve")
    public ResponseEntity<List<Product>> getProdAll(final HttpSession session) {
        log.info(session.getId() + " : Retrieving all products");
        return ResponseEntity.status(HttpStatus.OK).body(prodService.getAll());
    }

    //Place the order from your cart
    @PostMapping("/order")
    public ResponseEntity<OrderConfirmation> placeOrder(final HttpSession session) throws Exception {
        Map<String, CartProduct> initialCart = (Map<String, CartProduct>) session.getAttribute(CART_SESSION_CONSTANT);
        log.info(session.getId() + " : Reviewing the order");
        //check if the cart is empty
        ControllerUtils.checkEmptyCart(initialCart, session.getId());

        //check if items are still available during confirmation of order
        List<OrderProduct> orderProducts = new ArrayList<>();
        for (String key : initialCart.keySet())
            orderProducts.add(ControllerUtils.parse(initialCart.get(key)));
        List<String> chckQuantity = prodService.checkQuantityList(orderProducts);
        if (chckQuantity != null) {
            log.error(session.getId() + " : Error submitting order for products unavailable");
            throw new AvailabilityException(chckQuantity);
        }

        //Confirm order and update tables
        log.info(session.getId() + " : confirming the order and killing the session");
        OrderConfirmation orderConfirmation = prodService.confirmOrder(initialCart);
        session.invalidate();
        return ResponseEntity.status(HttpStatus.OK).body(orderConfirmation);
    }


    //Update your cart
    @PostMapping("/updatecart")
    public ResponseEntity<Map<String, CartProduct>> updateCart(@RequestBody @Valid List<OrderProduct> orderProducts, final HttpSession session) {
        //Retrieve initial cart data stored in the session
        log.info(session.getId() + " : Reviewing the initial cart");
        Map<String, CartProduct> initialCart = (Map<String, CartProduct>) session.getAttribute(CART_SESSION_CONSTANT);
        ControllerUtils.checkEmptyCart(initialCart, session.getId());

        //Check if update is asked for items not present in the cart
        ControllerUtils.checkCart(initialCart, orderProducts);

        //Check if the cart can be updated and update them
        log.info(session.getId() + " : Processing the update of the cart");
        List<String> chckQuantity = prodService.checkQuantityList(orderProducts);
        if (chckQuantity == null) {
            ControllerUtils.updateCart(initialCart, orderProducts);
            session.setAttribute(CART_SESSION_CONSTANT, initialCart);
            return ResponseEntity.status(HttpStatus.OK).body(initialCart);
        } else {
            log.error(session.getId() + " : Error updating cart for products unavailable");
            throw new AvailabilityException(chckQuantity);
        }
    }

    //Add an item to your cart
    @PostMapping(value = "/addcart")
    public ResponseEntity<String> addCart(@RequestBody @Valid OrderProduct orderProduct, final HttpSession session) throws Exception {

        //Get the initial cart
        log.info(session.getId() + " : Reviewing the initial cart");
        Map<String, CartProduct> initialCart = (Map<String, CartProduct>) session.getAttribute(CART_SESSION_CONSTANT);

        Integer quantity = orderProduct.getQuantity();

        //Check if cart is empty and instantiate it, if product is already there in the cart, update them
        if (CollectionUtils.isEmpty(initialCart)) {
            log.info("No Items added yet");
            initialCart = new HashMap<>();
        } else {
            if (initialCart.containsKey(orderProduct.getProductName()))
                quantity = quantity + initialCart.get(orderProduct.getProductName()).getQuantity();
        }

        //check if the store has the available quantity and update the cart
        log.info(session.getId() + " : Processing the addition of product to the cart");
        Product product = prodService.getProd(orderProduct.getProductName());
        if (product.getQuantity() >= quantity) {
            CartProduct cartProduct = ControllerUtils.buildCartProduct(product, quantity);
            initialCart.put(orderProduct.getProductName(), cartProduct);
            session.setAttribute(CART_SESSION_CONSTANT, initialCart);
            return ResponseEntity.status(HttpStatus.OK).body("Product " + orderProduct.getProductName() + " added to the cart");
        } else {
            log.error(session.getId() + " : Error updating cart for products unavailable");
            throw new AvailabilityException(orderProduct.getProductName());
        }

    }

    //View your current cart
    @GetMapping(value = "/viewcart")
    public ResponseEntity<List<CartProduct>> viewCart(final HttpSession session) {
        log.info(session.getId() + " : Retrieving the cart to view");
        Map<String, CartProduct> initialCart = (Map<String, CartProduct>) session.getAttribute(CART_SESSION_CONSTANT);
        ControllerUtils.checkEmptyCart(initialCart, session.getId());
        List<CartProduct> result = new ArrayList();

        for (String key : initialCart.keySet()) {
            result.add(initialCart.get(key));
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    //Delete the cart
    @DeleteMapping(value = "/deletecart")
    public ResponseEntity<String> deleteCart(final HttpSession session) {
        log.info(session.getId() + " : Reviewing the initial cart");
        Map<String, CartProduct> initialCart = (Map<String, CartProduct>) session.getAttribute(CART_SESSION_CONSTANT);

        //check if the cart is empty
        ControllerUtils.checkEmptyCart(initialCart, session.getId());

        log.info(session.getId() + " : Remove the existing cart and kill the session");
        session.invalidate();
        return ResponseEntity.status(HttpStatus.OK).body("Cart removed");
    }

    //Remove a product from the cart
    @DeleteMapping(value = "/deleteproduct")
    public ResponseEntity<String> deleteProduct(@RequestBody String productName, final HttpSession session) {
        log.info(session.getId() + " : Reviewing the initial cart");
        Map<String, CartProduct> initialCart = (Map<String, CartProduct>) session.getAttribute(CART_SESSION_CONSTANT);

        //check if the cart is empty
        ControllerUtils.checkEmptyCart(initialCart, session.getId());

        if (!initialCart.containsKey(productName)) {
            log.error(session.getId() + " : Error removing product not present in the cart");
            throw new ProductNotFoundException(productName);
        }

        log.info(session.getId() + " : Remove the product from the cart");
        initialCart.remove(productName);
        session.setAttribute(CART_SESSION_CONSTANT, initialCart);
        return ResponseEntity.status(HttpStatus.OK).body("Product " + productName + " removed");
    }

}
