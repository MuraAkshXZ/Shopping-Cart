package com.ethoca.cart.model.db;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Positive;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name="PRODUCT")
public class Product implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="product_id")
    private Integer productId;

    @Column(name = "product_name", unique = true)
    private String productName;

    @Column(name = "price", nullable = false)
    @DecimalMin(value = "0.00", message = "Price has to be a positive number")
    private BigDecimal price;

    @Column(name = "quantity", nullable = false)
    @Positive(message = "Minimum order should be 1")
    private Integer quantity;

    @Column(name = "description")
    private String description;

    @Column(name = "image")
    private String image;


    public Product(){

    }

    public Product(String productName, BigDecimal price, Integer quantity, String description, String image) {
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.description = description;
        this.image = image;
    }

    public Integer getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() { return image; }

    public void setImage(String image) { this.image = image; }


    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
