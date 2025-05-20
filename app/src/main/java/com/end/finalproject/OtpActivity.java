package com.end.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import com.end.finalproject.home.CustomerHomeActivity;
import com.end.finalproject.home.EmployeeHomeActivity;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.*;

import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {

    private EditText otpInput;
    private Button verifyBtn;
    private Button resendBtn;
    private String verificationId;
    private String phoneNumber;
    private FirebaseAuth mAuth;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        otpInput = findViewById(R.id.otp_input);
        verifyBtn = findViewById(R.id.verify_btn);
        resendBtn = findViewById(R.id.resend_btn);

        mAuth = FirebaseAuth.getInstance();
        verificationId = getIntent().getStringExtra("verificationId");
        phoneNumber = getIntent().getStringExtra("phoneNumber");

        startResendTimer(); // Bắt đầu đếm ngược

        verifyBtn.setOnClickListener(v -> {
            String code = otpInput.getText().toString().trim();
            if (code.isEmpty()) {
                Toast.makeText(OtpActivity.this, "Vui lòng nhập mã OTP", Toast.LENGTH_SHORT).show();
                return;
            }
            verifyOtp(code);
        });

        resendBtn.setOnClickListener(v -> {
            resendVerificationCode(phoneNumber);
            startResendTimer();
        });
    }

    private void verifyOtp(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(OtpActivity.this, "Xác thực OTP thành công!", Toast.LENGTH_SHORT).show();

                        // Truy vấn users để lấy role và userId
                        DatabaseReference usersRef = FirebaseDatabase.getInstance("https://finalprojectandroid-ab72b-default-rtdb.asia-southeast1.firebasedatabase.app")
                                .getReference("users");

                        usersRef.orderByChild("phoneNumber").equalTo(phoneNumber)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                                String role = userSnapshot.child("role").getValue(String.class);
                                                String email = userSnapshot.child("email").getValue(String.class);
                                                String userId = userSnapshot.getKey();

                                                if ("customer".equalsIgnoreCase(role)) {
                                                    // Lấy thông tin từ bảng customers/{userId}
                                                    DatabaseReference customerRef = FirebaseDatabase.getInstance("https://finalprojectandroid-ab72b-default-rtdb.asia-southeast1.firebasedatabase.app")
                                                            .getReference("customers")
                                                            .child(userId);

                                                    customerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot customerSnapshot) {
                                                            String name = String.valueOf(customerSnapshot.child("name").getValue());
                                                            String accountNumber = String.valueOf(customerSnapshot.child("checkingAccount").child("accountNumber").getValue());
                                                            String balance = String.valueOf(customerSnapshot.child("checkingAccount").child("balance").getValue());

                                                            Intent intent = new Intent(OtpActivity.this, CustomerHomeActivity.class);
                                                            intent.putExtra("key", userId);
                                                            intent.putExtra("email", email);
                                                            intent.putExtra("phoneNumber", phoneNumber);
                                                            intent.putExtra("name", name);
                                                            intent.putExtra("accountNumber", accountNumber);
                                                            intent.putExtra("balance", balance);
                                                            startActivity(intent);
                                                            finish();
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError error) {
                                                            Toast.makeText(OtpActivity.this, "Lỗi khi đọc bảng customers: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                                } else if ("employee".equalsIgnoreCase(role)) {
                                                    Intent intent = new Intent(OtpActivity.this, EmployeeHomeActivity.class);
                                                    intent.putExtra("key", userId);
                                                    intent.putExtra("email", email);
                                                    intent.putExtra("phoneNumber", phoneNumber);
                                                    startActivity(intent);
                                                    finish();

                                                } else {
                                                    Toast.makeText(OtpActivity.this, "Không hỗ trợ vai trò: " + role, Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        } else {
                                            Toast.makeText(OtpActivity.this, "Không tìm thấy người dùng tương ứng", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError error) {
                                        Toast.makeText(OtpActivity.this, "Lỗi đọc database: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                    } else {
                        Toast.makeText(OtpActivity.this, "Xác thực OTP thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void startResendTimer() {
        resendBtn.setEnabled(false);
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                resendBtn.setText("Gửi lại mã (" + millisUntilFinished / 1000 + "s)");
            }

            @Override
            public void onFinish() {
                resendBtn.setText("Gửi lại mã");
                resendBtn.setEnabled(true);
            }
        }.start();
    }

    private void resendVerificationCode(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(PhoneAuthCredential credential) {
                                signInWithPhoneAuthCredential(credential);
                            }

                            @Override
                            public void onVerificationFailed(FirebaseException e) {
                                Toast.makeText(OtpActivity.this, "Gửi lại mã thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(String newVerificationId, PhoneAuthProvider.ForceResendingToken token) {
                                verificationId = newVerificationId;
                                Toast.makeText(OtpActivity.this, "Mã OTP mới đã được gửi", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
}
