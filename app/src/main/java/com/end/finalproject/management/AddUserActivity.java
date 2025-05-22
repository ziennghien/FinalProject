package com.end.finalproject.management;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.end.finalproject.R;
import com.google.firebase.database.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AddUserActivity extends AppCompatActivity {

    private EditText edtUserId, edtEmail, edtName, edtPhone;
    private Button btnSave;
    private static final String DB_URL =
            "https://finalprojectandroid-ab72b-default-rtdb.asia-southeast1.firebasedatabase.app";

    private DatabaseReference userRef;
    private final Random random = new Random();
    private String generatedId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_nhan_vien);

        edtUserId = findViewById(R.id.edtUserId);
        edtEmail = findViewById(R.id.edtEmail);
        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        btnSave = findViewById(R.id.btnSave);

        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        userRef = FirebaseDatabase.getInstance(DB_URL).getReference("users");

        // Tạo ID ngay khi mở form
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                generatedId = generateUniqueEmployeeId(snapshot);
                edtUserId.setText(generatedId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddUserActivity.this, "Không thể tạo ID mới", Toast.LENGTH_SHORT).show();
            }
        });

        btnSave.setOnClickListener(v -> validateAndSave());
    }

    private void validateAndSave() {
        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!phone.matches("\\+84\\d{9}")) {
            Toast.makeText(this, "Số điện thoại phải có định dạng +84XXXXXXXXX", Toast.LENGTH_LONG).show();
            return;
        }

        if (!email.endsWith("@gmail.com")) {
            Toast.makeText(this, "Email phải kết thúc bằng @gmail.com", Toast.LENGTH_LONG).show();
            return;
        }

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    String emailDb = userSnap.child("email").getValue(String.class);
                    String phoneDb = userSnap.child("phoneNumber").getValue(String.class);

                    if (email.equalsIgnoreCase(emailDb)) {
                        Toast.makeText(AddUserActivity.this, "Email đã tồn tại", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (phone.equals(phoneDb)) {
                        Toast.makeText(AddUserActivity.this, "Số điện thoại đã tồn tại", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                createNewEmployee(generatedId, name, phone, email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddUserActivity.this, "Lỗi truy vấn dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String generateUniqueEmployeeId(DataSnapshot snapshot) {
        String id;
        boolean isUnique;
        do {
            int rand = 10000 + random.nextInt(90000); // 5 chữ số
            id = "Emp" + rand;
            isUnique = true;
            for (DataSnapshot user : snapshot.getChildren()) {
                if (id.equals(user.getKey())) {
                    isUnique = false;
                    break;
                }
            }
        } while (!isUnique);
        return id;
    }

    private void createNewEmployee(String userId, String name, String phone, String email) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("phoneNumber", phone);
        userData.put("email", email);
        userData.put("role", "employee");
        userData.put("password", "123456"); // Mật khẩu mặc định

        userRef.child(userId).setValue(userData)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Thêm nhân viên thành công", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi lưu dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
