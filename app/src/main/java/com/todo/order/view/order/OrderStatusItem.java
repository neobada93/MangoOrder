package com.todo.order.view.order;

import java.io.Serializable;

public class OrderStatusItem implements Serializable {

    private String ordDt;
    private String price;

    public OrderStatusItem(String ordDt, String price) {
        this.ordDt = ordDt;
        this.price = price;
    }

    public OrderStatusItem() {

    }

    public String getOrdDt() {
        return ordDt;
    }

    public void setOrdDt(String ordDt) {
        this.ordDt = ordDt;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
