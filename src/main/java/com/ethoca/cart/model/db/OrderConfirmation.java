package com.ethoca.cart.model.db;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

@Entity
@Table(name="ORDER_CONFIRMATION")
public class OrderConfirmation implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="order_id")
    private int orderId;

    @Column(name = "order_list")
    private String orderList;

    @Column(name = "bill", nullable = false)
    private BigDecimal bill;

    @Column(name = "created_ts")
    private Date createdTs;

    public OrderConfirmation(String orderList, BigDecimal bill, Date createdTs) {
        this.orderList = orderList;
        this.bill = bill;
        this.createdTs = createdTs;
    }

    public OrderConfirmation(){

    }

    public int getOrderId() {
        return orderId;
    }

    public String getOrderList() {
        return orderList;
    }

    public void setOrderList(String orderList) {
        this.orderList = orderList;
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

    @Override
    public String toString() {
        return "OrderConfirmation{" +
                "orderId=" + orderId +
                ", orderList='" + orderList + '\'' +
                ", cost=" + bill +
                ", createdTs=" + createdTs +
                '}';
    }
}
