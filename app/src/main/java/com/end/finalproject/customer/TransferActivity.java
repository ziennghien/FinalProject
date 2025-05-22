package com.end.finalproject.customer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.end.finalproject.R;
import com.end.finalproject.model.Bank;
import com.end.finalproject.model.BankResponse;
import com.end.finalproject.model.TransferRequest;
import com.end.finalproject.model.TransferResponse;
import com.end.finalproject.model.VietQrApi;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TransferActivity extends AppCompatActivity {

    private EditText edtReceiverAccount, edtReceiverName, edtAmount, edtNote, edtPurpose;
    private Spinner spinnerBank;
    private Button btnContinue;
    private List<Bank> bankList = new ArrayList<>();
    private String selectedBin = "";
    private FirebaseFunctions mFunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        // Nút back
        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        mFunctions = FirebaseFunctions.getInstance();

        // Ánh xạ view
        spinnerBank = findViewById(R.id.spinnerBank);
        edtReceiverAccount = findViewById(R.id.edtReceiverAccount);
        edtReceiverName = findViewById(R.id.edtReceiverName);
        edtAmount = findViewById(R.id.edtAmount);
        edtNote = findViewById(R.id.edtNote);
        edtPurpose = findViewById(R.id.edtPurpose);
        btnContinue = findViewById(R.id.btnContinue);

        loadBanks();
        String phoneNumber = getIntent().getStringExtra("phoneNumber");

        String ten = getIntent().getStringExtra("name");
        edtNote.setText(ten.toUpperCase() + " chuyen tien");

        spinnerBank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedBin = bankList.get(position).bin;
                Log.d("DEBUG_BANK", "Chọn bin: " + selectedBin);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedBin = "";
            }
        });

        edtReceiverAccount.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                String account = edtReceiverAccount.getText().toString().trim();
                Log.d("DEBUG_ACCOUNT", "Account: " + account + ", Bin: " + selectedBin);
                if (!account.isEmpty() && !selectedBin.isEmpty()) {
                    lookupReceiverWithFirebase(selectedBin, account);
                } else {
                    Log.w("DEBUG_SKIP", "Thiếu thông tin, không gọi function");
                }
            }
        });

        btnContinue.setOnClickListener(view -> {
            String account = edtReceiverAccount.getText().toString().trim();
            String amount = edtAmount.getText().toString().trim();
            String note = edtNote.getText().toString().trim();

            if (account.isEmpty() || amount.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show();
                return;
            }

            double amountValue;
            try {
                amountValue = Double.parseDouble(amount);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Số tiền không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            double balance = Double.parseDouble(getIntent().getStringExtra("balance"));
            if (amountValue > balance) {
                Toast.makeText(this, "Số dư không đủ", Toast.LENGTH_SHORT).show();
                return;
            }

            btnContinue.setEnabled(false);

            String fromAccount = getIntent().getStringExtra("accountNumber");
            TransferRequest request = new TransferRequest(fromAccount, account, selectedBin, amountValue, note);

            // Gửi OTP
            Map<String, Object> data = new HashMap<>();
            data.put("accountNumber", fromAccount);
            data.put("phoneNumber", phoneNumber); // ✅ thêm dòng này
            Log.d("Mark", "Sắp gọi sendOtp");
            Log.d("OTP_CALL", "Đang gửi OTP với data: " + new Gson().toJson(data));

            mFunctions.getHttpsCallable("sendOtp")
                .call(data)
                .addOnSuccessListener(result -> {
                    Toast.makeText(this, "Mã OTP đã gửi. Vui lòng kiểm tra.", Toast.LENGTH_SHORT).show();
                    showOtpDialog(fromAccount, request);  // mở dialog
                })
                .addOnFailureListener(e -> {
                    //Toast.makeText(this, "Gửi OTP thất bại", Toast.LENGTH_SHORT).show();
                    Log.e("OTP_FAIL", "❌ Gửi OTP thất bại", e);
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    btnContinue.setEnabled(true);
                });
            Log.d("Mark", "Gọi xong sendOtp");
        });
    }

    private void lookupReceiverWithFirebase(String bankCode, String accountNumber) {
        String binVal = String.valueOf(bankCode).trim();
        String accVal = String.valueOf(accountNumber).trim();

        Map<String, Object> data = new HashMap<>();
        data.put("bin", binVal);
        data.put("accountNumber", accVal);

        Log.d("CALL_FN", "✅ Truyền vào Firebase: bin=" + binVal + ", accountNumber=" + accVal);
        Log.d("CALL_FN_MAP", "📦 Data map = " + new com.google.gson.Gson().toJson(data));

        mFunctions.getHttpsCallable("lookupAccount")
                .call(data)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Map result = (Map) task.getResult().getData();
                        Boolean success = (Boolean) result.get("success");
                        if (success != null && success) {
                            String name = (String) result.get("accountName");
                            edtReceiverName.setText(name);
                        } else {
                            edtReceiverName.setText("");
                            Toast.makeText(this, "Tài khoản không tồn tại", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Exception e = task.getException();
                        edtReceiverName.setText("");
                        if (e instanceof FirebaseFunctionsException) {
                            FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                            Log.e("FIREBASE_FN", "❌ Lỗi Firebase: " + ffe.getCode() + " - " + ffe.getMessage());
                        } else {
                            Log.e("FIREBASE_FN", "❌ Lỗi không xác định: " + e.getMessage());
                        }
                        Toast.makeText(this, "Không thể xác minh tài khoản", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadBanks() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.vietqr.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        VietQrApi api = retrofit.create(VietQrApi.class);

        api.getBanks().enqueue(new Callback<BankResponse>() {
            @Override
            public void onResponse(Call<BankResponse> call, Response<BankResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Bank> allBanks = response.body().data;
                    List<Bank> lookupSupportedBanks = new ArrayList<>();

                    for (Bank bank : allBanks) {
                        if (bank.lookupSupported == 1) {
                            lookupSupportedBanks.add(bank);
                        }
                    }

                    bankList = lookupSupportedBanks;

                    ArrayAdapter<Bank> adapter = new ArrayAdapter<>(
                            TransferActivity.this,
                            android.R.layout.simple_spinner_item,
                            bankList
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerBank.setAdapter(adapter);
                } else {
                    Toast.makeText(TransferActivity.this, "Không tải được danh sách ngân hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BankResponse> call, Throwable t) {
                Toast.makeText(TransferActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showOtpDialog(String fromAccount, TransferRequest transferRequest) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_otp_input, null);
        EditText edtOtpInput = dialogView.findViewById(R.id.edtOtpInput);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Xác minh OTP")
                .setView(dialogView)
                .setCancelable(false)
                .setPositiveButton("Xác nhận", null)  // Ta sẽ override sau để tránh auto-dismiss
                .setNegativeButton("Huỷ", (d, which) -> {
                    Toast.makeText(this, "Huỷ giao dịch", Toast.LENGTH_SHORT).show();
                    btnContinue.setEnabled(true);
                })
                .create();

        dialog.setOnShowListener(d -> {
            Button btnConfirm = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            btnConfirm.setOnClickListener(v -> {
                String otp = edtOtpInput.getText().toString().trim();
                if (otp.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập OTP", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Gọi Firebase xác minh OTP
                Map<String, Object> data = new HashMap<>();
                data.put("accountNumber", fromAccount);
                data.put("otp", otp);

                mFunctions.getHttpsCallable("verifyOtp")
                        .call(data)
                        .addOnSuccessListener(result -> {
                            Map<String, Object> res = (Map<String, Object>) result.getData();
                            boolean success = (Boolean) res.get("success");
                            if (success) {
                                dialog.dismiss();
                                Toast.makeText(this, "✅ Xác minh thành công. Đang chuyển tiền...", Toast.LENGTH_SHORT).show();
                                transferMoney(transferRequest);
                            } else {
                                Toast.makeText(this, "❌ OTP không đúng", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Lỗi xác minh OTP", Toast.LENGTH_SHORT).show();
                        });
            });
        });

        dialog.show();
    }
    private void transferMoney(TransferRequest request) {
        double amount = request.amount;
        String fromAccount = request.fromAccount;
        String transactionId = "TXN" + System.currentTimeMillis();
        long timestamp = System.currentTimeMillis();

        btnContinue.setEnabled(false);
        String userId = getIntent().getStringExtra("key");
        // Đọc số dư từ Realtime Database
        DatabaseReference balanceRef = FirebaseDatabase.getInstance("https://finalprojectandroid-ab72b-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("customers")
                .child(userId)
                .child("checkingAccount")
                .child("balance");

        balanceRef.get().addOnSuccessListener(snapshot -> {
            if (!snapshot.exists()) {
                Toast.makeText(this, "Không tìm thấy số dư", Toast.LENGTH_SHORT).show();
                btnContinue.setEnabled(true);
                return;
            }

            double currentBalance = snapshot.getValue(Double.class);

            if (amount < 1000) {
                Toast.makeText(this, "Số tiền phải từ 1000 VND trở lên", Toast.LENGTH_SHORT).show();
                btnContinue.setEnabled(true);
                return;
            }

            if (amount > currentBalance) {
                Toast.makeText(this, "Số dư không đủ", Toast.LENGTH_SHORT).show();
                btnContinue.setEnabled(true);
                return;
            }

            // Tạo dữ liệu giao dịch
            Map<String, Object> transferData = new HashMap<>();
            transferData.put("transactionId", transactionId);
            transferData.put("fromAccount", fromAccount);
            transferData.put("toAccount", request.toAccount);
            transferData.put("bankBin", request.bankBin);
            transferData.put("amount", amount);
            transferData.put("note", request.note);
            transferData.put("timestamp", timestamp);

            String phoneNumber = getIntent().getStringExtra("phoneNumber");

            String ten = getIntent().getStringExtra("name");

            DatabaseReference txnRef = FirebaseDatabase.getInstance("https://finalprojectandroid-ab72b-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .getReference("transactions")
                    .child(fromAccount)
                    .child(transactionId);

            txnRef.setValue(transferData)
                    .addOnSuccessListener(aVoid -> {
                        // Trừ tiền sau khi ghi giao dịch thành công
                        balanceRef.setValue(currentBalance - amount)
                                .addOnSuccessListener(unused -> {
                                    Intent intent = new Intent(TransferActivity.this, TransactionSuccessActivity.class);
                                    intent.putExtra("amount", String.format("%,.0f", amount));
                                    intent.putExtra("time", new SimpleDateFormat("HH:mm 'Thứ' E dd/MM/yyyy", Locale.getDefault()).format(new Date(timestamp)));
                                    intent.putExtra("receiverAccount", request.toAccount);
                                    intent.putExtra("receiverName", edtReceiverName.getText().toString()); // hoặc lấy từ lookup
                                    Bank selectedBank = bankList.get(spinnerBank.getSelectedItemPosition());
                                    intent.putExtra("bankName", selectedBank.toString());
                                    intent.putExtra("note", request.note);
                                    intent.putExtra("transactionId", transactionId);
                                    intent.putExtra("key", userId);
                                    intent.putExtra("accountNumber", fromAccount);
                                    intent.putExtra("name", ten);
                                    intent.putExtra("phoneNumber", phoneNumber);
                                    startActivity(intent);
                                    finish(); // kết thúc TransferActivity
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "❌ Giao dịch ghi thành công nhưng cập nhật số dư thất bại", Toast.LENGTH_SHORT).show();
                                    Log.e("TRANSFER", "❌ Lỗi khi cập nhật số dư", e);
                                    btnContinue.setEnabled(true);
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "❌ Giao dịch thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("TRANSFER", "❌ Lỗi khi ghi giao dịch", e);
                        btnContinue.setEnabled(true);
                    });

        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Lỗi đọc số dư: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("TRANSFER", "❌ Không đọc được số dư", e);
            btnContinue.setEnabled(true);
        });
    }

}
