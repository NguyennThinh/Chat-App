package com.example.may.Fragment.Group;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.may.Adapter.GroupWorkAdapter;
import com.example.may.Model.Group;
import com.example.may.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class GroupWork extends Fragment {
    private View view;
    private RecyclerView listGroup;
    private List<Group> arrGroup;

    private List<Group> arrGroups;
    private EditText edtSearch;
    //Adapter
    private GroupWorkAdapter adapter;

    //Firebase
    private FirebaseUser mUser;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_group_work, container, false);
        iniUI();

        loadListGroup();

        edtSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                String s = edtSearch.getText().toString();
                arrGroups.clear();
                for (Group u : arrGroup){
                    if (u.getGroupName().toLowerCase().contains(s.toLowerCase())){
                        arrGroups.add(u);

                    }
                }
                adapter  = new GroupWorkAdapter(arrGroups);
                listGroup.setAdapter(adapter);
                return false;
            }
        });
        return view;
    }



    private void iniUI() {
        listGroup = view.findViewById(R.id.list_group);
        arrGroup = new ArrayList<>();
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        edtSearch = view.findViewById(R.id.search);
        arrGroups = new ArrayList<>();

        adapter = new GroupWorkAdapter(arrGroup);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);

         listGroup.setAdapter(adapter);
         listGroup.setLayoutManager(layoutManager);
         listGroup.addItemDecoration(itemDecoration);
    }
    private void loadListGroup() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrGroup.clear();
                for (DataSnapshot dn : snapshot.getChildren()){

                    if (dn.child("participant").child(mUser.getUid()).exists()){
                        Group group = dn.getValue(Group.class);
                        if (group.getType() == 1){
                            arrGroup.add(group);
                        }
                    }
                }
              adapter.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }
}