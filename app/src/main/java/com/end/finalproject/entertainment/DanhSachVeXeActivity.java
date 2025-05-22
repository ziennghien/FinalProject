package com.end.finalproject.entertainment;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.end.finalproject.R;
import com.end.finalproject.adapter.TripAdapter;
import com.end.finalproject.model.Trip;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DanhSachVeXeActivity extends AppCompatActivity {
    private static final String DB_URL =
            "https://finalprojectandroid-ab72b-default-rtdb.asia-southeast1.firebasedatabase.app";

    private RecyclerView rvTrips;
    private TripAdapter adapter;
    private List<Trip> tripList;

    // Parameters passed from search
    private String searchDeparture;
    private String searchDestination;
    private String searchBusCompany;

    private String searchDateDepart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danh_sach_chuyen_di);

        // Retrieve search parameters
        searchDeparture   = getIntent().getStringExtra("departure");
        searchDestination = getIntent().getStringExtra("destination");
        searchBusCompany  = getIntent().getStringExtra("busCompany");
        searchDateDepart  = getIntent().getStringExtra("dateDepart");

        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        rvTrips = findViewById(R.id.rvTrips);
        rvTrips.setLayoutManager(new LinearLayoutManager(this));
        tripList = new ArrayList<>();
        adapter = new TripAdapter(tripList, null);
        rvTrips.setAdapter(adapter);

        loadTripsFromFirebase();
    }

    private void loadTripsFromFirebase() {
        DatabaseReference tripRef = FirebaseDatabase.getInstance(DB_URL)
                .getReference("trip");

        tripRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tripList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Trip t = snap.getValue(Trip.class);
                    if (t == null) continue;
                    // filter by search criteria
                    if (matchesSearch(t)) {
                        tripList.add(t);
                    }
                }
                if (tripList.isEmpty()) {
                    Toast.makeText(DanhSachVeXeActivity.this,
                            "Không tìm thấy chuyến phù hợp.",
                            Toast.LENGTH_SHORT).show();
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DanhSachVeXeActivity.this,
                        "Lỗi tải danh sách chuyến: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean matchesSearch(Trip t) {
        boolean matchDepart = searchDeparture == null || searchDeparture.isEmpty()
                || t.getDeparture().equalsIgnoreCase(searchDeparture);

        boolean matchDest = searchDestination == null || searchDestination.isEmpty()
                || t.getDestination().equalsIgnoreCase(searchDestination);

        boolean matchCompany = searchBusCompany == null || searchBusCompany.isEmpty()
                || t.getBusCompany().equalsIgnoreCase(searchBusCompany);

        boolean matchDate = searchDateDepart == null || searchDateDepart.isEmpty()
                || t.getDateDepart().equals(searchDateDepart);

        return matchDepart && matchDest && matchCompany && matchDate;
    }
}