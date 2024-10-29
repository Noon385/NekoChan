package com.example.nekochancoffee.Model;

import java.io.Serializable;

public class Customer implements Serializable {
    private int customer_id;
    private String customer_name;
    private String customer_phone;
    private String customer_point;

    public Customer(int customer_id, String customer_name, String customer_phone, String customer_point) {
        this.customer_id = customer_id;
        this.customer_name = customer_name;
        this.customer_phone = customer_phone;
        this.customer_point = customer_point;
    }

    public Customer() {
    }

    public Customer(String customer_name, String customer_phone, String customer_point) {
        this.customer_name = customer_name;
        this.customer_phone = customer_phone;
        this.customer_point = customer_point;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getCustomer_phone() {
        return customer_phone;
    }

    public void setCustomer_phone(String customer_phone) {
        this.customer_phone = customer_phone;
    }

    public String getCustomer_point() {
        return customer_point;
    }

    public void setCustomer_point(String customer_point) {
        this.customer_point = customer_point;
    }
    @Override
    public String toString() {
        return customer_name;
    }
}
