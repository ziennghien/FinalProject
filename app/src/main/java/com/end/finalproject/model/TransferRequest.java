package com.end.finalproject.model;

public class TransferRequest {
    public String fromAccount;
    public String toAccount;
    public String bankBin;   // ✅ thêm dòng này
    public double amount;
    public String note;

    public TransferRequest() {
        // Constructor rỗng cho Firebase hoặc Gson
    }

    public TransferRequest(String fromAccount, String toAccount, String bankBin, double amount, String note) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.bankBin = bankBin;
        this.amount = amount;
        this.note = note;
    }
}
