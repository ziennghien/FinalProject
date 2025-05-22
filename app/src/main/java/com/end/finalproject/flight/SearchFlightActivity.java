// SearchFlightActivity.java
package com.end.finalproject.flight;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.end.finalproject.R;

import java.util.Calendar;

public class SearchFlightActivity extends AppCompatActivity {
    private EditText etDeparture, etDestination, etDate, etCompany;
    private Button btnSearch;
    private String userId;  // thêm trường này

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tim_ve_may_bay);

        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        // Nhận userId (key) từ EntertainmentListActivity
        userId = getIntent().getStringExtra("key");

        etDeparture   = findViewById(R.id.etDeparture);
        etDestination = findViewById(R.id.etDestination);
        etDate        = findViewById(R.id.etDateDepart);
        etCompany     = findViewById(R.id.etCompanyName);
        btnSearch     = findViewById(R.id.btnSearch);

        etDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this,
                    (DatePicker view, int year, int month, int dayOfMonth) -> {
                        String formatted = String.format("%02d/%02d/%04d",
                                dayOfMonth, month + 1, year);
                        etDate.setText(formatted);
                    },
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        btnSearch.setOnClickListener(v -> {
            Intent intent = new Intent(
                    SearchFlightActivity.this,
                    FlightListActivity.class
            );
            // truyền các trường tìm kiếm
            intent.putExtra("departure",  etDeparture.getText().toString().trim());
            intent.putExtra("destination", etDestination.getText().toString().trim());
            intent.putExtra("date",        etDate.getText().toString().trim());
            intent.putExtra("company",     etCompany.getText().toString().trim());
            // truyền userId tiếp cho FlightListActivity
            intent.putExtra("customerId",  userId);
            startActivity(intent);
        });
    }
}
