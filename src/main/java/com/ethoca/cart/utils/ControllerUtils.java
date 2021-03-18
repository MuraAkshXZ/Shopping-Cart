package com.ethoca.cart.utils;

import com.ethoca.cart.exception.EmptyCartException;
import com.ethoca.cart.exception.ProductNotFoundException;
import com.ethoca.cart.model.CartProduct;
import com.ethoca.cart.model.OrderProduct;
import com.ethoca.cart.model.db.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ControllerUtils {

    private static final Logger log = LoggerFactory.getLogger(ControllerUtils.class);

    public static CartProduct buildCartProduct(Product product, Integer quantity){
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

    public static void checkCart(Map<String, CartProduct> initialCart, List<OrderProduct> orderProducts){
        List<String> result = new ArrayList<>();
        for(OrderProduct product : orderProducts){
            if(!initialCart.containsKey(product.getProductName()))
                result.add(product.getProductName());
        }
        if(!result.isEmpty())
            throw new ProductNotFoundException(result);
    }

    public static void checkEmptyCart(Map<String, CartProduct> initialCart, String id){
        if (CollectionUtils.isEmpty(initialCart)) {
            log.error(id + " : Error submitting empty cart");
            throw new EmptyCartException("Cart is empty");
        }
    }
}
