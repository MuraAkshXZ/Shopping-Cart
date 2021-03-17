package com.ethoca.cart.service;

import com.ethoca.cart.model.CartProduct;
import com.ethoca.cart.model.OrderProduct;
import com.ethoca.cart.model.db.OrderConfirmation;
import com.ethoca.cart.model.db.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ProdService {

    Product getProd(String id);

    Product getProdId(Integer id);

    List<Product> getProdList(String id);

    List<Product> getAll();

    List<String> checkQuantityList(List<OrderProduct> orderProducts);

    boolean checkQuantity(String productName, Integer quantity);

    OrderConfirmation confirmOrder(Map<String, CartProduct> orderProducts) throws Exception;




}
