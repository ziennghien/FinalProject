package com.end.finalproject.flight;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.end.finalproject.R;
import com.end.finalproject.adapter.FlightAdapter;
import com.end.finalproject.model.Flight;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FlightListActivity extends AppCompatActivity {
    private static final String DB_URL =
            "https://finalprojectandroid-ab72b-default-rtdb.asia-southeast1.firebasedatabase.app";

    private RecyclerView rvFlights;
    private FlightAdapter adapter;
    private List<Flight> flights = new ArrayList<>();
    private String dep, dst, date, comp, customerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danh_sach_ve_may_bay);

        // Nhận params và customerId từ Intent
        dep        = getIntent().getStringExtra("departure");
        dst        = getIntent().getStringExtra("destination");
        date       = getIntent().getStringExtra("date");
        comp       = getIntent().getStringExtra("company");
        customerId = getIntent().getStringExtra("customerId");

        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        rvFlights = findViewById(R.id.rvFlightResults);
        rvFlights.setLayoutManager(new LinearLayoutManager(this));

        adapter = new FlightAdapter(flights, flight -> {
            // Mở SeatSelectionActivity khi click, truyền key thực
            Intent intent = new Intent(this, SeatSelectionActivity.class);
            intent.putExtra("key", flight.getKey());
            intent.putStringArrayListExtra("available", new ArrayList<>(flight.getAvailable()));
            intent.putExtra("price", flight.getPrice());
            intent.putExtra("customerId", customerId);
            startActivity(intent);
        });
        rvFlights.setAdapter(adapter);

        // Load dữ liệu từ Firebase
        DatabaseReference ref = FirebaseDatabase.getInstance(DB_URL)
                .getReference("flights");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                flights.clear();
                for (DataSnapshot ch : snapshot.getChildren()) {
                    Flight f = ch.getValue(Flight.class);
                    if (f == null) continue;

                    // Gán key thực từ Firebase
                    f.setKey(ch.getKey());

                    boolean matchDeparture   = dep == null || dep.isEmpty()   || f.getDeparture().equalsIgnoreCase(dep);
                    boolean matchDestination = dst == null || dst.isEmpty()   || f.getDestination().equalsIgnoreCase(dst);
                    boolean matchDate        = date == null || date.isEmpty()  || f.getDate().equals(date);
                    boolean matchCompany     = comp == null || comp.isEmpty()  || f.getCompany().equalsIgnoreCase(comp);

                    if (matchDeparture && matchDestination && matchDate && matchCompany) {
                        flights.add(f);
                    }
                }
                adapter.notifyDataSetChanged();
                if (flights.isEmpty()) {
                    Toast.makeText(FlightListActivity.this,
                            "Không tìm thấy chuyến đi phù hợp", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
