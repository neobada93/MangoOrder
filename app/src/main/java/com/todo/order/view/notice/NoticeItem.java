package com.todo.order.view.notice;

public class NoticeItem {
    private String title;
    private String date;
    private String content;
    private boolean expanded;
    private String url;
    private String position;
    private String seq;
    private String gbn;
    private String n;

    public NoticeItem(String title, String date, String content, String url, String position, String seq, String gbn, String n) {
        this.title = title;
        this.date = date;
        this.content = content;
        this.expanded = false;
        this.url = url;
        this.position = position;
        this.seq = seq;
        this.gbn = gbn;
        this.n = n;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getGbn() {
        return gbn;
    }

    public void setGbn(String gbn) {
        this.gbn = gbn;
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }
}
