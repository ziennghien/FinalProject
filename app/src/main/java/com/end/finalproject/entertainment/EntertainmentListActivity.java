package com.end.finalproject.entertainment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.end.finalproject.R;
import com.end.finalproject.flight.SearchFlightActivity;
import com.google.android.material.card.MaterialCardView;

public class EntertainmentListActivity extends AppCompatActivity {
    private String userId;
    private String email;
    private String phoneNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager);


        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());


        // Nút back
        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        // Nhận thông tin người dùng
        Intent intent = getIntent();
        userId = intent.getStringExtra("key");
        email = intent.getStringExtra("email");
        phoneNumber = intent.getStringExtra("phoneNumber");

        // 👉 Nút xem vé đã đặt
        findViewById(R.id.btn_myticket).setOnClickListener(v -> {
            Intent myTicketIntent = new Intent(EntertainmentListActivity.this, MyTicketActivity.class);
            myTicketIntent.putExtra("key", userId);  // Gửi customerId
            startActivity(myTicketIntent);
        });

        // Đặt vé xe
        CardView cardBus = findViewById(R.id.cardBus);
        cardBus.setOnClickListener(v -> toDatVeXe());

        // Đặt vé máy bay
        CardView cardFlight = findViewById(R.id.cardFlight);
        cardFlight.setOnClickListener(v -> toDatVeMayBay());
    }


    private void toDatVeXe() {
        new AlertDialog.Builder(this)
                .setMessage("Đây là dịch vụ hợp tác giữ KDK và VNPAY. Mọi vấn đề liên quan đến Đặt Vé Xe sẽ do VNPAY chịu trách nhiệm")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Khởi tạo Intent và truyền tiếp các thông tin
                        Intent intent = new Intent(EntertainmentListActivity.this, DatVeXeActivity.class);
                        intent.putExtra("key", userId);
                        intent.putExtra("email", email);
                        intent.putExtra("phoneNumber", phoneNumber);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(true)
                .show();
    }

    private void toDatVeMayBay() {
        new AlertDialog.Builder(this)
                .setMessage("Đây là dịch vụ hợp tác giữ KDK và VNPAY. Mọi vấn đề liên quan đến Đặt Vé May Bay sẽ do VNPAY chịu trách nhiệm")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Khởi tạo Intent và truyền tiếp các thông tin
                        Intent intent = new Intent(EntertainmentListActivity.this, SearchFlightActivity.class);
                        intent.putExtra("key", userId);
                        intent.putExtra("email", email);
                        intent.putExtra("phoneNumber", phoneNumber);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(true)
                .show();
    }
}
