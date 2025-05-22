// SeatSelectionActivity.java
package com.end.finalproject.flight;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.end.finalproject.R;
import com.end.finalproject.model.Flight;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SeatSelectionActivity extends AppCompatActivity {
    private GridLayout gridSeats;
    private TextView tvSelectedCount, tvTotalAmount;
    private Button btnPay;

    private List<String> availableSeats;
    private List<String> selectedSeats = new ArrayList<>();
    private long pricePerSeat;
    private String flightKey;
    private String customerId;

    private static final String DB_URL =
            "https://finalprojectandroid-ab72b-default-rtdb.asia-southeast1.firebasedatabase.app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chon_ghe_may_bay);

        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        gridSeats = findViewById(R.id.gridSeats);
        tvSelectedCount = findViewById(R.id.tvSelectedCount);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        btnPay = findViewById(R.id.btnPay);

        // Nhận dữ liệu Intent
        flightKey = getIntent().getStringExtra("key");
        availableSeats = getIntent().getStringArrayListExtra("available");
        pricePerSeat = getIntent().getLongExtra("price", 0);
        customerId = getIntent().getStringExtra("customerId");

        setupSeatButtons();

        btnPay.setOnClickListener(v -> {
            if (selectedSeats.isEmpty()) {
                Toast.makeText(this, "Chưa chọn ghế nào", Toast.LENGTH_SHORT).show();
                return;
            }
            long total = pricePerSeat * selectedSeats.size();

            String message = "Bạn có chắc chắn muốn đặt " + selectedSeats.size() + " ghế?"
                    + "\nGhế: " + String.join(", ", selectedSeats)
                    + "\nTổng tiền: " + String.format(Locale.US, "%,d VND", total);

            new android.app.AlertDialog.Builder(this)
                    .setTitle("Xác nhận đặt vé")
                    .setMessage(message)
                    .setPositiveButton("Xác nhận", (dialog, which) -> saveTicket(total))
                    .setNegativeButton("Huỷ", null)
                    .show();
        });

    }

    private void setupSeatButtons() {
        int childCount = gridSeats.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View v = gridSeats.getChildAt(i);
            if (v instanceof Button) {
                Button seatBtn = (Button) v;
                String seatNumber = seatBtn.getText().toString();
                if (!availableSeats.contains(seatNumber)) {
                    seatBtn.setEnabled(false);
                    seatBtn.setAlpha(0.5f);
                } else {
                    seatBtn.setOnClickListener(view -> toggleSeatSelection(seatBtn));
                }
            }
        }
    }

    private void toggleSeatSelection(Button seatBtn) {
        String num = seatBtn.getText().toString();
        if (selectedSeats.contains(num)) {
            selectedSeats.remove(num);
            seatBtn.setBackgroundColor(getResources().getColor(android.R.color.darker_gray)); // hoặc màu ghế trống mặc định
        } else {
            selectedSeats.add(num);
            seatBtn.setBackgroundColor(getResources().getColor(R.color.pink_primary)); // màu hồng
        }
        updateSummary();
    }


    private void updateSummary() {
        tvSelectedCount.setText("Ghế đã chọn: " + String.join(", ", selectedSeats));
        long total = pricePerSeat * selectedSeats.size();
        tvTotalAmount.setText("Tổng tiền: " + String.format(Locale.US, "%,d VND", total));
    }

    private void saveTicket(long amount) {
        DatabaseReference db = FirebaseDatabase.getInstance(DB_URL).getReference();

        // Bước 1: Kiểm tra số dư và lấy accountNumber
        DatabaseReference customerRef = db.child("customers").child(customerId);
        customerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(SeatSelectionActivity.this, "Không tìm thấy thông tin khách hàng", Toast.LENGTH_SHORT).show();
                    return;
                }




                Long balance = snapshot.child("checkingAccount").child("balance").getValue(Long.class);
                String accountNumber = snapshot.child("checkingAccount").child("accountNumber").getValue(String.class);


                if (balance == null || accountNumber == null) {
                    Toast.makeText(SeatSelectionActivity.this, "Dữ liệu tài khoản không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (balance < amount) {
                    Toast.makeText(SeatSelectionActivity.this, "Số dư không đủ để đặt vé", Toast.LENGTH_LONG).show();
                    return;
                }

                // Bước 2: Trừ tiền
                long newBalance = balance - amount;
                customerRef.child("checkingAccount").child("balance").setValue(newBalance);

                // Bước 3: Lưu vé
                DatabaseReference ticketsRef = db.child("tickets");
                ticketsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int ticketCount = (int) snapshot.getChildrenCount();
                        String ticketKey = "ticket" + (ticketCount + 1);

                        Map<String, Object> ticketData = new HashMap<>();
                        ticketData.put("category", "flight");
                        ticketData.put("chosen", selectedSeats);
                        ticketData.put("customerId", customerId);
                        ticketData.put("entertainmentId", flightKey);
                        ticketData.put("price", amount);

                        String timestamp = new SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.US).format(new Date());
                        ticketData.put("createdAt", timestamp);

                        ticketsRef.child(ticketKey).setValue(ticketData);

                        // Bước 4: Lưu lịch sử giao dịch
                        DatabaseReference historyRef = db.child("historys");
                        historyRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int historyCount = (int) snapshot.getChildrenCount();
                                String historyKey = "history" + (historyCount + 1);

                                Date now = new Date();
                                String datePart = new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(now);
                                String timePart = new SimpleDateFormat("HH:mm", Locale.US).format(now);
                                String timestamp = timePart + " - " + datePart;

                                String balanceStatus = "Tài khoản " + accountNumber + " -" + amount
                                        + " lúc " + timestamp + ", số dư còn " + String.format("%,d", newBalance);

                                Map<String, Object> historyData = new HashMap<>();
                                historyData.put("customerId", customerId);
                                historyData.put("date", timestamp);
                                historyData.put("balanceStatus", balanceStatus);
                                historyData.put("info", "Đặt vé máy bay lúc " + timePart);

                                historyRef.child(historyKey).setValue(historyData);
                            }

                            @Override public void onCancelled(@NonNull DatabaseError error) {}
                        });

                        // Bước 5: Cập nhật danh sách ghế đã chọn
                        DatabaseReference flightRef = db.child("flights").child(flightKey);
                        flightRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Flight f = snapshot.getValue(Flight.class);
                                if (f != null) {
                                    List<String> newAvailable = new ArrayList<>(f.getAvailable());
                                    List<String> newChosen = new ArrayList<>(f.getChosen() != null ? f.getChosen() : new ArrayList<>());

                                    for (String seat : selectedSeats) {
                                        newAvailable.remove(seat);
                                        if (!newChosen.contains(seat)) {
                                            newChosen.add(seat);
                                        }
                                    }

                                    Map<String, Object> updates = new HashMap<>();
                                    updates.put("available", newAvailable);
                                    updates.put("chosen", newChosen);
                                    flightRef.updateChildren(updates);
                                }
                            }

                            @Override public void onCancelled(@NonNull DatabaseError error) {}
                        });

                        Toast.makeText(SeatSelectionActivity.this, "Đặt vé thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }



}
