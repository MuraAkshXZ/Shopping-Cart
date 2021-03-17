package com.ethoca.cart.model;

import javax.validation.constraints.Positive;
import java.io.Serializable;
import java.util.Objects;

public class OrderProduct implements Serializable {

    private String productName;

    @Positive(message = "Minimum order should be 1")
    private Integer quantity;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderProduct orderProduct = (OrderProduct) o;
        return quantity == orderProduct.quantity && Objects.equals(productName, orderProduct.productName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productName, quantity);
    }


    @Override
    public String toString() {
        return "OrderProduct{" +
                "productName='" + productName + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
