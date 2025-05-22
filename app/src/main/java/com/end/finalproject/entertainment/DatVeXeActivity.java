package com.end.finalproject.entertainment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.end.finalproject.R;
import com.google.android.material.tabs.TabLayout;

import java.util.Calendar;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DatVeXeActivity extends AppCompatActivity {
    private EditText etDeparture, etDestination;
    private EditText etDateDepart, etDateReturn;
    private EditText etBusCompany;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dat_ve_xe);

        // Header back button
        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        // Fields
        etDeparture    = findViewById(R.id.et_departure);
        etDestination  = findViewById(R.id.et_destination);
        etDateDepart   = findViewById(R.id.et_date_depart);
        etDateReturn   = findViewById(R.id.et_date_return);
        etBusCompany   = findViewById(R.id.et_bus_company);
        tabLayout      = findViewById(R.id.tabLayout);
        Button btnSearch = findViewById(R.id.btn_search);

        // DatePicker on click
        etDateDepart.setOnClickListener(v -> showDatePicker(etDateDepart));
        etDateReturn.setOnClickListener(v -> showDatePicker(etDateReturn));

        // Tab selection: hide return date on one-way
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                if (pos == 0) { // Một chiều
                    etDateReturn.setVisibility(View.GONE);
                } else {     // Khứ hồi
                    etDateReturn.setVisibility(View.VISIBLE);
                }
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Initialize tab state
        if (tabLayout.getSelectedTabPosition() == 0) {
            etDateReturn.setVisibility(View.GONE);
        }

        // Search button: chuyển sang danh sách vé xe
        btnSearch.setOnClickListener(v -> {
            Intent intent = new Intent(DatVeXeActivity.this, DanhSachVeXeActivity.class);
            intent.putExtra("departure", etDeparture.getText().toString().trim());
            intent.putExtra("destination", etDestination.getText().toString().trim());
            intent.putExtra("busCompany", etBusCompany.getText().toString().trim());
            intent.putExtra("dateDepart", etDateDepart.getText().toString().trim());
            startActivity(intent);
        });
    }

    private void showDatePicker(final EditText target) {
        Calendar calendar = Calendar.getInstance();
        int y = calendar.get(Calendar.YEAR);
        int m = calendar.get(Calendar.MONTH);
        int d = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar sel = Calendar.getInstance();
                        sel.set(year, month, dayOfMonth);
                        Date date = sel.getTime();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        target.setText(sdf.format(date));
                    }
                }, y, m, d);
        dpd.show();
    }
}
