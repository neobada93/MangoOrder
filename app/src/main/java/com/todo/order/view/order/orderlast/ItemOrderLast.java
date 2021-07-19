package com.todo.order.view.order.orderlast;

import java.io.Serializable;

public class ItemOrderLast implements Serializable {
    private String orderDate, itemName;

    public ItemOrderLast(String orderDate, String itemName, int itemCount) {
        this.orderDate = orderDate;
        this.itemName = itemName;
    }

    public ItemOrderLast() {

    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
