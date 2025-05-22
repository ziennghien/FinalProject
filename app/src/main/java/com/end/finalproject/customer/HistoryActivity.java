// HistoryActivity.java
package com.end.finalproject.customer;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.end.finalproject.R;
import com.end.finalproject.adapter.HistoryAdapter;
import com.end.finalproject.model.History;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {
    private static final String DB_URL =
            "https://finalprojectandroid-ab72b-default-rtdb.asia-southeast1.firebasedatabase.app";
    private RecyclerView rvHistory;
    private HistoryAdapter adapter;
    private List<History> historyList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lich_su_giao_dich);

        // Nút back
        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        rvHistory = findViewById(R.id.rvLichSuGiaoDich);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HistoryAdapter(historyList);
        rvHistory.setAdapter(adapter);

        String currentId = getIntent().getStringExtra("key");

        FirebaseDatabase database = FirebaseDatabase.getInstance(DB_URL);
        DatabaseReference ref = database.getReference("historys");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                historyList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String key = child.getKey();  // Lấy key như "history1"
                    String customerId = child.child("customerId").getValue(String.class);
                    if (currentId.equals(customerId)) {
                        String date = child.child("date").getValue(String.class);
                        String status = child.child("balanceStatus").getValue(String.class);
                        String info = child.child("info").getValue(String.class);
                        historyList.add(new History(key, date, status, info));
                    }
                }

                // Sắp xếp danh sách
                // Sắp xếp theo date mới nhất đến cũ nhất
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault());
                historyList.sort((h1, h2) -> {
                    try {
                        Date d1 = sdf.parse(h1.getDate());
                        Date d2 = sdf.parse(h2.getDate());
                        return d2.compareTo(d1); // Mới nhất trước
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return 0; // Không đổi vị trí nếu lỗi
                    }
                });


                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

    }
}