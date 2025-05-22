package com.end.finalproject.model;

public class Flight {
    private String key, departure, destination, date, company;
    public Flight() {}
    public Flight(String key, String departure, String destination, String date, String company) {
        this.key = key; this.departure = departure;
        this.destination = destination; this.date = date;
        this.company = company;
    }
    // getters and setters
}