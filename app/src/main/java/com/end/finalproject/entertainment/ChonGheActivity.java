package com.end.finalproject.entertainment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ToggleButton;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.end.finalproject.R;
import com.end.finalproject.model.Trip;

import java.util.HashSet;
import java.util.Set;

public class ChonGheActivity extends AppCompatActivity {
    private GridLayout glSeats;
    private TextView tvRouteDate, tvTotal;
    private Button btnConfirm;
    private int pricePerSeat;
    private Set<Integer> selectedSeats;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chon_ghe);

        // Header back button
        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        tvRouteDate = findViewById(R.id.tvRouteDate);
        glSeats     = findViewById(R.id.glSeats);
        tvTotal     = findViewById(R.id.tvTotal);
        btnConfirm  = findViewById(R.id.btnConfirm);

        // Get trip data from intent
        String dep   = getIntent().getStringExtra("departure");
        String dest  = getIntent().getStringExtra("destination");
        String date  = getIntent().getStringExtra("dateDepart");
        String bus   = getIntent().getStringExtra("busCompany");
        pricePerSeat = getIntent().getIntExtra("price", 0);
        int seatCount= getIntent().getIntExtra("seat", 0);

        // Show route/date header
        tvRouteDate.setText(dep + " - " + dest + " (" + date + ")");

        // Initialize selection
        selectedSeats = new HashSet<>();
        updateTotal();

        // Dynamically add seat buttons
        for (int i = 1; i <= seatCount; i++) {
            ToggleButton tb = new ToggleButton(this);
            tb.setText(String.valueOf(i));
            tb.setTextOn(String.valueOf(i));
            tb.setTextOff(String.valueOf(i));
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = 0;
            lp.height = GridLayout.LayoutParams.WRAP_CONTENT;
            lp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            lp.setMargins(8, 8, 8, 8);
            tb.setLayoutParams(lp);

            tb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int seatNum = Integer.parseInt(buttonView.getText().toString());
                if (isChecked) selectedSeats.add(seatNum);
                else selectedSeats.remove(seatNum);
                updateTotal();
            });
            glSeats.addView(tb);
        }

        // Confirm button
        btnConfirm.setOnClickListener(v -> {
            // handle booking with selectedSeats
            // for now just finish
            finish();
        });
    }

    private void updateTotal() {
        int total = selectedSeats.size() * pricePerSeat;
        tvTotal.setText("Tổng: " + total + " VND");
    }
}
