package com.end.finalproject.entertainment;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.end.finalproject.R;
import com.end.finalproject.adapter.TicketAdapter;
import com.end.finalproject.model.TicketItem;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MyTicketActivity extends AppCompatActivity {
    private static final String DB_URL =
            "https://finalprojectandroid-ab72b-default-rtdb.asia-southeast1.firebasedatabase.app";

    private RecyclerView rvMyTickets;
    private TicketAdapter adapter;
    private List<TicketItem> ticketList = new ArrayList<>();
    private String userId;
    private DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ticket);

        userId = getIntent().getStringExtra("key");

        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        rvMyTickets = findViewById(R.id.rvMyTickets);
        rvMyTickets.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TicketAdapter(ticketList);
        rvMyTickets.setAdapter(adapter);

        db = FirebaseDatabase.getInstance(DB_URL).getReference();
        loadTicketsFromFirebase();
    }

    private void loadTicketsFromFirebase() {
        db.child("tickets").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ticketList.clear();
                for (DataSnapshot ticketSnap : snapshot.getChildren()) {
                    String customerId = ticketSnap.child("customerId").getValue(String.class);
                    if (userId.equals(customerId)) {
                        final String category = ticketSnap.child("category").getValue(String.class);
                        final String entertainmentId = ticketSnap.child("entertainmentId").getValue(String.class);
                        final String createdAt = ticketSnap.child("createdAt").getValue(String.class);

                        final List<String> chosen = new ArrayList<>();
                        for (DataSnapshot seatSnap : ticketSnap.child("chosen").getChildren()) {
                            chosen.add(seatSnap.getValue(String.class));
                        }

                        final Long price = ticketSnap.child("price").getValue(Long.class) != null
                                ? ticketSnap.child("price").getValue(Long.class) : 0L;

                        if ("movie".equals(category)) {
                            db.child("movies").child(entertainmentId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot movieSnap) {
                                    String name = movieSnap.child("name").getValue(String.class);
                                    String date = movieSnap.child("date").getValue(String.class);
                                    ticketList.add(new TicketItem("movie", name, date, chosen, price, createdAt));
                                    sortAndNotify();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {}
                            });
                        } else if ("flight".equals(category)) {
                            db.child("flights").child(entertainmentId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot flightSnap) {
                                    String departure = flightSnap.child("departure").getValue(String.class);
                                    String destination = flightSnap.child("destination").getValue(String.class);
                                    String date = flightSnap.child("date").getValue(String.class);
                                    String name = departure + " → " + destination;
                                    ticketList.add(new TicketItem("flight", name, date, chosen, price, createdAt));
                                    sortAndNotify();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {}
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void sortAndNotify() {
        Collections.sort(ticketList, new Comparator<TicketItem>() {
            @Override
            public int compare(TicketItem o1, TicketItem o2) {
                if (o1.getCreatedAt() == null) return 1;
                if (o2.getCreatedAt() == null) return -1;
                return o2.getCreatedAt().compareTo(o1.getCreatedAt()); // mới nhất lên đầu
            }
        });
        adapter.notifyDataSetChanged();
    }
}
