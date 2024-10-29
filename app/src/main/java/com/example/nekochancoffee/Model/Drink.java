package com.example.nekochancoffee.Model;

import java.io.Serializable;
import java.math.BigDecimal;

public class Drink implements Serializable {

    private int drink_id;
    private String drink_name;
    private String drink_status;
    private String drink_image;
    private BigDecimal drink_price;
    private int category_id;

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public int getDrink_id() {
        return drink_id;
    }

    public void setDrink_id(int drink_id) {
        this.drink_id = drink_id;
    }

    public String getDrink_name() {
        return drink_name;
    }

    public void setDrink_name(String drink_name) {
        this.drink_name = drink_name;
    }

    public String getDrink_status() {
        return drink_status;
    }

    public void setDrink_status(String drink_status) {
        this.drink_status = drink_status;
    }

    public String getDrink_image() {
        return drink_image;
    }

    public void setDrink_image(String drink_image) {
        this.drink_image = drink_image;
    }

    public BigDecimal getDrink_price() {
        return drink_price;
    }

    public void setDrink_price(BigDecimal drink_price) {
        this.drink_price = drink_price;
    }

    public Drink() {
    }

    public Drink(int drink_id, String drink_name, String drink_status, String drink_image, BigDecimal drink_price, int category_id) {
        this.drink_id = drink_id;
        this.drink_name = drink_name;
        this.drink_status = drink_status;
        this.drink_image = drink_image;
        this.drink_price = drink_price;
        this.category_id = category_id;
    }


}
