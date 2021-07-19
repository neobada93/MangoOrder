package com.todo.order.view.selfcheck;

import java.io.Serializable;

public class SelfCheckItem implements Serializable {
    private String chkcd, chkItem, chk;

    public SelfCheckItem(String chkcd, String chkItem, String chk) {
        this.chkcd = chkcd;
        this.chkItem = chkItem;
        this.chk = chk;
    }

    public SelfCheckItem() {

    }

    public String getChkcd() {
        return chkcd;
    }

    public void setChkcd(String chkcd) {
        this.chkcd = chkcd;
    }

    public String getChkItem() {
        return chkItem;
    }

    public void setChkItem(String chkItem) {
        this.chkItem = chkItem;
    }

    public String getChk() {
        return chk;
    }

    public void setChk(String chk) {
        this.chk = chk;
    }
}
