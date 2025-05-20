package com.end.finalproject;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class TabPagerAdapter extends FragmentStateAdapter {

    private String uid;
    private String email;
    private String phoneNumber;

    public TabPagerAdapter(@NonNull FragmentActivity fa, String uid, String email, String phoneNumber) {
        super(fa);
        this.uid = uid;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return TaiKhoanFragment.newInstance(uid, email, phoneNumber);
            case 1:
                return TaiKhoanThanhToanFragment.newInstance(uid);
            case 2:
                return TaiKhoanTietKiemFragment.newInstance(uid);
            case 3:
                return TaiKhoanTheChapFragment.newInstance(uid);
            default:
                return TaiKhoanFragment.newInstance(uid, email, phoneNumber);
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
