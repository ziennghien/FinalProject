package com.end.finalproject.customer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.end.finalproject.R;
import com.end.finalproject.home.CustomerHomeActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TransactionSuccessActivity extends AppCompatActivity {

    private TextView txtAmount, txtTime, txtReceiverAccount, txtReceiverName, txtBankName,
            txtNote, txtTxnId;
    private Button btnNewTransaction, btnHome;

    private String userId, accountNumber, name, phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_success);

        // Ánh xạ view
        txtAmount = findViewById(R.id.txtAmount);
        txtTime = findViewById(R.id.txtTime);
        txtReceiverAccount = findViewById(R.id.txtReceiverAccount);
        txtReceiverName = findViewById(R.id.txtReceiverName);
        txtBankName = findViewById(R.id.txtBankName);
        txtNote = findViewById(R.id.txtNote);
        txtTxnId = findViewById(R.id.txtTxnId);
        btnNewTransaction = findViewById(R.id.btnNewTransaction);
        btnHome = findViewById(R.id.btnHome);

        // Nhận dữ liệu
        Intent intent = getIntent();
        txtAmount.setText(intent.getStringExtra("amount") + " VND");
        txtTime.setText(intent.getStringExtra("time"));
        txtReceiverAccount.setText(intent.getStringExtra("receiverAccount"));
        txtReceiverName.setText(intent.getStringExtra("receiverName"));
        txtBankName.setText(intent.getStringExtra("bankName"));
        txtNote.setText(intent.getStringExtra("note"));
        txtTxnId.setText(intent.getStringExtra("transactionId"));

        // Dữ liệu dùng lại
        userId = intent.getStringExtra("key");
        accountNumber = intent.getStringExtra("accountNumber");
        name = intent.getStringExtra("name");
        phoneNumber = intent.getStringExtra("phoneNumber");

        // Sau khi đã getExtra các trường từ Intent
        String txnId = intent.getStringExtra("transactionId");
        String time = intent.getStringExtra("time");
        String receiverAccount = intent.getStringExtra("receiverAccount");
        String receiverName = intent.getStringExtra("receiverName");
        String note = intent.getStringExtra("note");
        double amount = Double.parseDouble(intent.getStringExtra("amount").replace(",", ""));

// Truy vấn lại số dư và ghi lịch sử cho cả người gửi và người nhận
        DatabaseReference customerRef = FirebaseDatabase
                .getInstance("https://finalprojectandroid-ab72b-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("customers");

        DatabaseReference historyRef = FirebaseDatabase.getInstance("https://finalprojectandroid-ab72b-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("historys");

        customerRef.child(userId).child("checkingAccount").child("balance").get()
                .addOnSuccessListener(balanceSnapshot -> {
                    if (!balanceSnapshot.exists()) {
                        Log.e("HISTORY", "❌ Không lấy được số dư mới sau giao dịch");
                        return;
                    }

                    long senderBalance = balanceSnapshot.getValue(Long.class);
                    String info = accountNumber + " " + name + " chuyển tiền " + receiverAccount + " " + receiverName;
                    Date now = new Date();
                    String datePart = new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(now);
                    String timePart = new SimpleDateFormat("HH:mm", Locale.US).format(now);
                    String timestamp = timePart + " - " + datePart;
                    // Ghi lịch sử cho người gửi
                    Map<String, Object> senderHistory = new HashMap<>();
                    senderHistory.put("customerId", userId);
                    senderHistory.put("date", timestamp);
                    senderHistory.put("info", info);
                    senderHistory.put("balanceStatus", "Tài khoản " + accountNumber + " -" + (long) amount +
                            " lúc " + timestamp + ", số dư còn " + senderBalance);

                    historyRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            long count = snapshot.getChildrenCount();
                            String senderHistoryKey = "history" + (count + 1);
                            historyRef.child(senderHistoryKey).setValue(senderHistory)
                                    .addOnSuccessListener(v -> Log.d("HISTORY", "✅ Lưu lịch sử người gửi thành công"))
                                    .addOnFailureListener(e -> Log.e("HISTORY", "❌ Lỗi khi lưu lịch sử người gửi", e));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("HISTORY", "❌ Không thể ghi lịch sử người gửi", error.toException());
                        }
                    });

                    // 🔍 Tìm userId của người nhận theo số tài khoản
                    customerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String receiverId = null;
                            long receiverBalance = 0;

                            for (DataSnapshot child : snapshot.getChildren()) {
                                String acc = child.child("checkingAccount").child("accountNumber").getValue(String.class);
                                if (acc != null && acc.equals(receiverAccount)) {
                                    receiverId = child.getKey();
                                    Long bal = child.child("checkingAccount").child("balance").getValue(Long.class);
                                    receiverBalance = bal != null ? bal : 0;
                                    break;
                                }
                            }

                            if (receiverId == null) {
                                Log.e("HISTORY", "❌ Không tìm thấy người nhận với số tài khoản: " + receiverAccount);
                                return;
                            }

                            // Ghi lịch sử cho người nhận
                            Map<String, Object> receiverHistory = new HashMap<>();
                            receiverHistory.put("customerId", receiverId);
                            receiverHistory.put("date", timestamp);
                            receiverHistory.put("info", info);
                            receiverHistory.put("balanceStatus", "Tài khoản " + receiverAccount + " +" + (long) amount +
                                    " lúc " + timestamp + ", số dư còn " + receiverBalance);

                            historyRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    long count = snapshot.getChildrenCount();
                                    String receiverHistoryKey = "history" + (count + 1);
                                    historyRef.child(receiverHistoryKey).setValue(receiverHistory)
                                            .addOnSuccessListener(v -> Log.d("HISTORY", "✅ Lưu lịch sử người nhận thành công"))
                                            .addOnFailureListener(e -> Log.e("HISTORY", "❌ Lỗi khi lưu lịch sử người nhận", e));
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e("HISTORY", "❌ Không thể ghi lịch sử người nhận", error.toException());
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("HISTORY", "❌ Không truy vấn được customers", error.toException());
                        }
                    });

                })
                .addOnFailureListener(e -> Log.e("HISTORY", "❌ Lỗi khi lấy số dư mới", e));


        // Giao dịch mới
        btnNewTransaction.setOnClickListener(v -> fetchBalanceAndProceed(balance -> {
            Intent i = new Intent(TransactionSuccessActivity.this, TransferActivity.class);
            i.putExtra("key", userId);
            i.putExtra("accountNumber", accountNumber);
            i.putExtra("balance", String.format("%,.0f", balance));
            i.putExtra("name", name);
            i.putExtra("phoneNumber", phoneNumber);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        }));

        // Trở về trang chủ
        btnHome.setOnClickListener(v -> fetchBalanceAndProceed(balance -> {
            DatabaseReference userRef = FirebaseDatabase
                    .getInstance("https://finalprojectandroid-ab72b-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .getReference("users")
                    .child(userId);

            userRef.child("email").get().addOnSuccessListener(snapshot -> {
                String email = snapshot.getValue(String.class);

                Intent i = new Intent(TransactionSuccessActivity.this, CustomerHomeActivity.class);
                i.putExtra("key", userId);
                i.putExtra("email", email);
                i.putExtra("phoneNumber", phoneNumber);
                i.putExtra("name", name);
                i.putExtra("accountNumber", accountNumber);
                i.putExtra("balance", String.format("%,.0f", balance));
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Không thể lấy email", Toast.LENGTH_SHORT).show();
                Log.e("LOAD_EMAIL", "❌ Lỗi khi đọc email từ Firebase", e);
            });
        }));
    }

    /**
     * Tách phần lấy balance từ Firebase dùng lại được
     */
    private void fetchBalanceAndProceed(BalanceCallback callback) {
        DatabaseReference balanceRef = FirebaseDatabase
                .getInstance("https://finalprojectandroid-ab72b-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("customers")
                .child(userId)
                .child("checkingAccount")
                .child("balance");

        balanceRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                double balance = snapshot.getValue(Double.class);
                Log.d("Detail", "Balance từ database: " + balance);
                callback.onBalanceRetrieved(balance);
            } else {
                Toast.makeText(this, "Không tìm thấy số dư", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Lỗi truy vấn số dư: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Giao diện callback cho việc lấy balance
     */
    interface BalanceCallback {
        void onBalanceRetrieved(double balance);
    }
}
