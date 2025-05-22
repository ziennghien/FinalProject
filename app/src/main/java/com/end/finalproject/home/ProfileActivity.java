package com.end.finalproject.home;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.end.finalproject.R;
import com.google.firebase.database.*;

public class ProfileActivity extends AppCompatActivity {

    private EditText edtEmail, edtName, edtPhone, edtPassword, edtRole, edtUserId;
    private Button btnSave;
    private String userId;

    private static final String DB_URL =
            "https://finalprojectandroid-ab72b-default-rtdb.asia-southeast1.firebasedatabase.app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Ánh xạ view
        edtUserId = findViewById(R.id.edtUserId);
        edtEmail = findViewById(R.id.edtEmail);
        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);
        edtRole = findViewById(R.id.edtRole);
        btnSave = findViewById(R.id.btnSave);

        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        // Nhận dữ liệu từ Intent
        userId = getIntent().getStringExtra("userId");
        edtEmail.setText(getIntent().getStringExtra("email"));
        edtName.setText(getIntent().getStringExtra("name"));
        edtPhone.setText(getIntent().getStringExtra("phoneNumber"));
        edtPassword.setText(getIntent().getStringExtra("password"));
        edtRole.setText(getIntent().getStringExtra("role"));
        edtRole.setEnabled(false); // Không cho sửa quyền

        edtUserId.setText(userId);
        edtUserId.setEnabled(false);

        // Sự kiện lưu
        btnSave.setOnClickListener(v -> checkValidationAndSave());
    }

    private void checkValidationAndSave() {
        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || password.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra định dạng số điện thoại
        if (!phone.matches("\\+84\\d{9}")) {
            Toast.makeText(this, "Số điện thoại phải theo định dạng +84xxxxxxxxx", Toast.LENGTH_LONG).show();
            return;
        }

        // Kiểm tra định dạng email
        if (!email.endsWith("@gmail.com") || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email phải đúng định dạng và kết thúc bằng @gmail.com", Toast.LENGTH_LONG).show();
            return;
        }

        DatabaseReference usersRef = FirebaseDatabase.getInstance(DB_URL).getReference("users");

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isDuplicatePhone = false;
                boolean isDuplicateEmail = false;

                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    String key = userSnap.getKey();
                    String phoneInDb = userSnap.child("phoneNumber").getValue(String.class);
                    String emailInDb = userSnap.child("email").getValue(String.class);

                    if (phone.equals(phoneInDb) && !userId.equals(key)) {
                        isDuplicatePhone = true;
                    }

                    if (email.equalsIgnoreCase(emailInDb) && !userId.equals(key)) {
                        isDuplicateEmail = true;
                    }
                }

                if (isDuplicatePhone) {
                    Toast.makeText(ProfileActivity.this, "Số điện thoại đã được sử dụng!", Toast.LENGTH_LONG).show();
                } else if (isDuplicateEmail) {
                    Toast.makeText(ProfileActivity.this, "Email đã được sử dụng!", Toast.LENGTH_LONG).show();
                } else {
                    // Cập nhật thông tin
                    DatabaseReference currentUserRef = usersRef.child(userId);
                    currentUserRef.child("name").setValue(name);
                    currentUserRef.child("phoneNumber").setValue(phone);
                    currentUserRef.child("password").setValue(password);
                    currentUserRef.child("email").setValue(email);

                    Toast.makeText(ProfileActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Lỗi kết nối dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
