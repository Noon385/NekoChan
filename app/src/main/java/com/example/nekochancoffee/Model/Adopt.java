package com.example.nekochancoffee.Model;

import java.io.Serializable;

public class Adopt implements Serializable {
    private int adopt_id;
    private String adopt_time;
    private int cat_id;
    private int customer_id;
    private String cat_status;
    private String cat_name;
    private String cat_image;
    private String customer_name;


    public Adopt() {
    }

    public Adopt(int adopt_id, String adopt_time, int cat_id, int customer_id, String cat_status, String cat_name, String cat_image, String customer_name) {
        this.adopt_id = adopt_id;
        this.adopt_time = adopt_time;
        this.cat_id = cat_id;
        this.customer_id = customer_id;
        this.cat_status = cat_status;
        this.cat_name = cat_name;
        this.cat_image = cat_image;
        this.customer_name = customer_name;
    }

    public int getAdopt_id() {
        return adopt_id;
    }

    public void setAdopt_id(int adopt_id) {
        this.adopt_id = adopt_id;
    }

    public String getAdopt_time() {
        return adopt_time;
    }

    public void setAdopt_time(String adopt_time) {
        this.adopt_time = adopt_time;
    }

    public int getCat_id() {
        return cat_id;
    }

    public void setCat_id(int cat_id) {
        this.cat_id = cat_id;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public String getCat_status() {
        return cat_status;
    }

    public void setCat_status(String cat_status) {
        this.cat_status = cat_status;
    }

    public String getCat_name() {
        return cat_name;
    }

    public void setCat_name(String cat_name) {
        this.cat_name = cat_name;
    }

    public String getCat_image() {
        return cat_image;
    }

    public void setCat_image(String cat_image) {
        this.cat_image = cat_image;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }
}
