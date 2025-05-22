// History.java
package com.end.finalproject.model;

public class History {
    private String key;
    private  String date;
    private String balanceStatus;
    private String info;

    public History() {}

    public History(String key, String date, String balanceStatus, String info) {
        this.key = key;
        this.date = date;
        this.balanceStatus = balanceStatus;
        this.info = info;
    }
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    public String getBalanceStatus() {
        return balanceStatus;
    }

    public void setBalanceStatus(String balanceStatus) {
        this.balanceStatus = balanceStatus;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}