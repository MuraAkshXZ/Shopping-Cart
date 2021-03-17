package com.ethoca.cart.model.db;

import com.ethoca.cart.model.CartProduct;
import com.ethoca.cart.utils.ServiceUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "ORDER_CONFIRMATION")
public class OrderConfirmation implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;

    @Column(name = "cart_list", nullable = false)
    @Lob
    private byte[] cartList;

    @Column(name = "bill", nullable = false)
    private BigDecimal bill;

    @Column(name = "created_ts")
    private Date createdTs;

    public OrderConfirmation() {

    }

    public OrderConfirmation(byte[] cartList, BigDecimal bill, Date createdTs) {
        this.cartList = cartList;
        this.bill = bill;
        this.createdTs = createdTs;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public BigDecimal getBill() {
        return bill;
    }

    public void setBill(BigDecimal bill) {
        this.bill = bill;
    }

    public Date getCreatedTs() {
        return createdTs;
    }

    public void setCreatedTs(Date createdTs) {
        this.createdTs = createdTs;
    }

    public List<CartProduct> getCartList() throws Exception {
        return ServiceUtils.byteToCart(cartList);
    }

    public void setCartList(byte[] cartList) {
        this.cartList = cartList;
    }

    @Override
    public String toString() {
        return "OrderConfirmation{" +
                "orderId=" + orderId +
                ", cartList=" + Arrays.toString(cartList) +
                ", bill=" + bill +
                ", createdTs=" + createdTs +
                '}';
    }
}
