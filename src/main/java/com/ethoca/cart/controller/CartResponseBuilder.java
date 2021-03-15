package com.ethoca.cart.controller;

import com.ethoca.cart.model.CartProduct;
import com.ethoca.cart.model.OrderProduct;
import com.ethoca.cart.model.db.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class CartResponseBuilder {

    public static CartProduct buildCartProduct(Product product, int quantity){
        CartProduct cartProduct = new CartProduct();
        cartProduct.setProductName(product.getProductName());
        cartProduct.setQuantity(quantity);
        cartProduct.setCost(product.getPrice());
        cartProduct.setTotalCost(product.getPrice().multiply(BigDecimal.valueOf(quantity)));

        return cartProduct;
    }

    public static void updateCart(Map<String, CartProduct> initialCart, List<OrderProduct> orderProducts){
        for (OrderProduct orderProduct : orderProducts) {
            CartProduct cartProduct = initialCart.get(orderProduct.getProductName());
            cartProduct.setQuantity(orderProduct.getQuantity());
            cartProduct.setTotalCost(cartProduct.getCost().multiply(BigDecimal.valueOf(cartProduct.getQuantity())));
            initialCart.put(orderProduct.getProductName(), cartProduct);
        }
    }

    public static OrderProduct parse(CartProduct cartProduct){
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setProductName(cartProduct.getProductName());
        orderProduct.setQuantity(cartProduct.getQuantity());
        return orderProduct;
    }
}
