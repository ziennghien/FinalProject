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
        btnHome = findViewById(R.id.btnHome); // ánh xạ nút home mới

        // Nhận dữ liệu
        Intent intent = getIntent();
        txtAmount.setText(intent.getStringExtra("amount") + " VND");
        txtTime.setText(intent.getStringExtra("time"));
        txtReceiverAccount.setText(intent.getStringExtra("receiverAccount"));
        txtReceiverName.setText(intent.getStringExtra("receiverName"));
        txtBankName.setText(intent.getStringExtra("bankName"));
        txtNote.setText(intent.getStringExtra("note"));
        txtTxnId.setText(intent.getStringExtra("transactionId"));

        // Nhấn để chuyển sang màn hình giao dịch mới
        btnNewTransaction.setOnClickListener(v -> {
            Intent i = new Intent(TransactionSuccessActivity.this, TransferActivity.class);

            // Lấy dữ liệu cần truyền lại từ Intent hiện tại
            i.putExtra("key", getIntent().getStringExtra("key"));
            i.putExtra("accountNumber", getIntent().getStringExtra("accountNumber"));
            i.putExtra("balance", getIntent().getDoubleExtra("balance", 0));
            i.putExtra("name", getIntent().getStringExtra("name"));
            i.putExtra("phoneNumber", getIntent().getStringExtra("phoneNumber"));

            // Xóa stack cũ để tránh back lại màn hình success
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        });

        // Nhấn để quay về trang chủ
        btnHome.setOnClickListener(v -> {
            String userId = getIntent().getStringExtra("key");

            DatabaseReference userRef = FirebaseDatabase
                    .getInstance("https://finalprojectandroid-ab72b-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .getReference("users")
                    .child(userId);

            userRef.child("email").get().addOnSuccessListener(snapshot -> {
                String email = snapshot.getValue(String.class);

                Intent i = new Intent(TransactionSuccessActivity.this, CustomerHomeActivity.class);
                i.putExtra("key", userId);
                i.putExtra("email", email);
                i.putExtra("phoneNumber", getIntent().getStringExtra("phoneNumber"));
                i.putExtra("name", getIntent().getStringExtra("name"));
                i.putExtra("accountNumber", getIntent().getStringExtra("accountNumber"));
                i.putExtra("balance", getIntent().getDoubleExtra("balance", 0));
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Không thể lấy email", Toast.LENGTH_SHORT).show();
                Log.e("LOAD_EMAIL", "❌ Lỗi khi đọc email từ Firebase", e);
            });
        });

    }

}
