package com.end.finalproject.customer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.end.finalproject.R;
import com.end.finalproject.home.CustomerHomeActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
