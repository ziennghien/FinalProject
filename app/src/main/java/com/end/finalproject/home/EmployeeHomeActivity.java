package com.end.finalproject.home;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.end.finalproject.R;
import com.end.finalproject.management.DanhSachKhachHangActivity;

public class EmployeeHomeActivity extends AppCompatActivity {

    private TextView tvWelcome, tvEmail, tvPhone, tvUserId;
    private CardView cardAccountDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_employee);

        tvWelcome = findViewById(R.id.tv_welcome);
        tvEmail = findViewById(R.id.tv_email);
        tvPhone = findViewById(R.id.tv_phone);
        tvUserId = findViewById(R.id.tv_user_id);
        cardAccountDetail = findViewById(R.id.cardKhachHang);

        // Lấy dữ liệu từ Intent
        String name = getIntent().getStringExtra("name");
        String email = getIntent().getStringExtra("email");
        String phone = getIntent().getStringExtra("phoneNumber");
        String userId = getIntent().getStringExtra("key");

        // Hiển thị thông tin
        tvWelcome.setText("Chào mừng " + name + "!");
        tvEmail.setText(email);
        tvPhone.setText(phone);
        tvUserId.setText(userId);

        // Xử lý sự kiện click vào card để mở danh sách khách hàng
        cardAccountDetail.setOnClickListener(view -> {
            Intent intent = new Intent(EmployeeHomeActivity.this, DanhSachKhachHangActivity.class);
            startActivity(intent);
        });
    }
}
