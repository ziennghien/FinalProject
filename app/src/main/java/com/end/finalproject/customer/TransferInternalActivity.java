package com.end.finalproject.customer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.end.finalproject.R;
import com.google.firebase.database.*;
import com.google.firebase.functions.FirebaseFunctions;

import java.text.SimpleDateFormat;
import java.util.*;

public class TransferInternalActivity extends AppCompatActivity {

    private EditText edtReceiverAccount, edtReceiverName, edtAmount, edtNote;
    private Button btnContinue;
    private FirebaseFunctions mFunctions;
    private DatabaseReference dbRef;

    private String senderAccount, senderName, phoneNumber, userId;
    private double senderBalance = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_internal);

        // UI elements
        ImageView btnBack = findViewById(R.id.btn_back);
        edtReceiverAccount = findViewById(R.id.edtReceiverAccount);
        edtReceiverName = findViewById(R.id.edtReceiverName);
        edtAmount = findViewById(R.id.edtAmount);
        edtNote = findViewById(R.id.edtNote);
        btnContinue = findViewById(R.id.btnContinue);

        btnBack.setOnClickListener(v -> finish());

        // Firebase init
        mFunctions = FirebaseFunctions.getInstance();
        dbRef = FirebaseDatabase.getInstance("https://finalprojectandroid-ab72b-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        // Get data from Intent
        Intent intent = getIntent();
        senderAccount = intent.getStringExtra("accountNumber");
        senderName = intent.getStringExtra("name");
        phoneNumber = intent.getStringExtra("phoneNumber");
        userId = intent.getStringExtra("key");

        // Gợi ý ghi chú
        edtNote.setText(senderName.toUpperCase() + " chuyen tien");

        // Lấy số dư từ Firebase
        loadSenderBalance();

        // Auto lookup người nhận khi rời khỏi ô nhập
        edtReceiverAccount.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                lookupReceiver();
            }
        });

        // Bắt đầu chuyển tiền
        btnContinue.setOnClickListener(v -> {
            String receiverAccount = edtReceiverAccount.getText().toString().trim();
            String amountStr = edtAmount.getText().toString().trim();
            String note = edtNote.getText().toString().trim();

            if (receiverAccount.isEmpty() || amountStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Số tiền không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            if (amount > senderBalance) {
                Toast.makeText(this, "Số dư không đủ", Toast.LENGTH_SHORT).show();
                return;
            }

            if (amount <= 1000) {
                Toast.makeText(this, "Số tiền phải lớn hơn 1000", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gửi OTP
            Map<String, Object> data = new HashMap<>();
            data.put("accountNumber", senderAccount);
            data.put("phoneNumber", phoneNumber);

            mFunctions.getHttpsCallable("sendOtp")
                    .call(data)
                    .addOnSuccessListener(result -> {
                        Map response = (Map) result.getData();
                        String otp = (String) response.get("otp");

                        Log.d("OTP_Demo", "📲 OTP nhận được từ server: " + otp);
                        Toast.makeText(this, "Đã gửi mã OTP", Toast.LENGTH_SHORT).show();
                        Toast.makeText(this, "OTP (demo): " + otp, Toast.LENGTH_LONG).show();
                        showOtpDialog(receiverAccount, amount, note);
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Không gửi được OTP", Toast.LENGTH_SHORT).show());
        });
    }

    private void loadSenderBalance() {
        DatabaseReference balanceRef = dbRef.child("customers").child(userId).child("checkingAccount").child("balance");
        balanceRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                senderBalance = snapshot.getValue(Double.class);
            } else {
                Toast.makeText(this, "Không tìm thấy số dư", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Lỗi tải số dư: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void lookupReceiver() {
        String acc = edtReceiverAccount.getText().toString().trim();
        if (acc.isEmpty()) return;

        Map<String, Object> data = new HashMap<>();
        data.put("accountNumber", acc);

        mFunctions.getHttpsCallable("lookupInternalAccount")
                .call(data)
                .addOnSuccessListener(task -> {
                    Map result = (Map) task.getData();
                    Boolean success = (Boolean) result.get("success");
                    if (success != null && success) {
                        edtReceiverName.setText((String) result.get("accountName"));
                    } else {
                        edtReceiverName.setText("");
                        Toast.makeText(this, "Tài khoản không tồn tại", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    edtReceiverName.setText("");
                    Toast.makeText(this, "Lỗi kiểm tra tài khoản", Toast.LENGTH_SHORT).show();
                });
    }

    private void showOtpDialog(String receiverAccount, double amount, String note) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_otp_input, null);
        EditText edtOtp = dialogView.findViewById(R.id.edtOtpInput);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Xác minh OTP")
                .setView(dialogView)
                .setCancelable(false)
                .setPositiveButton("Xác nhận", null)
                .setNegativeButton("Huỷ", (d, which) -> Toast.makeText(this, "Đã huỷ giao dịch", Toast.LENGTH_SHORT).show())
                .create();

        dialog.setOnShowListener(d -> {
            Button btn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            btn.setOnClickListener(v -> {
                String otp = edtOtp.getText().toString().trim();
                if (otp.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập OTP", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String, Object> data = new HashMap<>();
                data.put("accountNumber", senderAccount);
                data.put("otp", otp);

                mFunctions.getHttpsCallable("verifyOtp")
                        .call(data)
                        .addOnSuccessListener(result -> {
                            boolean success = (Boolean) ((Map) result.getData()).get("success");
                            if (success) {
                                dialog.dismiss();
                                performTransfer(receiverAccount, amount, note);
                            } else {
                                Toast.makeText(this, "OTP không đúng", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> Toast.makeText(this, "Lỗi xác minh OTP", Toast.LENGTH_SHORT).show());
            });
        });

        dialog.show();
    }

    private void performTransfer(String receiverAccount, double amount, String note) {
        long timestamp = System.currentTimeMillis();
        String txnId = "TXN" + timestamp;

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang xử lý giao dịch...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        DatabaseReference db = FirebaseDatabase
                .getInstance("https://finalprojectandroid-ab72b-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference();

        DatabaseReference senderBalanceRef = db.child("customers").child(userId).child("checkingAccount").child("balance");

        senderBalanceRef.get().addOnSuccessListener(snapshot -> {
            double currentBalance = snapshot.getValue(Double.class);
            if (currentBalance < amount) {
                progressDialog.dismiss();
                Toast.makeText(this, "Số dư không đủ", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tìm người nhận
            db.child("customers")
                    .orderByChild("checkingAccount/accountNumber")
                    .equalTo(receiverAccount)
                    .get().addOnSuccessListener(receiverSnapshot -> {
                        if (!receiverSnapshot.exists()) {
                            progressDialog.dismiss();
                            Toast.makeText(this, "Không tìm thấy người nhận", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String receiverUid = receiverSnapshot.getChildren().iterator().next().getKey();

                        // 🚫 Không cho chuyển cho chính mình
                        if (receiverUid.equals(userId)) {
                            progressDialog.dismiss();
                            Toast.makeText(this, "Không thể chuyển tiền cho chính bạn", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Cập nhật số dư
                        DatabaseReference receiverBalanceRef = db.child("customers").child(receiverUid).child("checkingAccount").child("balance");

                        receiverBalanceRef.get().addOnSuccessListener(balanceSnap -> {
                            double recvBalance = balanceSnap.getValue(Double.class);

                            // Ghi dữ liệu
                            senderBalanceRef.setValue(currentBalance - amount);
                            receiverBalanceRef.setValue(recvBalance + amount);

                            Map<String, Object> txnData = new HashMap<>();
                            txnData.put("transactionId", txnId);
                            txnData.put("fromAccount", senderAccount);
                            txnData.put("toAccount", receiverAccount);
                            txnData.put("amount", amount);
                            txnData.put("note", note);
                            txnData.put("timestamp", timestamp);

                            db.child("transactions").child(senderAccount).child(txnId).setValue(txnData);
                            db.child("transactions").child(receiverAccount).child(txnId).setValue(txnData);

                            progressDialog.dismiss();

                            // Giao diện thành công
                            Intent intent = new Intent(this, TransactionSuccessActivity.class);
                            intent.putExtra("amount", String.format("%,.0f", amount));
                            intent.putExtra("time", new SimpleDateFormat("HH:mm 'Thứ' E dd/MM/yyyy", Locale.getDefault()).format(new Date(timestamp)));
                            intent.putExtra("receiverAccount", receiverAccount);
                            intent.putExtra("receiverName", edtReceiverName.getText().toString());
                            intent.putExtra("bankName", "KDK Bank");
                            intent.putExtra("note", note);
                            intent.putExtra("transactionId", txnId);
                            intent.putExtra("key", userId);
                            intent.putExtra("accountNumber", senderAccount);
                            intent.putExtra("name", senderName);
                            intent.putExtra("phoneNumber", phoneNumber);
                            startActivity(intent);
                            finish();
                        });
                    });
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(this, "Lỗi khi kiểm tra số dư", Toast.LENGTH_SHORT).show();
        });
    }

}
