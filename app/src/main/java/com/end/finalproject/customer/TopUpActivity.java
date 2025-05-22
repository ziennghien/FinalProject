package com.end.finalproject.customer;

import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.end.finalproject.R;
import com.google.firebase.database.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class TopUpActivity extends AppCompatActivity {

    private EditText edtPhone, edtSoTien;
    private TextView tvGoc, tvChietKhau, tvTong, btnThanhToan;
    private String userId, accountNumber;
    private long selectedAmount = 0;
    private final double DISCOUNT = 0.025;
    private final String DB_URL = "https://finalprojectandroid-ab72b-default-rtdb.asia-southeast1.firebasedatabase.app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nap_tien_dien_thoai);

        // Nút back
        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        edtPhone     = findViewById(R.id.edtSoDienThoai);
        edtSoTien    = findViewById(R.id.edtSoTien);
        tvGoc        = findViewById(R.id.tvThanhToanGoc);
        tvChietKhau  = findViewById(R.id.tvChietKhau);
        tvTong       = findViewById(R.id.tvTongThanhToan);
        btnThanhToan = findViewById(R.id.btnThanhToan);

        userId = getIntent().getStringExtra("key");
        accountNumber = getIntent().getStringExtra("accountNumber");
        String userPhone = getIntent().getStringExtra("phoneNumber");  // lấy số điện thoại người dùng
        edtPhone.setText(userPhone); // hiển thị sẵn vào ô nhập


        setDenominationButtons();


        btnThanhToan.setOnClickListener(v -> {
            String phone = edtPhone.getText().toString().trim();

            if (!phone.matches("^\\+84\\d{9}$")) {
                Toast.makeText(this, "Số điện thoại không hợp lệ! Ví dụ: +84981234567", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedAmount == 0) {
                Toast.makeText(this, "Vui lòng chọn mệnh giá", Toast.LENGTH_SHORT).show();
                return;
            }

            long discount = (long) (selectedAmount * DISCOUNT);
            long pay = selectedAmount - discount;

            String message = "Xác nhận nạp " + formatVND(selectedAmount) +
                    "\nChiết khấu: " + formatVND(discount) +
                    "\nTổng thanh toán: " + formatVND(pay) +
                    "\nSố điện thoại: " + phone;

            new android.app.AlertDialog.Builder(this)
                    .setTitle("Xác nhận thanh toán")
                    .setMessage(message)
                    .setPositiveButton("Xác nhận", (dialog, which) -> processTopUp(phone))
                    .setNegativeButton("Huỷ", null)
                    .show();
        });

    }

    private String formatVND(long amount) {
        return NumberFormat.getInstance().format(amount) + "đ";
    }


    private void setDenominationButtons() {
        int[] btnIds = {R.id.btn10k, R.id.btn20k, R.id.btn50k, R.id.btn100k, R.id.btn200k, R.id.btn500k};
        int[] values = {10000, 20000, 50000, 100000, 200000, 500000};

        for (int i = 0; i < btnIds.length; i++) {
            int val = values[i];
            findViewById(btnIds[i]).setOnClickListener(v -> {
                selectedAmount = val;
                updateCalculation();
            });
        }
    }

    private void updateCalculation() {
        long discount = (long)(selectedAmount * DISCOUNT);
        long finalAmount = selectedAmount - discount;
        NumberFormat nf = NumberFormat.getInstance();

        edtSoTien.setText(nf.format(selectedAmount));
        tvGoc.setText(nf.format(selectedAmount) + "đ");
        tvChietKhau.setText(nf.format(discount) + "đ");
        tvTong.setText(nf.format(finalAmount) + "đ");
        btnThanhToan.setText("Thanh toán " + nf.format(finalAmount) + "đ");
    }

    private void processTopUp(String phone) {
        DatabaseReference db = FirebaseDatabase.getInstance(DB_URL).getReference();

        db.child("customers").child(userId).child("checkingAccount")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Long balance = snapshot.child("balance").getValue(Long.class);
                        String accNum = snapshot.child("accountNumber").getValue(String.class);
                        if (balance == null || accNum == null) return;

                        long discount = (long)(selectedAmount * DISCOUNT);
                        long pay = selectedAmount - discount;

                        if (balance < pay) {
                            Toast.makeText(TopUpActivity.this, "Số dư không đủ", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        db.child("customers").child(userId).child("checkingAccount").child("balance")
                                .setValue(balance - pay);

                        saveHistory(db, accNum, balance - pay, phone, selectedAmount);
                    }

                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void saveHistory(DatabaseReference db, String accNum, long newBalance, String phone, long amount) {
        db.child("historys").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                String historyKey = "history" + (snapshot.getChildrenCount() + 1);
                Date now = new Date();
                String datePart = new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(now);
                String timePart = new SimpleDateFormat("HH:mm", Locale.US).format(now);

                Map<String, Object> data = new HashMap<>();
                data.put("customerId", userId);
                data.put("date", datePart);
                data.put("info", accNum + " đã nạp cho " + phone + " " + amount + "đ lúc " + timePart);
                data.put("balanceStatus", "Tài khoản " + accNum + " -" + (amount - (long)(amount * DISCOUNT)) +
                        " lúc " + datePart + " - " + timePart + ", số dư còn " + newBalance);


                db.child("historys").child(historyKey).setValue(data);
                Toast.makeText(TopUpActivity.this, "Nạp tiền thành công", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
