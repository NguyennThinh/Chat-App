package com.example.may.AdapterEmployee;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.may.Fragment.Employee.EmployeeFragment;
import com.example.may.Fragment.Group.GroupFragment;
import com.example.may.Fragment.MessageFragment;

/**
 * Created by Nguyễn Phúc Thịnh on 5/10/2022.
 * Đại học công nghiệp TP HCM
 * nguyenthinhc9@gmail.com
 */
public class MainEmployeeViewAdapter extends FragmentStateAdapter {
    public MainEmployeeViewAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new MessageFragment();
            case 1:
                return  new EmployeeFragment();
            case 2:
                return new GroupFragment();
            default:
                return  new MessageFragment();
        }

    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
