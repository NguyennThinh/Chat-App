package com.example.may.Fragment.Employee;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.may.AdapterEmployee.TabLayoutEmployeeAdapter;
import com.example.may.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class EmployeeFragment extends Fragment {
    View view;
    private TabLayout tabWork;
    private ViewPager2 viewListWork;
    private TabLayoutEmployeeAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =   inflater.inflate(R.layout.fragment_employee, container, false);

        initUI();

        new TabLayoutMediator(tabWork, viewListWork, (tab, position) -> {
            switch (position){
                case 0:
                    tab.setText("Nhân viên phòng ban");

                    break;
                case 1:
                    tab.setText("Nhân viên công ty");

                    break;
            }
        }).attach();

        return  view;
    }
    private void initUI() {

        tabWork = view.findViewById(R.id.tab_work_status);
        viewListWork = view.findViewById(R.id.view_list_work);

        adapter = new TabLayoutEmployeeAdapter(getActivity());
        viewListWork.setAdapter(adapter);
    }
}