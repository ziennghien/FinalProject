package com.end.finalproject.model;

public class Bank {
    public int id;
    public String name;
    public String code;
    public String bin;
    public String shortName;
    public String logo;
    public int transferSupported;
    public int lookupSupported;

    @Override
    public String toString() {
        return shortName + " - " + name;
    }
}
