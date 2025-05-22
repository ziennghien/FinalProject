package com.end.finalproject.model;

public class AccountLookupResponse {
    public boolean success;
    public Data data;

    public static class Data {
        public String accountName;
        public String accountNumber;
    }
}
