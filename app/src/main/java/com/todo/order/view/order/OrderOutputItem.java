package com.todo.order.view.order;

import java.io.Serializable;

public class OrderOutputItem implements Serializable {

    private String ordDt;
    private String price;

    public OrderOutputItem(String ordDt, String price) {
        this.ordDt = ordDt;
        this.price = price;
    }

    public OrderOutputItem() {

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
