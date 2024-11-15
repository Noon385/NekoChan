package com.example.nekochancoffee.Model;

import java.io.Serializable;
import java.math.BigDecimal;

public class Payment implements Serializable {
    private int order_id;
    private BigDecimal total_price;

    public Payment() {
    }

    public Payment(int order_id, BigDecimal total_price) {
        this.order_id = order_id;
        this.total_price = total_price;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public BigDecimal getTotal_price() {
        return total_price;
    }

    public void setTotal_price(BigDecimal total_price) {
        this.total_price = total_price;
    }
}
