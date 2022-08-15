package com.example.may.Fragment.Employee;

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

import com.example.may.Adapter.EmployeeCompanyAdapter;
import com.example.may.Model.Department;
import com.example.may.Model.Participant;
import com.example.may.Model.User;
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


public class EmployeeDepartment extends Fragment {
    private View view;
    private RecyclerView listUser;
    private List<User> arrUser;
    private List<User> arrUsers;
    private EditText edtSearch;
    //Adapter
    private EmployeeCompanyAdapter adapter;
    //Firebase
    private FirebaseUser mUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_employee_department, container, false);
        initUI();
        loadUser();

        edtSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                String s = edtSearch.getText().toString();
                arrUsers.clear();
                for (User u : arrUser){
                    if (u.getFullName().toLowerCase().contains(s.toLowerCase())){
                        arrUsers.add(u);

                    }
                }
                adapter  = new EmployeeCompanyAdapter(arrUsers);
                listUser.setAdapter(adapter);
                return false;
            }
        });

        return view;
    }

    private void initUI() {
        listUser = view.findViewById(R.id.list_user);
        arrUser = new ArrayList<>();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        edtSearch = view.findViewById(R.id.search);
        arrUsers = new ArrayList<>();



        adapter = new EmployeeCompanyAdapter(arrUser);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);

        listUser.setAdapter(adapter);
        listUser.setLayoutManager(layoutManager);
        listUser.addItemDecoration(itemDecoration);
    }

    private void loadUser() {
      /*  DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Department");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrUser.clear();
                for (DataSnapshot dn : snapshot.getChildren()){
                    Department department = dn.getValue(Department.class);

                    dbRef.child(department.getId()).child("participant").child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                         if (snapshot.exists()){
                             dbRef.child(department.getId()).child("participant").addValueEventListener(new ValueEventListener() {
                                 @Override
                                 public void onDataChange(@NonNull DataSnapshot snapshot) {
                                     arrUser.clear();
                                     for (DataSnapshot dn: snapshot.getChildren()){
                                         Participant participant = dn.getValue(Participant.class);

                                         DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                                         databaseReference.child(participant.getId()).addValueEventListener(new ValueEventListener() {
                                             @Override
                                             public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                 User user = snapshot.getValue(User.class);
                                                 if (!user.getId().equals(mUser.getUid())){
                                                     arrUser.add(user);
                                                 }

                                                 adapter.notifyDataSetChanged();
                                             }

                                             @Override
                                             public void onCancelled(@NonNull DatabaseError error) {

                                             }
                                         });
                                     }
                                 }

                                 @Override
                                 public void onCancelled(@NonNull DatabaseError error) {

                                 }
                             });
                         }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(mUser.getUid()).child("department").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Department");
                    dbRef.child(snapshot.getValue().toString()).child("participant").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            arrUser.clear();
                            for (DataSnapshot dn : snapshot.getChildren()) {
                                Participant participant = dn.getValue(Participant.class);
                                if (!participant.getId().equals(mUser.getUid())) {
                                    databaseReference.child(participant.getId()).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            User user = snapshot.getValue(User.class);
                                            arrUser.add(user);
                                            adapter.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}