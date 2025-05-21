package com.end.finalproject.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.end.finalproject.fragment.TaiKhoanManagementFragment;
import com.end.finalproject.fragment.TaiKhoanThanhToanManagementFragment;
import com.end.finalproject.fragment.TaiKhoanTietKiemManagementFragment;
import com.end.finalproject.fragment.TaiKhoanTheChapManagementFragment;

public class TabPagerManagementAdapter extends FragmentStateAdapter {
    private final String uid, email, phoneNumber;

    public TabPagerManagementAdapter(@NonNull FragmentActivity fa,
                                     String uid,
                                     String email,
                                     String phoneNumber) {
        super(fa);
        this.uid         = uid;
        this.email       = email;
        this.phoneNumber = phoneNumber;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return TaiKhoanThanhToanManagementFragment.newInstance(uid);
            case 2:
                return TaiKhoanTietKiemManagementFragment.newInstance(uid);
            case 3:
                return TaiKhoanTheChapManagementFragment.newInstance(uid);
            default:
                return TaiKhoanManagementFragment.newInstance(uid);
        }
    }

    @Override
    public int getItemCount() { return 4; }
}
