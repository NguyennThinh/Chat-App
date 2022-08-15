package com.example.may.AdapterEmployee;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.may.Fragment.Group.GroupDepartment;
import com.example.may.Fragment.Group.GroupWork;

/**
 * Created by Nguyễn Phúc Thịnh on 5/10/2022.
 * Đại học công nghiệp TP HCM
 * nguyenthinhc9@gmail.com
 */
public class TabLayoutGroupAdapter extends FragmentStateAdapter {
    public TabLayoutGroupAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new GroupWork();

            case 1:
                return new GroupDepartment();

            default:
                return new GroupWork();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
