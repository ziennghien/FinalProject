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

public class AddCustomerActivity extends AppCompatActivity {

    private EditText edtID, edtName, edtCCCD, edtAddress, edtPhone, edtEmail;
    private Button btnSave;
    private static final String DB_URL =
            "https://finalprojectandroid-ab72b-default-rtdb.asia-southeast1.firebasedatabase.app";

    private final Random random = new Random();
    private String generatedCustomerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_khach_hang);

        edtID      = findViewById(R.id.edtID);
        edtName    = findViewById(R.id.edtName);
        edtCCCD    = findViewById(R.id.edtCCCD);
        edtAddress = findViewById(R.id.edtAddress);
        edtPhone   = findViewById(R.id.edtPhone);
        edtEmail   = findViewById(R.id.edtEmail);
        btnSave    = findViewById(R.id.btnSave);
        edtID.setEnabled(false);

        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        // Sinh mã khách hàng mới
        DatabaseReference customerRef = FirebaseDatabase.getInstance(DB_URL).getReference("customers");
        customerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                int customerCount = (int) snapshot.getChildrenCount() + 1;
                generatedCustomerId = String.format("Cus%05d", customerCount);
                edtID.setText(generatedCustomerId);
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });

        btnSave.setOnClickListener(v -> validateAndSave());
    }

    private void validateAndSave() {
        String name = edtName.getText().toString().trim();
        String cccd = edtCCCD.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(cccd) || TextUtils.isEmpty(address)
                || TextUtils.isEmpty(phone) || TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!phone.matches("\\+84\\d{9}")) {
            Toast.makeText(this, "Số điện thoại phải đúng định dạng +849xxxxxxxx", Toast.LENGTH_LONG).show();
            return;
        }

        if (!email.endsWith("@gmail.com")) {
            Toast.makeText(this, "Email phải có đuôi @gmail.com", Toast.LENGTH_LONG).show();
            return;
        }

        DatabaseReference db = FirebaseDatabase.getInstance(DB_URL).getReference();

        // Kiểm tra trùng sđt, email, CCCD
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean phoneExists = false;
                boolean emailExists = false;
                boolean cccdExists = false;

                for (DataSnapshot user : snapshot.child("users").getChildren()) {
                    String p = user.child("phoneNumber").getValue(String.class);
                    String e = user.child("email").getValue(String.class);
                    if (phone.equals(p)) phoneExists = true;
                    if (email.equalsIgnoreCase(e)) emailExists = true;
                }

                for (DataSnapshot cust : snapshot.child("customers").getChildren()) {
                    String c = cust.child("cccd").getValue(String.class);
                    if (cccd.equals(c)) cccdExists = true;
                }

                if (phoneExists) {
                    Toast.makeText(AddCustomerActivity.this, "Số điện thoại đã tồn tại", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (emailExists) {
                    Toast.makeText(AddCustomerActivity.this, "Email đã tồn tại", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (cccdExists) {
                    Toast.makeText(AddCustomerActivity.this, "CCCD đã tồn tại", Toast.LENGTH_SHORT).show();
                    return;
                }

                createCustomer(db, name, cccd, address, phone, email);
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddCustomerActivity.this, "Lỗi khi kiểm tra dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createCustomer(DatabaseReference db, String name, String cccd, String address, String phone, String email) {
        db.child("customers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                String checkingAccount = generateUniqueAccountNumber(snapshot, "checkingAccount", 10, "");
                String mortgageAccount = generateUniqueAccountNumber(snapshot, "mortgageAccount", 4, "MOR");
                String savingAccount   = generateUniqueAccountNumber(snapshot, "savingAccount", 4, "SAV");

                Map<String, Object> customerData = new HashMap<>();
                customerData.put("name", name);
                customerData.put("cccd", cccd);
                customerData.put("address", address);
                customerData.put("currentAddress", "");
                customerData.put("checkingAccount", Map.of(
                        "accountNumber", checkingAccount,
                        "balance", 0,
                        "nickname", name
                ));
                customerData.put("mortgageAccount", Map.of(
                        "accountNumber", mortgageAccount,
                        "interestRate", 0,
                        "loanAmount", 0,
                        "monthlyPayment", 0,
                        "remainingTermMonths", 0
                ));
                customerData.put("savingAccount", Map.of(
                        "accountNumber", savingAccount,
                        "balance", 0,
                        "interestRate", 0,
                        "termMonths", 0
                ));

                db.child("customers").child(generatedCustomerId).setValue(customerData);

                Map<String, Object> userData = new HashMap<>();
                userData.put("email", email);
                userData.put("password", "123456");
                userData.put("phoneNumber", phone);
                userData.put("role", "customer");

                db.child("users").child(generatedCustomerId).setValue(userData);

                Toast.makeText(AddCustomerActivity.this, "Thêm khách hàng thành công", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private String generateUniqueAccountNumber(DataSnapshot snapshot, String accountType, int length, String prefix) {
        boolean isUnique;
        String generated;
        do {
            int rand = (int) (Math.pow(10, length - 1) + random.nextInt((int) Math.pow(10, length) - (int) Math.pow(10, length - 1)));
            generated = prefix + rand;
            isUnique = true;
            for (DataSnapshot customer : snapshot.getChildren()) {
                String existing = customer.child(accountType).child("accountNumber").getValue(String.class);
                if (generated.equals(existing)) {
                    isUnique = false;
                    break;
                }
            }
        } while (!isUnique);
        return generated;
    }
}
