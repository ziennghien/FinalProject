package com.end.finalproject.model;

import java.util.List;

public class TicketItem {
    private String category;       // movie hoặc flight
    private String name;           // tên phim hoặc tuyến bay
    private String date;           // ngày chiếu hoặc bay
    private List<String> chosenSeats;
    private long price;
    private String createdAt;      // thời gian tạo vé (chuỗi)

    public TicketItem(String category, String name, String date,
                      List<String> chosenSeats, long price, String createdAt) {
        this.category = category;
        this.name = name;
        this.date = date;
        this.chosenSeats = chosenSeats;
        this.price = price;
        this.createdAt = createdAt;
    }

    public String getCategory() { return category; }
    public String getName() { return name; }
    public String getDate() { return date; }
    public List<String> getChosenSeats() { return chosenSeats; }
    public long getPrice() { return price; }
    public String getCreatedAt() { return createdAt; }
}
