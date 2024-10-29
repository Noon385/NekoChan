package com.example.nekochancoffee.Model;

import java.io.Serializable;
import java.math.BigDecimal;

public class Cat implements Serializable {
    private int cat_id;
    private String cat_name;
    private String cat_status;
    private BigDecimal cat_price;
    private String cat_image;


    public Cat() {
    }

    public Cat(String cat_name, String cat_status, BigDecimal cat_price, String cat_image) {
        this.cat_name = cat_name;
        this.cat_status = cat_status;
        this.cat_price = cat_price;
        this.cat_image = cat_image;
    }

    // Getters và Setters
    public int getCatId() {
        return cat_id;
    }

    public void setCatId(int cat_id) {
        this.cat_id = cat_id;
    }

    public String getCatName() {
        return cat_name;
    }

    public void setCatName(String cat_name) {
        this.cat_name = cat_name;
    }

    public String getCatStatus() {
        return cat_status;
    }

    public void setCatStatus(String cat_status) {
        this.cat_status = cat_status;
    }

    public BigDecimal getCatPrice() {
        return cat_price;
    }
    public void setCatPrice(BigDecimal cat_price) {
        this.cat_price = cat_price;
    }

    // Getter và Setter cho ảnh dưới dạng base64
    public String getCatImage() {
        return cat_image;
    }

    public void setCatImage(String cat_image) {
        this.cat_image = cat_image;
    }
    @Override
    public String toString() {
        return cat_name; // Hiển thị tên mèo
    }
}
