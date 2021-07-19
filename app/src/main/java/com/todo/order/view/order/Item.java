package com.todo.order.view.order;

public class Item {
    private int id, icon;
    private String name, cd, unit, price;

    public Item(int id, String price, String name, String cd, String unit) {
        this.id = id;
        this.price = price;
        this.name = name;
        this.cd = cd;
        this.unit = unit;
    }

    public Item() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}
