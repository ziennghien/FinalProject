// Flight.java
package com.end.finalproject.model;

import com.google.firebase.database.IgnoreExtraProperties;
import java.util.List;

@IgnoreExtraProperties
public class Flight {
    private String key;

    private List<String> available;
    private List<String> chosen;
    private String company;
    private String date;
    private String departure;
    private String destination;
    private long price;

    // Empty constructor needed for Firebase
    public Flight() {}

    public Flight(String key,List<String> available, List<String> chosen, String company,
                  String date, String departure, String destination, long price) {
        this.key = key;
        this.available = available;
        this.chosen = chosen;
        this.company = company;
        this.date = date;
        this.departure = departure;
        this.destination = destination;
        this.price = price;
    }

    public String getKey() { return key; }

    public void setKey(String key) { this.key = key; }

    public List<String> getAvailable() {
        return available;
    }

    public void setAvailable(List<String> available) {
        this.available = available;
    }

    public List<String> getChosen() {
        return chosen;
    }

    public void setChosen(List<String> chosen) {
        this.chosen = chosen;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }
}