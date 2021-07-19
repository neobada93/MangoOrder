package com.todo.order.view.order;

public class OrderStatusDetailItem {

    private String larNm;
    private String cnt;
    private String price;

    public OrderStatusDetailItem(String larNm, String cnt, String price) {
        this.larNm = larNm;
        this.cnt = cnt;
        this.price = price;
    }

    public OrderStatusDetailItem() {

    }

    public String getLarNm() {
        return larNm;
    }

    public void setLarNm(String larNm) {
        this.larNm = larNm;
    }

    public String getCnt() {
        return cnt;
    }

    public void setCnt(String cnt) {
        this.cnt = cnt;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
