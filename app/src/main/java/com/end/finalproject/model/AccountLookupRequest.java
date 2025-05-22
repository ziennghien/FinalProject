package com.end.finalproject.model;

public class AccountLookupRequest {
    private String bin;
    private String accountNumber;

    public AccountLookupRequest(String bin, String accountNumber) {
        this.bin = bin;
        this.accountNumber = accountNumber;
    }
}
