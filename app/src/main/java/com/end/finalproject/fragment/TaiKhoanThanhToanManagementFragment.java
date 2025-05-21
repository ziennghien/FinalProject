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

public class TaiKhoanThanhToanManagementFragment extends Fragment {

    private static final String ARG_UID = "uid";
    private static final String DB_URL =
            "https://finalprojectandroid-ab72b-default-rtdb.asia-southeast1.firebasedatabase.app";

    private String uid;
    private DatabaseReference checkingRef;

    private EditText etAccount, etNickname, etBalance;
    private Button btnSave;

    public TaiKhoanThanhToanManagementFragment() {}

    public static TaiKhoanThanhToanManagementFragment newInstance(String uid) {
        TaiKhoanThanhToanManagementFragment fragment = new TaiKhoanThanhToanManagementFragment();
        Bundle args = new Bundle();
        args.putString(ARG_UID, uid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uid = getArguments().getString(ARG_UID);
            checkingRef = FirebaseDatabase.getInstance(DB_URL)
                    .getReference("customers")
                    .child(uid)
                    .child("checkingAccount");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup   container,
                             @Nullable Bundle      savedInstanceState) {
        View v = inflater.inflate(
                R.layout.fragment_tai_khoan_thanh_toan_management,
                container, false
        );

        etAccount  = v.findViewById(R.id.et_account);
        etNickname = v.findViewById(R.id.et_nickname);
        etBalance  = v.findViewById(R.id.et_balance);
        btnSave    = v.findViewById(R.id.btn_save_address);

        // Disable editing on account number field
        etAccount.setEnabled(false);
        etAccount.setFocusable(false);

        loadCheckingAccountInfo();
        btnSave.setOnClickListener(x -> saveData());

        return v;
    }

    private void loadCheckingAccountInfo() {
        checkingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snap) {
                if (!snap.exists()) {
                    Toast.makeText(getContext(),
                            "Không tìm thấy tài khoản thanh toán",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                // account number remains read-only
                etAccount.setText(
                        snap.child("accountNumber").getValue(String.class)
                );
                etNickname.setText(
                        snap.child("nickname").getValue(String.class)
                );
                Object bal = snap.child("balance").getValue();
                etBalance.setText(bal != null ? bal.toString() : "");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError e) {
                Toast.makeText(getContext(),
                        "Lỗi khi tải dữ liệu: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveData() {
        String nick = etNickname.getText().toString().trim();
        String bal  = etBalance.getText().toString().trim();

        if (TextUtils.isEmpty(nick) || TextUtils.isEmpty(bal)) {
            Toast.makeText(getContext(),
                    "Vui lòng điền đầy đủ thông tin",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Only nickname and balance are editable
        checkingRef.child("nickname").setValue(nick);
        try {
            double d = Double.parseDouble(bal);
            checkingRef.child("balance").setValue(d);
        } catch (NumberFormatException ignored) {}

        Toast.makeText(getContext(),
                "Đã lưu tài khoản thanh toán",
                Toast.LENGTH_SHORT).show();
    }
}
