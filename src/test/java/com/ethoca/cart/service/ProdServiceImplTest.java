package com.ethoca.cart.service;

import com.ethoca.cart.exception.ProductNotFoundException;
import com.ethoca.cart.model.CartProduct;
import com.ethoca.cart.model.OrderProduct;
import com.ethoca.cart.model.db.OrderConfirmation;
import com.ethoca.cart.model.db.Product;
import com.ethoca.cart.repository.OrderRepository;
import com.ethoca.cart.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;

@ExtendWith(MockitoExtension.class)
public class ProdServiceImplTest {

    @Mock
    ProductRepository productRepository;

    @Mock
    OrderRepository orderRepository;

    @InjectMocks
    ProdServiceImpl prodService;

    @Test
    public void testGetProd(){
        Mockito.when(productRepository.findByProductName("TV")).thenReturn(new Product("TV", BigDecimal.valueOf(10),10,"TV","TV"));
        assertNotNull(prodService.getProd("TV"));
    }

    @Test
    public void testGetProdException(){
        assertThrows(ProductNotFoundException.class, ()->prodService.getProd("TVX"));
    }

    @Test
    public void testGetProdId(){
        Mockito.when(productRepository.findByProductId(1)).thenReturn(new Product("TV", BigDecimal.valueOf(10),10,"TV","TV"));
        assertNotNull(prodService.getProdId(1));
    }

    @Test
    public void testGetProdList(){
        Mockito.when(productRepository.findByProductNameContaining("TV")).thenReturn(getProducts());
        assertNotNull(prodService.getProdList("TV"));
    }

    @Test
    public void testGetAll(){
        Mockito.when(productRepository.findAll(Sort.by(Sort.Direction.ASC, "productName"))).thenReturn(getProducts());
        assertNotNull(prodService.getAll());
    }

    @Test
    public void checkQuantityList(){
        List<String> names = new ArrayList<>();
        names.add("TV");
        Mockito.when(productRepository.findAllByProductNameIn(anyList())).thenReturn(getProducts());
        assertNull(prodService.checkQuantityList(getOrderProduct("TV",5)));
    }

    @Test
    public void checkQuantityList2(){
        List<String> names = new ArrayList<>();
        names.add("TV");
        Mockito.when(productRepository.findAllByProductNameIn(anyList())).thenReturn(getProducts());
        assertNotNull(prodService.checkQuantityList(getOrderProduct("TV",20)));
    }

    @Test
    public void checkOrderConfirmation() throws Exception {
        Mockito.when(productRepository.findAllByProductNameIn(anyList())).thenReturn(getProducts());
        assertNotNull(prodService.confirmOrder(getMap()));
    }



    private List<Product> getProducts(){
        Product product1 = new Product("TV", BigDecimal.valueOf(100), 10, "TV", "tv.png");
        List<Product> products = new ArrayList<>();
        products.add(product1);
        return products;
    }

    private Map<String, CartProduct> getMap(){
        CartProduct cartProduct = new CartProduct();
        cartProduct.setProductName("TV");
        cartProduct.setQuantity(10);
        cartProduct.setTotalCost(BigDecimal.valueOf(1000));
        cartProduct.setCost(BigDecimal.valueOf(100));
        Map<String, CartProduct> map = new HashMap<>();
        map.put("TV", cartProduct);
        return map;
    }

    private List<OrderProduct> getOrderProduct(String name, int quantity){
        OrderProduct op = new OrderProduct();
        op.setQuantity(quantity);
        op.setProductName(name);
        List<OrderProduct> orderProduct = new ArrayList<>();
        orderProduct.add(op);
        return orderProduct;
    }

}
