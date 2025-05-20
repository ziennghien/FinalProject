package com.end.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.*;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private EditText phoneInput;
    private EditText passWordInput;
    private Button loginBtn;
    private FirebaseAuth mAuth;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);

        phoneInput = findViewById(R.id.phone_input);
        passWordInput = findViewById(R.id.password_input);
        loginBtn = findViewById(R.id.login_btn);

        mAuth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(view -> {
            String phoneNumber = phoneInput.getText().toString().trim();
            String passWord = passWordInput.getText().toString().trim();

            if (phoneNumber.isEmpty() || passWord.isEmpty()) {
                Toast.makeText(MainActivity.this, "Vui lòng nhập đầy đủ thông tin.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra password trước khi gửi OTP
            DatabaseReference usersRef = FirebaseDatabase.getInstance(
                            "https://finalprojectandroid-ab72b-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .getReference("users");

            usersRef.orderByChild("phoneNumber").equalTo(phoneNumber)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                    String storedPassword = userSnapshot.child("password").getValue(String.class);
                                    if (storedPassword != null && storedPassword.equals(passWord)) {
                                        // Mật khẩu đúng → gửi OTP
                                        sendOtp(phoneNumber);
                                    } else {
                                        Toast.makeText(MainActivity.this, "Sai mật khẩu!", Toast.LENGTH_SHORT).show();
                                    }
                                    return; // Chỉ xét user đầu tiên
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Không tìm thấy số điện thoại!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(MainActivity.this, "Lỗi kết nối: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void sendOtp(String phoneNumber) {
        executorService.execute(() -> {
            runOnUiThread(() -> Toast.makeText(MainActivity.this, "Đang gửi OTP...", Toast.LENGTH_SHORT).show());

            PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                    .setPhoneNumber(phoneNumber)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(this)
                    .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        @Override
                        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                            runOnUiThread(() -> signInWithPhoneAuthCredential(credential));
                        }

                        @Override
                        public void onVerificationFailed(@NonNull FirebaseException e) {
                            Log.e(TAG, "OTP verification failed: " + e.getMessage());
                            runOnUiThread(() -> Toast.makeText(MainActivity.this,
                                    "Gửi OTP thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show());
                        }

                        @Override
                        public void onCodeSent(@NonNull String verificationId,
                                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
                            Log.d(TAG, "OTP sent successfully. Verification ID: " + verificationId);
                            runOnUiThread(() -> {
                                Intent intent = new Intent(MainActivity.this, OtpActivity.class);
                                intent.putExtra("verificationId", verificationId);
                                intent.putExtra("phoneNumber", phoneNumber);
                                startActivity(intent);
                            });
                        }
                    })
                    .build();

            PhoneAuthProvider.verifyPhoneNumber(options);
        });
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                        // TODO: Điều hướng sang màn hình chính
                    } else {
                        Toast.makeText(MainActivity.this, "Xác thực OTP thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
