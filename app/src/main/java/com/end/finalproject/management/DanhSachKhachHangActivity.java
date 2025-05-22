package com.end.finalproject.management;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.text.TextWatcher;
import android.text.Editable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.end.finalproject.R;
import com.end.finalproject.adapter.CustomerAdapter;
import com.end.finalproject.home.EmployeeHomeActivity;
import com.end.finalproject.model.Customer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DanhSachKhachHangActivity extends AppCompatActivity {

    private EditText edtSearchKhachHang;
    private RecyclerView recyclerView;
    private CustomerAdapter adapter;
    private List<Customer> customerList, allCustomers;
    private DatabaseReference customerRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danh_sach_khach_hang);

        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        // 1. Khởi tạo view và list
        edtSearchKhachHang = findViewById(R.id.edtSearchKhachHang);
        recyclerView = findViewById(R.id.rvDanhSachKhachHang);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        allCustomers = new ArrayList<>();
        customerList = new ArrayList<>();
        // thay adapter init:
        adapter = new CustomerAdapter(customerList, customer -> {
            // Khi click, đưa sang AccountDetailManagementActivity
            Intent it = new Intent(this, AccountDetailManagementActivity.class);
            it.putExtra("key", customer.getId());
            startActivity(it);
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setAdapter(adapter);

        // 2. Bắt sự kiện search
        edtSearchKhachHang.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                filterCustomers(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // 3. Khởi tạo DatabaseReference với URL đúng region
        customerRef = FirebaseDatabase.getInstance(
                "https://finalprojectandroid-ab72b-default-rtdb.asia-southeast1.firebasedatabase.app"
        ).getReference("customers");

        // 4. Tải dữ liệu
        loadCustomers();

        FloatingActionButton faAddButton = findViewById(R.id.faAddButton); // ID của FAB

        faAddButton.setOnClickListener(v -> {
            Intent intent = new Intent(DanhSachKhachHangActivity.this, AddCustomerActivity.class);
            startActivity(intent);
        });
    }

    private void loadCustomers() {
        customerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allCustomers.clear();
                customerList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Customer c = snap.getValue(Customer.class);
                        c.setId(snap.getKey());
                        allCustomers.add(c);
                        customerList.add(c);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DanhSachKhachHangActivity.this,
                        "Lỗi tải dữ liệu: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterCustomers(String query) {
        String lower = query.toLowerCase().trim();
        customerList.clear();

        if (lower.isEmpty()) {
            customerList.addAll(allCustomers);
        } else {
            for (Customer c : allCustomers) {
                boolean match =
                        c.getId().toLowerCase().contains(lower) ||
                                c.getName().toLowerCase().contains(lower);

                // check nested accountNumbers
                if (!match && c.getCheckingAccount() != null &&
                        c.getCheckingAccount().getAccountNumber().toLowerCase().contains(lower)) {
                    match = true;
                }
                if (!match && c.getSavingAccount() != null &&
                        c.getSavingAccount().getAccountNumber().toLowerCase().contains(lower)) {
                    match = true;
                }
                if (!match && c.getMortgageAccount() != null &&
                        c.getMortgageAccount().getAccountNumber().toLowerCase().contains(lower)) {
                    match = true;
                }

                if (match) {
                    customerList.add(c);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

}
