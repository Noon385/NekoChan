package com.example.nekochancoffee.Model;

import java.io.Serializable;

public class Table implements Serializable {
    private int table_id;
    private String table_name;
    private String table_status;
    private String table_description;

    public Table() {
    }

    public Table(int table_id, String table_name, String table_status, String table_description) {
        this.table_id = table_id;
        this.table_name = table_name;
        this.table_status = table_status;
        this.table_description = table_description;
    }

    public int getTable_id() {
        return table_id;
    }

    public void setTable_id(int table_id) {
        this.table_id = table_id;
    }

    public String getTable_name() {
        return table_name;
    }

    public void setTable_name(String table_name) {
        this.table_name = table_name;
    }

    public String getTable_status() {
        return table_status;
    }

    public void setTable_status(String table_status) {
        this.table_status = table_status;
    }

    public String getTable_description() {
        return table_description;
    }

    public void setTable_description(String table_description) {
        this.table_description = table_description;
    }
    @Override
    public String toString() {
        return table_name; // Hiển thị tên mèo
    }
}
