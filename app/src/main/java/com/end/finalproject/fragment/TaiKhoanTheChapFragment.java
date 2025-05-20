package com.end.finalproject.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.end.finalproject.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TaiKhoanTheChapFragment extends Fragment {

    private String uid;
    private EditText etAccount, etInterestRate, etLoanAmount, etMonthlyPayment, etRemainingTerm;

    public TaiKhoanTheChapFragment() {}

    public static TaiKhoanTheChapFragment newInstance(String uid) {
        TaiKhoanTheChapFragment fragment = new TaiKhoanTheChapFragment();
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
        View view = inflater.inflate(R.layout.fragment_tai_khoan_the_chap, container, false);

        etAccount = view.findViewById(R.id.et_mortgage_account);
        etInterestRate = view.findViewById(R.id.et_interest_rate);
        etLoanAmount = view.findViewById(R.id.et_loan_amount);
        etMonthlyPayment = view.findViewById(R.id.et_monthly_payment);
        etRemainingTerm = view.findViewById(R.id.et_remaining_term);

        if (uid != null) {
            Log.d("MortgageFragment", "UID: " + uid);
            loadMortgageAccountInfo(uid);
        }

        return view;
    }

    private void loadMortgageAccountInfo(String uid) {
        DatabaseReference ref = FirebaseDatabase.getInstance(
                        "https://finalprojectandroid-ab72b-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("customers")
                .child(uid)
                .child("mortgageAccount");

        Log.d("MortgageFragment", "Đường dẫn Firebase: /customers/" + uid + "/mortgageAccount");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("MortgageFragment", "snapshot.exists() = " + snapshot.exists());
                Log.d("MortgageFragment", "snapshot value = " + snapshot.getValue());
                if (snapshot.exists()) {
                    String accountNumber = snapshot.child("accountNumber").getValue(String.class);
                    Object interestRate = snapshot.child("interestRate").getValue();
                    Object loanAmount = snapshot.child("loanAmount").getValue();
                    Object monthlyPayment = snapshot.child("monthlyPayment").getValue();
                    Object remainingTerm = snapshot.child("remainingTermMonths").getValue();

                    etAccount.setText(accountNumber != null ? accountNumber : "");
                    etInterestRate.setText(interestRate != null ? interestRate.toString() : "");
                    etLoanAmount.setText(loanAmount != null ? loanAmount.toString() : "");
                    etMonthlyPayment.setText(monthlyPayment != null ? monthlyPayment.toString() : "");
                    etRemainingTerm.setText(remainingTerm != null ? remainingTerm.toString() : "");
                } else {
                    Toast.makeText(getContext(), "Không tìm thấy tài khoản thế chấp", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
