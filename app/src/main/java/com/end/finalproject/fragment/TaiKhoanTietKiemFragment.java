package com.end.finalproject.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class TaiKhoanTietKiemFragment extends Fragment {

    private String uid;
    private EditText etAccount, etBalance, etInterestRate, etTermMonths;

    public TaiKhoanTietKiemFragment() {}

    public static TaiKhoanTietKiemFragment newInstance(String uid) {
        TaiKhoanTietKiemFragment fragment = new TaiKhoanTietKiemFragment();
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

        View view = inflater.inflate(R.layout.fragment_tai_khoan_tiet_kiem, container, false);

        // Ánh xạ view
        etAccount = view.findViewById(R.id.et_saving_account);
        etBalance = view.findViewById(R.id.et_saving_balance);
        etInterestRate = view.findViewById(R.id.et_interest_rate);
        etTermMonths = view.findViewById(R.id.et_term_months);

        if (uid != null) {
            loadSavingAccountInfo(uid);
        }

        return view;
    }

    private void loadSavingAccountInfo(String uid) {
        DatabaseReference ref = FirebaseDatabase.getInstance(
                        "https://finalprojectandroid-ab72b-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("customers")
                .child(uid)
                .child("savingAccount");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String accountNumber = snapshot.child("accountNumber").getValue(String.class);
                    Object balance = snapshot.child("balance").getValue();
                    Object interestRate = snapshot.child("interestRate").getValue();
                    Object termMonths = snapshot.child("termMonths").getValue();

                    etAccount.setText(accountNumber != null ? accountNumber : "");
                    etBalance.setText(balance != null ? balance.toString() : "0");
                    etInterestRate.setText(interestRate != null ? interestRate.toString() : "0");
                    etTermMonths.setText(termMonths != null ? termMonths.toString() : "0");
                } else {
                    Toast.makeText(getContext(), "Không tìm thấy tài khoản tiết kiệm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
