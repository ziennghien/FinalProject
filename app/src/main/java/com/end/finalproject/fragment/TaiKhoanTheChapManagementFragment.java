package com.end.finalproject.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

public class TaiKhoanTheChapManagementFragment extends Fragment {

    private static final String ARG_UID = "uid";
    private static final String DB_URL =
            "https://finalprojectandroid-ab72b-default-rtdb.asia-southeast1.firebasedatabase.app";

    private String uid;
    private DatabaseReference mortgageRef;

    private EditText etAccount;
    private EditText etInterestRate;
    private EditText etLoanAmount;
    private EditText etMonthlyPayment;
    private EditText etRemainingTerm;
    private Button btnSave;

    public TaiKhoanTheChapManagementFragment() {}

    public static TaiKhoanTheChapManagementFragment newInstance(String uid) {
        TaiKhoanTheChapManagementFragment fragment = new TaiKhoanTheChapManagementFragment();
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
            mortgageRef = FirebaseDatabase
                    .getInstance(DB_URL)
                    .getReference("customers")
                    .child(uid)
                    .child("mortgageAccount");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.fragment_tai_khoan_the_chap_management,
                container, false
        );

        etAccount        = view.findViewById(R.id.et_mortgage_account);
        etInterestRate   = view.findViewById(R.id.et_interest_rate);
        etLoanAmount     = view.findViewById(R.id.et_loan_amount);
        etMonthlyPayment = view.findViewById(R.id.et_monthly_payment);
        etRemainingTerm  = view.findViewById(R.id.et_remaining_term);
        btnSave          = view.findViewById(R.id.btn_save_address);

        // Disable editing on account number
        etAccount.setEnabled(false);
        etAccount.setFocusable(false);

        loadMortgageAccountInfo();
        btnSave.setOnClickListener(v -> saveData());

        return view;
    }

    private void loadMortgageAccountInfo() {
        if (mortgageRef == null) return;
        mortgageRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snap) {
                if (!snap.exists()) {
                    Toast.makeText(getContext(),
                            "Không tìm thấy tài khoản thế chấp",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                etAccount.setText(
                        snap.child("accountNumber").getValue(String.class)
                );
                Object rate = snap.child("interestRate").getValue();
                etInterestRate.setText(rate != null ? rate.toString() : "");
                Object loan = snap.child("loanAmount").getValue();
                etLoanAmount.setText(loan != null ? loan.toString() : "");
                Object pm = snap.child("monthlyPayment").getValue();
                etMonthlyPayment.setText(pm != null ? pm.toString() : "");
                Object rem = snap.child("remainingTermMonths").getValue();
                etRemainingTerm.setText(rem != null ? rem.toString() : "");
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),
                        "Lỗi tải dữ liệu: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveData() {
        String rate   = etInterestRate.getText().toString().trim();
        String loan   = etLoanAmount.getText().toString().trim();
        String pay    = etMonthlyPayment.getText().toString().trim();
        String remain = etRemainingTerm.getText().toString().trim();

        if (TextUtils.isEmpty(rate)
                || TextUtils.isEmpty(loan)
                || TextUtils.isEmpty(pay)
                || TextUtils.isEmpty(remain)) {
            Toast.makeText(getContext(),
                    "Vui lòng điền đầy đủ thông tin",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Save only editable fields
        try {
            mortgageRef.child("interestRate")
                    .setValue(Double.parseDouble(rate));
        } catch (NumberFormatException ignored) {}
        try {
            mortgageRef.child("loanAmount")
                    .setValue(Double.parseDouble(loan));
        } catch (NumberFormatException ignored) {}
        try {
            mortgageRef.child("monthlyPayment")
                    .setValue(Double.parseDouble(pay));
        } catch (NumberFormatException ignored) {}
        try {
            mortgageRef.child("remainingTermMonths")
                    .setValue(Integer.parseInt(remain));
        } catch (NumberFormatException ignored) {}

        Toast.makeText(getContext(),
                "Đã lưu tài khoản thế chấp",
                Toast.LENGTH_SHORT).show();
    }
}
