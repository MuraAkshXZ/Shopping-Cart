package com.ethoca.cart.repository;

import com.ethoca.cart.model.db.Product;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    Product findByProductName(String productName);

    Product findByProductId(int productId);

    List<Product> findByProductNameContaining(String productName);

    List<Product> findAllByProductNameIn(List<String> productName);

}
