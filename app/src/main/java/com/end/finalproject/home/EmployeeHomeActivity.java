package com.end.finalproject.home;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.end.finalproject.R;

public class EmployeeHomeActivity extends AppCompatActivity {

    private TextView tvWelcome, tvEmail, tvPhone, tvUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_employee);

        tvWelcome = findViewById(R.id.tv_welcome);
        tvEmail = findViewById(R.id.tv_email);
        tvPhone = findViewById(R.id.tv_phone);
        tvUserId = findViewById(R.id.tv_user_id);

        // Lấy dữ liệu từ Intent
        String email = getIntent().getStringExtra("email");
        String phone = getIntent().getStringExtra("phoneNumber");
        String userId = getIntent().getStringExtra("key");

        // Hiển thị thông tin
        tvWelcome.setText("Chào mừng nhân viên!");
        tvEmail.setText("Email: " + email);
        tvPhone.setText("SĐT: " + phone);
        tvUserId.setText("ID: " + userId);
    }
}
