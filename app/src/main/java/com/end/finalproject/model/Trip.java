package com.end.finalproject.model;

public class Trip {
    private String departure;
    private String destination;
    private String busCompany;
    private int seat;
    private int price;
    private String dateDepart;

    public Trip() { /* Required for Firebase */ }

    public Trip(String departure,
                String destination,
                String busCompany,
                int seat,
                int price,
                String dateDepart) {
        this.departure   = departure;
        this.destination = destination;
        this.busCompany  = busCompany;
        this.seat        = seat;
        this.price       = price;
        this.dateDepart  = dateDepart;
    }

    public String getDeparture()    { return departure; }
    public String getDestination()  { return destination; }
    public String getBusCompany()   { return busCompany; }
    public int    getSeat()         { return seat; }
    public int    getPrice()        { return price; }
    public String getDateDepart()   { return dateDepart; }

    public void setDeparture(String departure)       { this.departure = departure; }
    public void setDestination(String destination)   { this.destination = destination; }
    public void setBusCompany(String busCompany)     { this.busCompany = busCompany; }
    public void setSeat(int seat)                    { this.seat = seat; }
    public void setPrice(int price)                  { this.price = price; }
    public void setDateDepart(String dateDepart)     { this.dateDepart = dateDepart; }
}
