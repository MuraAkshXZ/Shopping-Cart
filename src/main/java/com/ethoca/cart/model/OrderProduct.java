package com.ethoca.cart.model;

import javax.validation.constraints.Positive;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

public class OrderProduct implements Serializable {

    private String productName;

    @Positive(message = "Minimum order should be 1")
    private int quantity;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
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
