package com.example.may.Fragment.Group;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.may.AdapterEmployee.TabLayoutGroupAdapter;
import com.example.may.R;
import com.example.may.View.CreateGroupDepartmentActivity;
import com.example.may.View.CreateGroupWorkActivity;
import com.example.may.ViewManager.GroupActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class GroupFragment extends Fragment {

    View view;
    private TabLayout tabWork;
    private ViewPager2 viewListWork;
    private TabLayoutGroupAdapter adapter;


    //View dialog
    private RadioButton radWork, radDepartment;
    private Button btnCancel, btnSubmit;

    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_group, container, false);

        initUI();

        new TabLayoutMediator(tabWork, viewListWork, (tab, position) -> {
            switch (position){
                case 0:
                    tab.setText("NNhóm công việc");

                    break;
                case 1:
                    tab.setText("Nhóm phòng ban");

                    break;
            }
        }).attach();

        return view;
    }

    private void initUI() {

        tabWork = view.findViewById(R.id.tab_work_status);
        viewListWork = view.findViewById(R.id.view_list_work);

        adapter = new TabLayoutGroupAdapter(getActivity());
        viewListWork.setAdapter(adapter);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_employee,menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.create_group:
                createGroup();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createGroup() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_option_create_grouo);
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams layoutParams = window.getAttributes();

        layoutParams.gravity = Gravity.CENTER;

        window.setAttributes(layoutParams);

        //init views
        radDepartment = dialog.findViewById(R.id.radDepartment);
        radWork = dialog.findViewById(R.id.radWork);
        btnCancel = dialog.findViewById(R.id.btnCancel);
        btnSubmit = dialog.findViewById(R.id.btnSubmit);


        btnSubmit.setOnClickListener(v ->{
            if (radWork.isChecked()){
                startActivity(new Intent(getActivity(), CreateGroupWorkActivity.class));
            }else {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                databaseReference.child(mUser.getUid()).child("department").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            startActivity(new Intent(getActivity(), CreateGroupDepartmentActivity.class));

                        }else {
                            Toast.makeText(getContext(), "Bạn không có phòng ban để tạo nhóm", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v->{
            dialog.cancel();
        });

        dialog.show();
    }
}