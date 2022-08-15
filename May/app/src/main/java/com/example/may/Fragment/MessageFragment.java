package com.example.may.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.may.Adapter.ListChatAdapter;
import com.example.may.Model.ChatList;
import com.example.may.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class MessageFragment extends Fragment {
    //Views
    RecyclerView list_chat;
    private View view;

    //Adapter
    ListChatAdapter adpter;
    //Declare Firebase
    FirebaseUser mUser;

    //Declare list
    List<ChatList> arrListIdChat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_message, container, false);
        initUI(view);
        loadListChat();
        return view;
    }

    private void initUI(View view) {
        list_chat = view.findViewById(R.id.display_list_chat);
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        arrListIdChat= new ArrayList<>();


        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);


        list_chat.setLayoutManager(layoutManager);
        list_chat.addItemDecoration(itemDecoration);
    }

    private void loadListChat() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ChatList").child(mUser.getUid());
        //get list id user đã chat
        Query query = databaseReference.orderByChild("timeSend");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrListIdChat.clear();
                for (DataSnapshot dn : snapshot.getChildren()) {
                    ChatList list = dn.getValue(ChatList.class);
                    arrListIdChat.add(0, list);
                }

                adpter =new ListChatAdapter(arrListIdChat);
                list_chat.setAdapter(adpter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}