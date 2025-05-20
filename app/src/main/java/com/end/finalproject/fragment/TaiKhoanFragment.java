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

public class TaiKhoanFragment extends Fragment {

    private String uid, email, phoneNumber;

    private EditText etName, etPhoneNumber, etEmail, etCCCD, etAddress, etCurrentAddress;
    private Button btnSave;

    public TaiKhoanFragment() {}

    public static TaiKhoanFragment newInstance(String uid, String email, String phoneNumber) {
        TaiKhoanFragment fragment = new TaiKhoanFragment();
        Bundle args = new Bundle();
        args.putString("uid", uid);
        args.putString("email", email);
        args.putString("phoneNumber", phoneNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uid = getArguments().getString("uid");
            email = getArguments().getString("email");
            phoneNumber = getArguments().getString("phoneNumber");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tai_khoan, container, false);

        // Ánh xạ view
        etName = view.findViewById(R.id.et_name);
        etPhoneNumber = view.findViewById(R.id.et_phoneNumber);
        etEmail = view.findViewById(R.id.et_email);
        etCCCD = view.findViewById(R.id.et_cccd);
        etAddress = view.findViewById(R.id.et_address);
        etCurrentAddress = view.findViewById(R.id.et_currentAddress);
        btnSave = view.findViewById(R.id.btn_save_address);

        // Load dữ liệu từ Firebase
        if (uid != null) {
            loadCustomerInfo(uid, email, phoneNumber);
        }

        // Lưu địa chỉ tạm trú
        btnSave.setOnClickListener(v -> {
            String newAddress = etCurrentAddress.getText().toString().trim();
            if (TextUtils.isEmpty(newAddress)) {
                Toast.makeText(getContext(), "Vui lòng nhập địa chỉ tạm trú", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseReference ref = FirebaseDatabase.getInstance(
                            "https://finalprojectandroid-ab72b-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .getReference("customers").child(uid);

            ref.child("currentAddress").setValue(newAddress)
                    .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Đã lưu địa chỉ!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi khi lưu: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        return view;
    }

    private void loadCustomerInfo(String uid, String email, String phoneNumber) {
        DatabaseReference ref = FirebaseDatabase.getInstance(
                        "https://finalprojectandroid-ab72b-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("customers").child(uid);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    etName.setText(snapshot.child("name").getValue(String.class));
                    etPhoneNumber.setText(phoneNumber != null ? phoneNumber : "");
                    etEmail.setText(email != null ? email : "");
                    etCCCD.setText(snapshot.child("cccd").getValue(String.class));
                    etAddress.setText(snapshot.child("address").getValue(String.class));
                    etCurrentAddress.setText(snapshot.child("currentAddress").getValue(String.class));
                } else {
                    Toast.makeText(getContext(), "Không tìm thấy dữ liệu người dùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
