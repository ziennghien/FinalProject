package com.end.finalproject.customer;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.end.finalproject.R;
import com.end.finalproject.adapter.TabPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class AccountDetailActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private TabPagerAdapter tabPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String uid = getIntent().getStringExtra("key");
        String email = getIntent().getStringExtra("email");
        String phoneNumber = getIntent().getStringExtra("phoneNumber");
        if (uid == null) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_detail);

        // Nút back
        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        // Khởi tạo TabLayout + ViewPager2
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        // Khởi tạo Adapter và gắn vào ViewPager2
        tabPagerAdapter = new TabPagerAdapter(this, uid, email, phoneNumber);
        viewPager.setAdapter(tabPagerAdapter);

        // Kết nối TabLayout với ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Tài khoản");
                    break;
                case 1:
                    tab.setText("Tài khoản thanh toán");
                    break;
                case 2:
                    tab.setText("Tài khoản tiết kiệm");
                    break;
                case 3:
                    tab.setText("Tài khoản thế chấp");
                    break;
            }
        }).attach();
    }
}
