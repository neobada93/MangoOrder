package com.todo.order.view.talk;

import com.todo.order.util.StringUtil;

public class TalkItem {

    private int position;
    private int idx;
    private String file;
    private String content;
    private String icon;
    private String regdt;
    private String atfileconnno;
    private String fileName;
    private String url;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getAtfileconnno() {
        return atfileconnno;
    }

    public void setAtfileconnno(String atfileconnno) {
        this.atfileconnno = atfileconnno;
    }

    public String getRegdt() {
        return regdt;
    }

    public void setRegdt(String regdt) {
        this.regdt = regdt;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getContent() {
        return StringUtil.nullValue(content);
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "QuestionItem{" +
                "position=" + position +
                ", idx=" + idx +
                ", file='" + file + '\'' +
                ", content='" + content + '\'' +
                ", icon='" + icon + '\'' +
                ", regdt='" + regdt + '\'' +
                ", atfileconnno='" + atfileconnno + '\'' +
                '}';
    }
}