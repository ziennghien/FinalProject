package com.end.finalproject.customer;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.end.finalproject.R;

public class TransferActivity extends AppCompatActivity {

    private EditText edtBank, edtReceiverAccount, edtAmount, edtNote, edtPurpose;
    private Button btnContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        edtBank = findViewById(R.id.edtBank);
        edtReceiverAccount = findViewById(R.id.edtReceiverAccount);
        edtAmount = findViewById(R.id.edtAmount);
        edtNote = findViewById(R.id.edtNote);
        edtPurpose = findViewById(R.id.edtPurpose);
        btnContinue = findViewById(R.id.btnContinue);

        btnContinue.setOnClickListener(v -> {
            String bank = edtBank.getText().toString().trim();
            String account = edtReceiverAccount.getText().toString().trim();
            String amount = edtAmount.getText().toString().trim();
            String note = edtNote.getText().toString().trim();
            String purpose = edtPurpose.getText().toString().trim();

            if (bank.isEmpty() || account.isEmpty() || amount.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show();
                return;
            }

            // Thực hiện logic chuyển tiền ở đây (gọi API hoặc xử lý cục bộ)
            Toast.makeText(this, "Đã gửi thông tin chuyển tiền!", Toast.LENGTH_LONG).show();
        });
    }
}
