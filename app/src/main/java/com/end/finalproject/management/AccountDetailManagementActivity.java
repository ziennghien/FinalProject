package com.end.finalproject.management;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.end.finalproject.R;
import com.end.finalproject.adapter.TabPagerManagementAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class AccountDetailManagementActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle saved) {
        super.onCreate(saved);
        setContentView(R.layout.activity_account_detail);

        String uid         = getIntent().getStringExtra("key");
        String email       = getIntent().getStringExtra("email");
        String phoneNumber = getIntent().getStringExtra("phoneNumber");
        if (uid == null) {
            Toast.makeText(this, "Không tìm thấy thông tin", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Nút back
        ((ImageView)findViewById(R.id.btn_back))
                .setOnClickListener(v -> finish());

        // Tab + ViewPager
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        TabPagerManagementAdapter adapter =
                new TabPagerManagementAdapter(this, uid, email, phoneNumber);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, pos) -> {
            switch (pos) {
                case 0: tab.setText("Tài khoản"); break;
                case 1: tab.setText("Thanh toán"); break;
                case 2: tab.setText("Tiết kiệm"); break;
                case 3: tab.setText("Thế chấp"); break;
            }
        }).attach();
    }
}
