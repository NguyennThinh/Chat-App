package com.example.may.AdapterEmployee;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.may.Fragment.Employee.EmployeeCompany;
import com.example.may.Fragment.Employee.EmployeeDepartment;

/**
 * Created by Nguyễn Phúc Thịnh on 5/10/2022.
 * Đại học công nghiệp TP HCM
 * nguyenthinhc9@gmail.com
 */
public class TabLayoutEmployeeAdapter extends FragmentStateAdapter {
    public TabLayoutEmployeeAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new EmployeeDepartment();

            case 1:
                return new EmployeeCompany();

            default:
                return new EmployeeDepartment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
