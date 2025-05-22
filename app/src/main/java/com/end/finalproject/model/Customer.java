package com.end.finalproject.model;

public class Customer {
    private String id;
    private String name;
    private CheckingAccount checkingAccount;
    private SavingAccount savingAccount;
    private MortgageAccount mortgageAccount;

    @SuppressWarnings("unused")
    public Customer() { /* required for Firebase */ }

    public Customer(String id, String name) {
        this.id = id;
        this.name = name;
    }

    // id, name
    public String getId() { return id; }
    public String getName() { return name; }
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }

    // nested accounts
    public CheckingAccount getCheckingAccount() { return checkingAccount; }
    public SavingAccount getSavingAccount() { return savingAccount; }
    public MortgageAccount getMortgageAccount() { return mortgageAccount; }
    public void setCheckingAccount(CheckingAccount c) { this.checkingAccount = c; }
    public void setSavingAccount(SavingAccount s)    { this.savingAccount = s; }
    public void setMortgageAccount(MortgageAccount m){ this.mortgageAccount = m; }

    // nested classes
    public static class CheckingAccount {
        private String accountNumber;
        @SuppressWarnings("unused") public CheckingAccount() {}
        public String getAccountNumber() { return accountNumber; }
        public void setAccountNumber(String a) { this.accountNumber = a; }
    }

    public static class SavingAccount {
        private String accountNumber;
        @SuppressWarnings("unused") public SavingAccount() {}
        public String getAccountNumber() { return accountNumber; }
        public void setAccountNumber(String a) { this.accountNumber = a; }
    }

    public static class MortgageAccount {
        private String accountNumber;
        @SuppressWarnings("unused") public MortgageAccount() {}
        public String getAccountNumber() { return accountNumber; }
        public void setAccountNumber(String a) { this.accountNumber = a; }
    }
}
