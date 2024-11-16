package com.example.nekochancoffee.Model;

import java.io.Serializable;
import java.math.BigDecimal;

public class Revenue implements Serializable {
    private String order_date;
    private BigDecimal total_revenue;

    public Revenue() {
    }

    public Revenue(String order_date, BigDecimal total_revenue) {
        this.order_date = order_date;
        this.total_revenue = total_revenue;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public BigDecimal getTotal_revenue() {
        return total_revenue;
    }

    public void setTotal_revenue(BigDecimal total_revenue) {
        this.total_revenue = total_revenue;
    }
}
