package com.ethoca.cart.controller;

import com.ethoca.cart.exception.AvailabilityException;
import com.ethoca.cart.exception.EmptyCartException;
import com.ethoca.cart.exception.ProductNotFoundException;
import com.ethoca.cart.model.CartProduct;
import com.ethoca.cart.model.OrderProduct;
import com.ethoca.cart.model.db.OrderConfirmation;
import com.ethoca.cart.model.db.Product;
import com.ethoca.cart.service.ProdService;
import com.ethoca.cart.utils.ControllerUtils;
import com.ethoca.cart.utils.ServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
    public ResponseEntity<List<Product>> getProd(@PathVariable String name, final HttpServletRequest request) {
        log.info(request.getSession().getId() + " : Retrieving products with given keyword");
        return ResponseEntity.status(HttpStatus.OK).body(prodService.getProdList(name));
    }

    //Retrieve all the products in the market
    @GetMapping("/retrieve")
    public ResponseEntity<List<Product>> getProdAll(final HttpServletRequest request) {
        log.info(request.getSession().getId() + " : Retrieving all products");
        return ResponseEntity.status(HttpStatus.OK).body(prodService.getAll());
    }

    //Place the order from your cart
    @PostMapping("/order")
    public ResponseEntity<OrderConfirmation> placeOrder(final HttpServletRequest request) throws Exception {
        Map<String, CartProduct> initialCart = (Map<String, CartProduct>) request.getSession().getAttribute(CART_SESSION_CONSTANT);
        log.info(request.getSession().getId() + " : Reviewing the order");
        //check if the cart is empty
        if (CollectionUtils.isEmpty(initialCart)) {
            log.error(request.getSession().getId() + " : Error submitting empty cart");
            throw new EmptyCartException("Cart is empty");
        }

        //check if items are still available during confirmation of order
        List<OrderProduct> orderProducts = new ArrayList<>();
        for (String key : initialCart.keySet())
            orderProducts.add(ControllerUtils.parse(initialCart.get(key)));
        List<String> chckQuantity = prodService.checkQuantityList(orderProducts);
        if (chckQuantity != null) {
            log.error(request.getSession().getId() + " : Error submitting order for products unavailable");
            throw new AvailabilityException(chckQuantity);
        }

        //Confirm order and update tables
        log.info(request.getSession().getId() + " : confirming the order and killing the session");
        OrderConfirmation orderConfirmation = prodService.confirmOrder(initialCart);
        request.getSession().invalidate();
        return ResponseEntity.status(HttpStatus.OK).body(orderConfirmation);
    }


    //Update your cart
    @PostMapping("/updatecart")
    public ResponseEntity<Map<String, CartProduct>> updateCart(@RequestBody @Valid List<OrderProduct> orderProducts, final HttpServletRequest request) {
        //Retrieve initial cart data stored in the session
        log.info(request.getSession().getId() + " : Reviewing the initial cart");
        Map<String, CartProduct> initialCart = (Map<String, CartProduct>) request.getSession().getAttribute(CART_SESSION_CONSTANT);
        if (CollectionUtils.isEmpty(initialCart)) {
            log.error(request.getSession().getId() + " : Error updating empty cart");
            throw new EmptyCartException("Cart is empty");
        }

        //Check if update is asked for items not present in the cart
        ControllerUtils.checkCart(initialCart, orderProducts);

        //Check if the cart can be updated and update them
        log.info(request.getSession().getId() + " : Processing the update of the cart");
        List<String> chckQuantity = prodService.checkQuantityList(orderProducts);
        if (chckQuantity == null) {
            ControllerUtils.updateCart(initialCart, orderProducts);
            request.getSession().setAttribute(CART_SESSION_CONSTANT, initialCart);
            return ResponseEntity.status(HttpStatus.OK).body(initialCart);
        } else {
            log.error(request.getSession().getId() + " : Error updating cart for products unavailable");
            throw new AvailabilityException(chckQuantity);
        }
    }

    //Add an item to your cart
    @PostMapping(value = "/addcart")
    public ResponseEntity<String> addCart(@RequestBody @Valid OrderProduct orderProduct, final HttpServletRequest request) throws Exception {

        //Get the initial cart
        log.info(request.getSession().getId() + " : Reviewing the initial cart");
        Map<String, CartProduct> initialCart = (Map<String, CartProduct>) request.getSession().getAttribute(CART_SESSION_CONSTANT);

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
        log.info(request.getSession().getId() + " : Processing the addition of product to the cart");
        Product product = prodService.getProd(orderProduct.getProductName());
        if (product.getQuantity() >= quantity) {
            CartProduct cartProduct = ControllerUtils.buildCartProduct(product, quantity);
            initialCart.put(orderProduct.getProductName(), cartProduct);
            request.getSession().setAttribute(CART_SESSION_CONSTANT, initialCart);
            return ResponseEntity.status(HttpStatus.OK).body("Product " + orderProduct.getProductName() + " added to the cart");
        } else {
            log.error(request.getSession().getId() + " : Error updating cart for products unavailable");
            throw new AvailabilityException(orderProduct.getProductName());
        }

    }

    //View your current cart
    @GetMapping(value = "/viewcart")
    public ResponseEntity<List<CartProduct>> viewCart(final HttpServletRequest request) {
        log.info(request.getSession().getId() + " : Retrieving the cart to view");
        Map<String, CartProduct> initialCart = (Map<String, CartProduct>) request.getSession().getAttribute(CART_SESSION_CONSTANT);
        if (CollectionUtils.isEmpty(initialCart)) {
            log.error(request.getSession().getId() + " : Error displaying empty cart");
            throw new EmptyCartException("Cart is empty");
        }
        List<CartProduct> result = new ArrayList();

        for (String key : initialCart.keySet()) {
            result.add(initialCart.get(key));
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    @DeleteMapping(value = "/deletecart")
    public ResponseEntity<String> deleteCart(final HttpServletRequest request) {
        log.info(request.getSession().getId() + " : Reviewing the initial cart");
        Map<String, CartProduct> initialCart = (Map<String, CartProduct>) request.getSession().getAttribute(CART_SESSION_CONSTANT);

        //check if the cart is empty
        if (CollectionUtils.isEmpty(initialCart)) {
            log.error(request.getSession().getId() + " : Error removing empty cart");
            throw new EmptyCartException("Cart is empty");
        }

        log.info(request.getSession().getId() + " : Remove the existing cart and kill the session");
        request.getSession().invalidate();
        return ResponseEntity.status(HttpStatus.OK).body("Cart removed");
    }

    @DeleteMapping(value = "/deleteproduct")
    public ResponseEntity<String> deleteProduct(@RequestBody String productName, final HttpServletRequest request) {
        log.info(request.getSession().getId() + " : Reviewing the initial cart");
        Map<String, CartProduct> initialCart = (Map<String, CartProduct>) request.getSession().getAttribute(CART_SESSION_CONSTANT);

        //check if the cart is empty
        if (CollectionUtils.isEmpty(initialCart)) {
            log.error(request.getSession().getId() + " : Error removing from an empty cart");
            throw new EmptyCartException("Cart is empty");
        }
        if (!initialCart.containsKey(productName)) {
            log.error(request.getSession().getId() + " : Error removing product not present in the cart");
            throw new ProductNotFoundException(productName);
        }

        log.info(request.getSession().getId() + " : Remove the product from the cart");
        initialCart.remove(productName);
        request.getSession().setAttribute(CART_SESSION_CONSTANT, initialCart);
        return ResponseEntity.status(HttpStatus.OK).body("Product " + productName + " removed");
    }

}
