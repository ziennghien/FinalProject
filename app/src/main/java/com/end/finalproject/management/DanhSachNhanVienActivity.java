// DanhSachNhanVienActivity.java
package com.end.finalproject.management;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.end.finalproject.R;
import com.end.finalproject.adapter.UserAdapter;
import com.end.finalproject.home.ProfileActivity;
import com.end.finalproject.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class DanhSachNhanVienActivity extends AppCompatActivity {
    private EditText edtSearchNhanVien;
    private RecyclerView rvDanhSachNhanVien;
    private UserAdapter adapter;
    private List<User> userList, allUsers;
    private static final String DB_URL =
            "https://finalprojectandroid-ab72b-default-rtdb.asia-southeast1.firebasedatabase.app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danh_sach_nhan_vien);

        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        edtSearchNhanVien = findViewById(R.id.edtSearchNhanVien);
        rvDanhSachNhanVien = findViewById(R.id.rvDanhSachNhanVien);
        rvDanhSachNhanVien.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        allUsers = new ArrayList<>();

        adapter = new UserAdapter(userList, user -> {
            Intent intent = new Intent(DanhSachNhanVienActivity.this, ProfileActivity.class);
            intent.putExtra("userId", user.getPhoneNumber());
            intent.putExtra("email", user.getEmail());
            intent.putExtra("name", user.getName());
            intent.putExtra("phoneNumber", user.getPhoneNumber());
            intent.putExtra("password", user.getPassword());
            intent.putExtra("role", user.getRole());
            startActivity(intent);
        });
        rvDanhSachNhanVien.setAdapter(adapter);

        edtSearchNhanVien.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                filterUsers(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        loadEmployees();

        FloatingActionButton faAddButton = findViewById(R.id.faAddButton);
        faAddButton.setOnClickListener(v -> {
            Intent intent = new Intent(DanhSachNhanVienActivity.this, AddUserActivity.class);
            startActivity(intent);
        });
    }

    private void loadEmployees() {
        DatabaseReference userRef = FirebaseDatabase.getInstance(DB_URL).getReference("users");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                allUsers.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    User user = child.getValue(User.class);
                    if (user != null && "employee".equalsIgnoreCase(user.getRole())) {
                        allUsers.add(user);
                        userList.add(user);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DanhSachNhanVienActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterUsers(String keyword) {
        String lower = keyword.toLowerCase();
        userList.clear();

        if (lower.isEmpty()) {
            userList.addAll(allUsers);
        } else {
            for (User user : allUsers) {
                if ((user.getName() != null && user.getName().toLowerCase().contains(lower)) ||
                        (user.getPhoneNumber() != null && user.getPhoneNumber().toLowerCase().contains(lower))) {
                    userList.add(user);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }
}