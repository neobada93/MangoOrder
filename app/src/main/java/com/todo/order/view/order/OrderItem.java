package com.todo.order.view.order;

import java.io.Serializable;

public class OrderItem implements Serializable {
    private int id, icon;
    private String name, cd, unit, qty, price, chk, url;

    public OrderItem(int id, String qty, String price, String name, String cd, String unit, String url) {
        this.id = id;
        this.qty = qty;
        this.price = price;
        this.name = name;
        this.cd = cd;
        this.unit = unit;
        this.url = url;
    }

    public OrderItem() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getPrice() { return price; }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCd() {
        return cd;
    }

    public void setCd(String cd) {
        this.cd = cd;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getChk() {
        return chk;
    }

    public void setChk(String chk) {
        this.chk = chk;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
