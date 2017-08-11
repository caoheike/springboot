package com.reptile.model;

/**
 * Created by HotWong on 2017/6/8 0008.
 */
public class Option{
    private String title;
    private int order;
    private String value;
    private String name;

    @Override
    public String toString() {
        return "Option{" + "title='" + title + '\'' + "order='" + order + '\''
                + "value='" + value + '\'' + ", name='" + name + '\'' +'}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
