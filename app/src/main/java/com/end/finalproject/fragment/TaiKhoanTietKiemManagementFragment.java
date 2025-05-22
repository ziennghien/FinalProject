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

public class TaiKhoanTietKiemManagementFragment extends Fragment {

    private static final String ARG_UID = "uid";
    private static final String DB_URL =
            "https://finalprojectandroid-ab72b-default-rtdb.asia-southeast1.firebasedatabase.app";

    private String uid;
    private DatabaseReference savingRef;

    private EditText etAccount, etBalance, etInterestRate, etTermMonths;
    private Button btnSave;

    public TaiKhoanTietKiemManagementFragment() {}

    public static TaiKhoanTietKiemManagementFragment newInstance(String uid) {
        TaiKhoanTietKiemManagementFragment fragment = new TaiKhoanTietKiemManagementFragment();
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
            savingRef = FirebaseDatabase.getInstance(DB_URL)
                    .getReference("customers")
                    .child(uid)
                    .child("savingAccount");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup   container,
                             @Nullable Bundle      savedInstanceState) {
        View v = inflater.inflate(
                R.layout.fragment_tai_khoan_tiet_kiem_management,
                container, false
        );

        etAccount      = v.findViewById(R.id.et_saving_account);
        etBalance      = v.findViewById(R.id.et_saving_balance);
        etInterestRate = v.findViewById(R.id.et_interest_rate);
        etTermMonths   = v.findViewById(R.id.et_term_months);
        btnSave        = v.findViewById(R.id.btn_save_address);

        // Disable editing on account number
        etAccount.setEnabled(false);
        etAccount.setFocusable(false);

        loadSavingAccountInfo();
        btnSave.setOnClickListener(x -> saveData());

        return v;
    }

    private void loadSavingAccountInfo() {
        if (savingRef == null) return;
        savingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snap) {
                if (!snap.exists()) {
                    Toast.makeText(getContext(),
                            "Không tìm thấy tài khoản tiết kiệm",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                etAccount.setText(
                        snap.child("accountNumber").getValue(String.class)
                );
                Object bal = snap.child("balance").getValue();
                etBalance.setText(bal != null ? bal.toString() : "");
                Object rate = snap.child("interestRate").getValue();
                etInterestRate.setText(rate != null ? rate.toString() : "");
                Object term = snap.child("termMonths").getValue();
                etTermMonths.setText(term != null ? term.toString() : "");
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),
                        "Lỗi tải dữ liệu: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveData() {
        String bal  = etBalance.getText().toString().trim();
        String rate = etInterestRate.getText().toString().trim();
        String term = etTermMonths.getText().toString().trim();

        if (TextUtils.isEmpty(bal)
                || TextUtils.isEmpty(rate)
                || TextUtils.isEmpty(term)) {
            Toast.makeText(getContext(),
                    "Vui lòng điền đầy đủ thông tin",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Save editable fields only
        try {
            savingRef.child("balance")
                    .setValue(Double.parseDouble(bal));
        } catch (NumberFormatException ignored) {}
        try {
            savingRef.child("interestRate")
                    .setValue(Double.parseDouble(rate));
        } catch (NumberFormatException ignored) {}
        try {
            savingRef.child("termMonths")
                    .setValue(Integer.parseInt(term));
        } catch (NumberFormatException ignored) {}

        Toast.makeText(getContext(),
                "Đã lưu tài khoản tiết kiệm",
                Toast.LENGTH_SHORT).show();
    }
}
