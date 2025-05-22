package com.end.finalproject.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.end.finalproject.MainActivity;
import com.end.finalproject.R;
import com.end.finalproject.customer.AccountDetailActivity;
import com.end.finalproject.customer.HistoryActivity;
import com.end.finalproject.customer.TransferActivity;
import com.end.finalproject.entertainment.EntertainmentListActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;

public class CustomerHomeActivity extends AppCompatActivity {

    private TextView welcomeText, accountNumber, balance;
    private CardView accountCard;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_customer);

        ImageButton btnLogout = findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(CustomerHomeActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        welcomeText   = findViewById(R.id.welcome_text);
        accountNumber = findViewById(R.id.account_number);
        balance       = findViewById(R.id.balance_amount);
        accountCard   = findViewById(R.id.account_card);
        bottomNav     = findViewById(R.id.bottom_navigation);

        // Data from Intent
        String ten         = getIntent().getStringExtra("name");
        String stk         = getIntent().getStringExtra("accountNumber");
        String soDu        = getIntent().getStringExtra("balance");
        String userId      = getIntent().getStringExtra("key");
        String email       = getIntent().getStringExtra("email");
        String phoneNumber = getIntent().getStringExtra("phoneNumber");

        // 👉 Set dữ liệu vào View
        welcomeText.setText(ten); // Ví dụ: Nguyễn Văn A
        accountNumber.setText("Tài khoản: " + stk); // Ví dụ: 123456789
        balance.setText("Số dư: " + soDu + " VND"); // Ví dụ: 20,000,000 VND
        // Load animation
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        accountCard.startAnimation(fadeIn);
        welcomeText.startAnimation(fadeIn);

        // Transfer button
        LinearLayout transferBtn = findViewById(R.id.layoutTransfer);
        transferBtn.setOnClickListener(view -> {
            Intent intent = new Intent(CustomerHomeActivity.this, TransferActivity.class);
            intent.putExtra("key", userId);
            intent.putExtra("accountNumber", stk);
            intent.putExtra("balance", soDu);
            intent.putExtra("name", ten);
            intent.putExtra("phoneNumber", phoneNumber);
            startActivity(intent);

        });

        // Account detail card
        MaterialCardView cardAccountDetail = findViewById(R.id.card_account_detail);
        cardAccountDetail.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerHomeActivity.this, AccountDetailActivity.class);
            intent.putExtra("key", userId);
            intent.putExtra("email", email);
            intent.putExtra("phoneNumber", phoneNumber);
            startActivity(intent);
        });

        // History
        MaterialCardView cardHistory = findViewById(R.id.card_transaction_history);
        cardHistory.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerHomeActivity.this, HistoryActivity.class);
            intent.putExtra("key", userId);
            intent.putExtra("email", email);
            intent.putExtra("phoneNumber", phoneNumber);
            startActivity(intent);
        });

        // Entertainment section
        LinearLayout entertainmentLayout = findViewById(R.id.Entertainment);
        entertainmentLayout.setOnClickListener(v -> {
            Intent entIntent = new Intent(CustomerHomeActivity.this, EntertainmentListActivity.class);
            entIntent.putExtra("key", userId);
            entIntent.putExtra("email", email);
            entIntent.putExtra("phoneNumber", phoneNumber);
            startActivity(entIntent);
        });
    }
}
