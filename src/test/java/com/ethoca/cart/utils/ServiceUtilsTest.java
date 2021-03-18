package com.ethoca.cart.utils;

import com.ethoca.cart.model.CartProduct;
import org.junit.jupiter.api.Test;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ServiceUtilsTest {

    private List<CartProduct> setup() {
        CartProduct cartProduct1 = new CartProduct();
        cartProduct1.setProductName("TV");
        cartProduct1.setTotalCost(BigDecimal.valueOf(100));
        cartProduct1.setQuantity(10);
        cartProduct1.setCost(BigDecimal.valueOf(10));
        List<CartProduct> cart = new ArrayList<>();
        cart.add(cartProduct1);

        return cart;
    }

    @Test
    public void testCartToByte() throws Exception {
        byte[] bytes = ServiceUtils.cartToByte(setup());
        assertNotNull(bytes);
    }

    @Test
    public void testByteToCart() throws Exception {
        byte[] bytes = ServiceUtils.cartToByte(setup());
        List<CartProduct> result = ServiceUtils.byteToCart(bytes);
        assertEquals(setup(), result);
    }
}
