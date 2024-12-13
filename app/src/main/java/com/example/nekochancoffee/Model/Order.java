package com.example.nekochancoffee.Model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class Order implements Serializable {
    private int order_id;
    private String order_status;
    private String order_time;
    private int table_id;
    private int customer_id;
    private int customer_point;
    private int user_id;
    private int drink_id;
    private int cat_id;
    private BigDecimal drink_price;
private int total_amount;
    private String table_name;
    private String cat_name;
    private String customer_name;
    private String username;
    private String drink_name;
    private int amount;
    private BigDecimal total_price;
    private BigDecimal total;
    private int cat_amount;

    public Order() {
    }

    public Order(int order_id, String order_status, int table_id, int customer_id, int user_id, int drink_id, int cat_id, BigDecimal drink_price, String table_name, String cat_name, String customer_name, String username, String drink_name, int amount, BigDecimal total_price,BigDecimal total,String order_time,int customer_point,int cat_amount) {
        this.order_id = order_id;
        this.order_status = order_status;
        this.table_id = table_id;
        this.customer_id = customer_id;
        this.user_id = user_id;
        this.drink_id = drink_id;
        this.cat_id = cat_id;
        this.drink_price = drink_price;
        this.table_name = table_name;
        this.cat_name = cat_name;
        this.customer_name = customer_name;
        this.username = username;
        this.drink_name = drink_name;
        this.amount = amount;
        this.total_price = total_price;
        this.total=total;
        this.order_time = order_time;
        this.customer_point = customer_point;
        this.cat_amount= cat_amount;

    }

    public int getCat_amount() {
        return cat_amount;
    }

    public void setCat_amount(int cat_amount) {
        this.cat_amount = cat_amount;
    }

    public int getCustomer_point() {
        return customer_point;
    }

    public void setCustomer_point(int customer_point) {
        this.customer_point = customer_point;
    }

    public int getTotal_amount() {
        return total_amount;
    }

    public String getOrder_time() {
        return order_time;
    }

    public void setOrder_time(String order_time) {
        this.order_time = order_time;
    }

    public BigDecimal getTotal_price() {
        return total_price;
    }

    public void setTotal_price(BigDecimal total_price) {
        this.total_price = total_price;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public BigDecimal getDrink_price() {
        return drink_price;
    }

    public void setDrink_price(BigDecimal drink_price) {
        this.drink_price = drink_price;
    }

    public String getTable_name() {
        return table_name;
    }

    public void setTable_name(String table_name) {
        this.table_name = table_name;
    }

    public String getCat_name() {
        return cat_name;
    }

    public void setCat_name(String cat_name) {
        this.cat_name = cat_name;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDrink_name() {
        return drink_name;
    }

    public void setDrink_name(String drink_name) {
        this.drink_name = drink_name;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getTable_id() {
        return table_id;
    }

    public void setTable_id(int table_id) {
        this.table_id = table_id;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getDrink_id() {
        return drink_id;
    }

    public void setDrink_id(int drink_id) {
        this.drink_id = drink_id;
    }

    public int getCat_id() {
        return cat_id;
    }

    public void setCat_id(int cat_id) {
        this.cat_id = cat_id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

}
