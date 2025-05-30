package com.end.finalproject.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.end.finalproject.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TaiKhoanManagementFragment extends Fragment {
    private static final String ARG_UID = "uid";
    private static final String DB_URL =
            "https://finalprojectandroid-ab72b-default-rtdb.asia-southeast1.firebasedatabase.app";

    private String uid;
    private DatabaseReference custRef;
    private DatabaseReference userRef;

    private EditText etName, etPhoneNumber, etEmail, etCCCD, etAddress, etCurrentAddress, etPassword;
    private Button btnSave;

    public TaiKhoanManagementFragment() {}

    public static TaiKhoanManagementFragment newInstance(String uid) {
        TaiKhoanManagementFragment f = new TaiKhoanManagementFragment();
        Bundle args = new Bundle();
        args.putString(ARG_UID, uid);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uid = getArguments().getString(ARG_UID);
        }
        custRef = FirebaseDatabase.getInstance(DB_URL)
                .getReference("customers").child(uid);
        userRef = FirebaseDatabase.getInstance(DB_URL)
                .getReference("users").child(uid);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tai_khoan_management, container, false);

        etPassword       = v.findViewById(R.id.et_password);

        etName           = v.findViewById(R.id.et_name);
        etPhoneNumber    = v.findViewById(R.id.et_phoneNumber);
        etEmail          = v.findViewById(R.id.et_email);
        etCCCD           = v.findViewById(R.id.et_cccd);
        etAddress        = v.findViewById(R.id.et_address);
        etCurrentAddress = v.findViewById(R.id.et_currentAddress);
        btnSave          = v.findViewById(R.id.btn_save_address);

        loadCustomerInfo();
        btnSave.setOnClickListener(x -> saveData());

        return v;
    }

    private void loadCustomerInfo() {
        // 1) Load from "customers"
        custRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snap) {
                if (!snap.exists()) {
                    Toast.makeText(getContext(),
                            "Không tìm thấy dữ liệu khách hàng", Toast.LENGTH_SHORT).show();
                    return;
                }
                etName.setText(snap.child("name").getValue(String.class));
                etCCCD.setText(snap.child("cccd").getValue(String.class));
                etAddress.setText(snap.child("address").getValue(String.class));
                etCurrentAddress.setText(snap.child("currentAddress").getValue(String.class));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),
                        "Lỗi khi tải dữ liệu: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        // 2) Load from "users"
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snap) {
                if (!snap.exists()) return;
                etEmail.setText(snap.child("email").getValue(String.class));
                etPhoneNumber.setText(snap.child("phoneNumber").getValue(String.class));
                etPassword.setText(snap.child("password").getValue(String.class));  // thêm dòng này

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // no-op
            }
        });
    }

    private void saveData() {
        String passVal = etPassword.getText().toString().trim();
        String nameVal  = etName.getText().toString().trim();
        String phoneVal = etPhoneNumber.getText().toString().trim();
        String emailVal = etEmail.getText().toString().trim();
        String cccdVal  = etCCCD.getText().toString().trim();
        String addrVal  = etAddress.getText().toString().trim();
        String currVal  = etCurrentAddress.getText().toString().trim();

        // Kiểm tra rỗng
        if (TextUtils.isEmpty(nameVal) || TextUtils.isEmpty(phoneVal) || TextUtils.isEmpty(emailVal)
                || TextUtils.isEmpty(cccdVal) || TextUtils.isEmpty(addrVal) || TextUtils.isEmpty(currVal)) {
            Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra định dạng số điện thoại
        if (!phoneVal.matches("\\+84\\d{9}")) {
            Toast.makeText(getContext(), "Số điện thoại phải có định dạng +849xxxxxxxx", Toast.LENGTH_LONG).show();
            return;
        }

        // Kiểm tra định dạng email
        if (!emailVal.endsWith("@gmail.com")) {
            Toast.makeText(getContext(), "Email phải có định dạng @gmail.com", Toast.LENGTH_LONG).show();
            return;
        }

        DatabaseReference rootRef = FirebaseDatabase.getInstance(DB_URL).getReference();

        // Kiểm tra trùng CCCD
        rootRef.child("customers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean duplicateCCCD = false;

                for (DataSnapshot snap : snapshot.getChildren()) {
                    String existingCCCD = snap.child("cccd").getValue(String.class);
                    if (cccdVal.equals(existingCCCD) && !snap.getKey().equals(uid)) {
                        duplicateCCCD = true;
                        break;
                    }
                }

                if (duplicateCCCD) {
                    Toast.makeText(getContext(), "Số CCCD đã được sử dụng bởi tài khoản khác!", Toast.LENGTH_LONG).show();
                    return;
                }

                // Nếu không trùng thì lưu
                custRef.child("name").setValue(nameVal);
                custRef.child("cccd").setValue(cccdVal);
                custRef.child("address").setValue(addrVal);
                custRef.child("currentAddress").setValue(currVal);

                userRef.child("email").setValue(emailVal);
                userRef.child("phoneNumber").setValue(phoneVal);
                userRef.child("password").setValue(passVal);

                Toast.makeText(getContext(), "Đã lưu thông tin tài khoản", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi kiểm tra CCCD: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
