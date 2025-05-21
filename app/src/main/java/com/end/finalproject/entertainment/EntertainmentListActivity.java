package com.end.finalproject.entertainment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.end.finalproject.R;
import com.google.android.material.card.MaterialCardView;

public class EntertainmentListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager);

        CardView cardBus = findViewById(R.id.cardBus);
        cardBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showVNPAYDialog();
            }
        });
    }

    private void showVNPAYDialog() {
        new AlertDialog.Builder(this)
                .setMessage("Đây là dịch vụ hợp tác giữ KDK và VNPAY. Mọi vấn đề liên quan đến Đặt Vé Xe sẽ do VNPAY chịu trách nhiệm")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(EntertainmentListActivity.this, DatVeXeActivity.class);
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
