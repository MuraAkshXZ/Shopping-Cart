package com.ethoca.cart.controller;

import com.ethoca.cart.exception.AvailabilityException;
import com.ethoca.cart.exception.EmptyCartException;
import com.ethoca.cart.exception.ProductNotFoundException;
import com.ethoca.cart.model.CartProduct;
import com.ethoca.cart.model.OrderProduct;
import com.ethoca.cart.model.db.Product;
import com.ethoca.cart.service.ProdService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;


@ExtendWith(MockitoExtension.class)
public class CartControllerTest {

    @Mock
    ProdService prodService;

    @InjectMocks
    CartController cartController;

    @Test
    public void testGetProd() {
        Mockito.when(prodService.getProdList("id")).thenReturn(getProducts());
        ResponseEntity result = cartController.getProd("id", getSession("CART_SESSION"));
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    public void testGetProdAll() {
        Mockito.when(prodService.getAll()).thenReturn(getProducts());
        ResponseEntity result = cartController.getProdAll(getSession("CART_SESSION"));
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    public void testPlaceOrder() throws Exception {
        MockHttpSession session = getSession("CART_SESSION");
        Mockito.when(prodService.checkQuantityList(any())).thenReturn(null);
        ResponseEntity result = cartController.placeOrder(session);
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    public void testPlaceOrderEmptyCart() throws Exception {
        MockHttpSession session = getSession("EMPTY_CART");
        assertThrows(EmptyCartException.class, () -> cartController.placeOrder(session));
    }

    @Test
    public void testPlaceOrderInvalidProducts() throws Exception {
        MockHttpSession session = getSession("CART_SESSION");
        List<String> err = new ArrayList<>();
        err.add("TVX");
        Mockito.when(prodService.checkQuantityList(any())).thenReturn(err);
        assertThrows(AvailabilityException.class, () -> cartController.placeOrder(session));
    }

    @Test
    public void testUpdateCart() {
        MockHttpSession session = getSession("CART_SESSION");
        Mockito.when(prodService.checkQuantityList(any())).thenReturn(null);
        ResponseEntity result = cartController.updateCart(anyList(), session);
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    public void testUpdateCartInvalidProduct() {
        MockHttpSession session = getSession("CART_SESSION");
        List<String> err = new ArrayList<>();
        err.add("TVX");
        Mockito.when(prodService.checkQuantityList(any())).thenReturn(err);
        assertThrows(AvailabilityException.class, () -> cartController.updateCart(anyList(), session));
    }

    @Test
    public void testAddCartFirstTime() throws Exception {
        MockHttpSession session = getSession("NEW_SESSION");
        Mockito.when(prodService.getProd(any())).thenReturn(getProduct("TV", 20));
        ResponseEntity result = cartController.addCart(getOrderProduct("TV", 10), session);
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    public void testAddCart() throws Exception {
        MockHttpSession session = getSession("CART_SESSION");
        Mockito.when(prodService.getProd(any())).thenReturn(getProduct("TV", 30));
        ResponseEntity result = cartController.addCart(getOrderProduct("TV", 10), session);
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    public void testAddCartAvailability() throws Exception {
        MockHttpSession session = getSession("CART_SESSION");
        Mockito.when(prodService.getProd(any())).thenReturn(getProduct("TV", 15));
        assertThrows(AvailabilityException.class, () -> cartController.addCart(getOrderProduct("TV", 10), session));
    }

    @Test
    public void testViewCart() {
        MockHttpSession session = getSession("CART_SESSION");
        ResponseEntity result = cartController.viewCart(session);
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    public void testDeleteCart() {
        MockHttpSession session = getSession("CART_SESSION");
        ResponseEntity result = cartController.deleteCart(session);
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    public void testDeleteProduct() throws Exception {
        MockHttpSession session = getSession("CART_SESSION");
        ResponseEntity result = cartController.deleteProduct("TV", session);
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    public void testDeleteProductException() throws Exception {
        MockHttpSession session = getSession("CART_SESSION");
        assertThrows(ProductNotFoundException.class, () -> cartController.deleteProduct("TVX", session));
    }


    private MockHttpSession getSession(String id) {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(id, getMap());
        return session;
    }

    private List<Product> getProducts() {
        Product product1 = new Product("TV", BigDecimal.valueOf(100), 10, "TV", "tv.png");
        List<Product> products = new ArrayList<>();
        products.add(product1);
        return products;
    }

    private List<CartProduct> getCartProducts() {
        CartProduct cartProduct = new CartProduct();
        cartProduct.setProductName("TV");
        cartProduct.setQuantity(10);
        cartProduct.setTotalCost(BigDecimal.valueOf(1000));
        cartProduct.setCost(BigDecimal.valueOf(100));
        List<CartProduct> cart = new ArrayList<>();
        cart.add(cartProduct);
        return cart;
    }

    private Map<String, CartProduct> getMap() {
        CartProduct cartProduct = new CartProduct();
        cartProduct.setProductName("TV");
        cartProduct.setQuantity(10);
        cartProduct.setTotalCost(BigDecimal.valueOf(1000));
        cartProduct.setCost(BigDecimal.valueOf(100));
        Map<String, CartProduct> map = new HashMap<>();
        map.put("TV", cartProduct);
        return map;
    }

    private OrderProduct getOrderProduct(String name, int quantity) {
        OrderProduct op = new OrderProduct();
        op.setQuantity(quantity);
        op.setProductName(name);
        return op;
    }

    private Product getProduct(String name, int quantity) {
        Product prod = new Product(name, BigDecimal.valueOf(100), quantity, name, name);
        return prod;
    }

}
