package com.ethoca.cart.utils;

import com.ethoca.cart.exception.EmptyCartException;
import com.ethoca.cart.exception.ProductNotFoundException;
import com.ethoca.cart.model.CartProduct;
import com.ethoca.cart.model.OrderProduct;
import com.ethoca.cart.model.db.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ControllerUtilsTest {

    @Test
    public void testBuildCartProduct(){
        CartProduct result = ControllerUtils.buildCartProduct(mockProduct(),5);
        assertEquals(mockCartProduct(), result);
    }

    @Test
    public void testUpdateart(){
        Map<String, CartProduct> map = mockInitialCart();
        OrderProduct order = new OrderProduct();
        order.setQuantity(3);
        order.setProductName("TV");
        List<OrderProduct> orders = new ArrayList<>();
        orders.add(order);

        ControllerUtils.updateCart(map, orders);
        assertEquals(Integer.valueOf(3), map.get("TV").getQuantity());
    }

    @Test
    public void testParse(){
        OrderProduct result = ControllerUtils.parse(mockCartProduct());
        assertEquals(mockOrderProduct(), result);
    }

    @Test
    public void testCheckCart(){
        OrderProduct order = new OrderProduct();
        order.setQuantity(3);
        order.setProductName("WATCH");
        List<OrderProduct> orders = new ArrayList<>();
        orders.add(order);
        assertThrows(ProductNotFoundException.class, ()->ControllerUtils.checkCart(mockInitialCart(), orders));
    }

    @Test
    public void testCheckEmptyCart(){
        assertThrows(EmptyCartException.class, ()->ControllerUtils.checkEmptyCart(new HashMap<String, CartProduct>(), new String("id")));
    }


    private Product mockProduct(){
        return new Product("TV", BigDecimal.valueOf(100),10, "TV","tv.png");
    }

    private OrderProduct mockOrderProduct(){
        OrderProduct order = new OrderProduct();
        order.setProductName("TV");
        order.setQuantity(5);
        return order;
    }

    private CartProduct mockCartProduct(){
        CartProduct cart = new CartProduct();
        cart.setProductName("TV");
        cart.setCost(BigDecimal.valueOf(100));
        cart.setQuantity(5);
        cart.setTotalCost(BigDecimal.valueOf(500));
        return cart;
    }

    private Map<String, CartProduct> mockInitialCart(){
        Map<String, CartProduct> initialCart = new HashMap<>();
        initialCart.put("TV", mockCartProduct());
        return initialCart;
    }
}
