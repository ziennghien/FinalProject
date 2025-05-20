package com.end.finalproject;

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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TaiKhoanThanhToanFragment extends Fragment {

    private String uid;

    private EditText etAccount, etNickname, etBalance;
    private Button btnSave;

    public TaiKhoanThanhToanFragment() {}

    public static TaiKhoanThanhToanFragment newInstance(String uid) {
        TaiKhoanThanhToanFragment fragment = new TaiKhoanThanhToanFragment();
        Bundle args = new Bundle();
        args.putString("uid", uid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uid = getArguments().getString("uid");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tai_khoan_thanh_toan, container, false);

        etAccount = view.findViewById(R.id.et_account);
        etNickname = view.findViewById(R.id.et_nickname);
        etBalance = view.findViewById(R.id.et_balance);
        btnSave = view.findViewById(R.id.btn_save_address); // reuse button ID

        if (uid != null) {
            loadCheckingAccountInfo(uid);
        }

        btnSave.setOnClickListener(v -> {
            String newNickname = etNickname.getText().toString().trim();

            if (TextUtils.isEmpty(newNickname)) {
                Toast.makeText(getContext(), "Vui lòng nhập biệt danh", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseReference allCustomersRef = FirebaseDatabase.getInstance(
                            "https://finalprojectandroid-ab72b-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .getReference("customers");

            allCustomersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean nicknameExists = false;

                    for (DataSnapshot customerSnapshot : snapshot.getChildren()) {
                        if (customerSnapshot.getKey().equals(uid)) continue;

                        String existingNickname = customerSnapshot
                                .child("checkingAccount")
                                .child("nickname")
                                .getValue(String.class);

                        if (newNickname.equalsIgnoreCase(existingNickname)) {
                            nicknameExists = true;
                            break;
                        }
                    }

                    if (nicknameExists) {
                        Toast.makeText(getContext(), "Biệt danh này đã được sử dụng!", Toast.LENGTH_SHORT).show();
                    } else {
                        DatabaseReference ref = allCustomersRef.child(uid).child("checkingAccount");
                        ref.child("nickname").setValue(newNickname)
                                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Đã lưu biệt danh!", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi khi lưu: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Lỗi kiểm tra biệt danh: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        return view;
    }

    private void loadCheckingAccountInfo(String uid) {
        DatabaseReference ref = FirebaseDatabase.getInstance(
                        "https://finalprojectandroid-ab72b-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("customers")
                .child(uid)
                .child("checkingAccount");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String accountNumber = snapshot.child("accountNumber").getValue(String.class);
                    String nickname = snapshot.child("nickname").getValue(String.class);
                    Object balanceObj = snapshot.child("balance").getValue();

                    etAccount.setText(accountNumber != null ? accountNumber : "");
                    etNickname.setText(nickname != null ? nickname : "");
                    etBalance.setText(balanceObj != null ? balanceObj.toString() : "0");
                } else {
                    Toast.makeText(getContext(), "Không tìm thấy tài khoản thanh toán", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi khi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
