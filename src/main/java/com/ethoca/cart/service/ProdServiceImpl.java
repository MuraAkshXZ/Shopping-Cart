package com.ethoca.cart.service;

import com.ethoca.cart.controller.CartController;
import com.ethoca.cart.exception.ProductNotFoundException;
import com.ethoca.cart.model.CartProduct;
import com.ethoca.cart.model.ConfirmProduct;
import com.ethoca.cart.model.OrderProduct;
import com.ethoca.cart.model.db.OrderConfirmation;
import com.ethoca.cart.model.db.Product;
import com.ethoca.cart.repository.OrderRepository;
import com.ethoca.cart.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProdServiceImpl implements ProdService {

    private static final Logger log = LoggerFactory.getLogger(ProdServiceImpl.class);

    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrderRepository orderRepository;


    @Override
    public Product getProd(String id) {
        Product product = productRepository.findByProductName(id);
        if (product == null) {
            log.error(id + " : product not available");
            throw new ProductNotFoundException(id);
        }
        return product;
    }

    @Override
    public Product getProdId(int id) {
        return productRepository.findByProductId(id);
    }

    @Override
    public List<Product> getProdList(String id) {
        return productRepository.findByProductNameContaining(id);
    }

    @Override
    public List<Product> getAll() {
        return productRepository.findAll(Sort.by(Sort.Direction.ASC, "productName"));
    }

    @Override
    public List<String> checkQuantityList(List<OrderProduct> orderProducts) {
        //Make a list of the productnames to search
        List<String> productNames = new ArrayList<>();
        Map<String, Integer> quantity = new HashMap<>();
        for (OrderProduct orderProduct : orderProducts) {
            productNames.add(orderProduct.getProductName());
            quantity.put(orderProduct.getProductName(), orderProduct.getQuantity());
        }

        //Retieve all the products data
        List<String> result = new ArrayList<>();
        List<Product> products = productRepository.findAllByProductNameIn(productNames);

        //Compare the product quantity ordered with availability in the market
        for (Product product : products) {
            int initial = product.getQuantity();
            if (initial < quantity.get(product.getProductName())) {
                result.add(product.getProductName());
            }
        }
        if (result.size() >= 1)
            return result;
        else
            return null;
    }

    @Override
    public boolean checkQuantity(String productName, int quantity) {
        Product product = productRepository.findByProductName(productName);

        if (product == null) {
            log.error(productName + " : product not available");
            throw new ProductNotFoundException(productName);
        }

        if (product.getQuantity() < quantity)
            return false;
        else
            return true;

    }

    @Override
    public OrderConfirmation confirmOrder(Map<String, CartProduct> orderProducts) throws Exception {

        //Initialize the values for the order confirmation
        ObjectMapper mapper = new ObjectMapper();
        Date date = new Date(System.currentTimeMillis());
        BigDecimal bill = BigDecimal.valueOf(0);
        List<ConfirmProduct> confirmProducts = new ArrayList<>();

        //Get the exisitng list of products available in the website
        List<String> productNames = new ArrayList<>();
        Map<String, Integer> quantityChange = new HashMap<>();
        for (String productName : orderProducts.keySet()) {
            productNames.add(productName);
            quantityChange.put(productName, orderProducts.get(productName).getQuantity());
        }
        List<Product> products = productRepository.findAllByProductNameIn(productNames);

        //update the quantities and calculate the total bill
        for (Product product : products) {
            int initial = product.getQuantity();
            product.setQuantity(initial - quantityChange.get(product.getProductName()));

            confirmProducts.add(new ConfirmProduct(product.getProductName(),
                    quantityChange.get(product.getProductName()), product.getPrice(),
                    product.getPrice().multiply(BigDecimal.valueOf(quantityChange.get(product.getProductName())))));

            bill = bill.add(product.getPrice().multiply(BigDecimal.valueOf(quantityChange.get(product.getProductName()))));
        }

        String orderList = mapper.writeValueAsString(confirmProducts);
        //Perist the tables
        productRepository.saveAll(products);
        OrderConfirmation confirmation = new OrderConfirmation(orderList, bill, date);
        orderRepository.saveAndFlush(confirmation);

        return confirmation;
    }
}
