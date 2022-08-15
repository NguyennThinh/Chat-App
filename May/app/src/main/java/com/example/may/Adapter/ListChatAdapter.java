package com.example.may.Adapter;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.may.Model.ChatList;
import com.example.may.Model.Group;
import com.example.may.Model.GroupChat;
import com.example.may.Model.User;
import com.example.may.Model.UserChat;
import com.example.may.R;
import com.example.may.View.GroupChatActivity;
import com.example.may.View.UserChatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static int USER_CHAT_LIST = 1;
    private static int GROUP_CHAT_LIST = 0;

    private List<ChatList> arrListChat;
    private List<User> arrUser = new ArrayList<>();
    private List<Group> arrGroup = new ArrayList<>();

    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

    public ListChatAdapter(List<ChatList> arrListChat) {
        this.arrListChat = arrListChat;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_chat, parent, false);
        if (USER_CHAT_LIST ==viewType){
            return new ListUserChatViewHolder(view);
        }else {
            return new ListGroupChatViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatList list = arrListChat.get(position);
        if (USER_CHAT_LIST == holder.getItemViewType()){
            ListUserChatViewHolder userChatListViewHolder = (ListUserChatViewHolder) holder;
            getUserChatList(userChatListViewHolder, list);
        }else if (GROUP_CHAT_LIST == holder.getItemViewType()){
            ListGroupChatViewHolder  groupChatListViewHolder = (ListGroupChatViewHolder) holder;
            getGroupChatList(groupChatListViewHolder, list);
        }
    }

    @Override
    public int getItemViewType(int position) {

        if (arrListChat.size() !=0){
            ChatList chatList = arrListChat.get(position);
            if (chatList != null){
                if (chatList.getType()==1){
                    return USER_CHAT_LIST;
                }else {
                    return GROUP_CHAT_LIST;
                }
            }
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return arrListChat.size();
    }

    static class ListUserChatViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout layoutListChat;
        TextView listChatStatus,listChatName, lisTChatMessage;
        ImageView ImageListChatStatus, imgSeen;
        CircleImageView imageListChat;

        public ListUserChatViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutListChat = itemView.findViewById(R.id.layout_list_chat);
            listChatStatus = itemView.findViewById(R.id.list_chat_status);
            listChatName = itemView.findViewById(R.id.name_list_chat_item);
            lisTChatMessage = itemView.findViewById(R.id.message_list_chat_item);
            ImageListChatStatus = itemView.findViewById(R.id.img_list_chat_status);
            imageListChat = itemView.findViewById(R.id.img_list_chat_avatar);
            imgSeen = itemView.findViewById(R.id.img_message_seen);
        }
    }
    static class ListGroupChatViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout layoutListChat;
        TextView listChatStatus,listChatName, lisTChatMessage;
        ImageView ImageListChatStatus,imgSeen;
        CircleImageView imageListChat;

        public ListGroupChatViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutListChat = itemView.findViewById(R.id.layout_list_chat);
            listChatStatus = itemView.findViewById(R.id.list_chat_status);
            listChatName = itemView.findViewById(R.id.name_list_chat_item);
            lisTChatMessage = itemView.findViewById(R.id.message_list_chat_item);
            ImageListChatStatus = itemView.findViewById(R.id.img_list_chat_status);
            imageListChat = itemView.findViewById(R.id.img_list_chat_avatar);
            imgSeen = itemView.findViewById(R.id.img_message_seen);
        }
    }
    private void getUserChatList(ListUserChatViewHolder holder, ChatList list) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                for (DataSnapshot dn : snapshot.getChildren()){

                    User user = dn.getValue(User.class);

                      if (user != null){
                        if (list.getId().equals(user.getId())){
                            holder.listChatName.setText(user.getFullName());
                            
                            if (user.getAvatar().equals("default")){
                                holder.imageListChat.setImageResource(R.drawable.ic_logo);
                            }else {
                                Picasso.get().load(user.getAvatar()).into(holder.imageListChat);
                            }
                          
                            if (user.getStatus()==1){
                                holder.ImageListChatStatus.setImageResource(R.drawable.ic_online);
                                holder.listChatStatus.setText("Đang hoạt động");
                            }else {
                                holder.ImageListChatStatus.setImageResource(R.drawable.ic_offline);
                                holder.listChatStatus.setText("Không hoạt động");
                            }
                            
                            
                            arrUser.add(user);

                            holder.layoutListChat.setOnClickListener(view->{
                                Intent intent = new Intent(view.getContext(), UserChatActivity.class);


                                intent.putExtra("id_user", user.getId());
                                view.getContext().startActivity(intent);
                            });
                        }

                      }

                        for (int i =0; i < arrUser.size(); i++){
                            getLastMessage(arrUser.get(i).getId(), holder);
                        }
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getLastMessage(String id, ListUserChatViewHolder holder) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Message");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dn: snapshot.getChildren()){
                    UserChat message = dn.getValue(UserChat.class);
                    if (message.getIdReceiver().equals(mUser.getUid()) && message.getIdSender().equals(id)
                            || message.getIdReceiver().equals(id) && message.getIdSender().equals(mUser.getUid())) {

                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Users");
                        databaseReference1.orderByChild("id").equalTo(message.getIdSender())
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String type = "text";
                                        String lastMessage = "default";
                                        String name = "default";
                                        boolean status;
                                        for (DataSnapshot dn : snapshot.getChildren()){
                                            name = dn.child("fullName").getValue()+"";

                                        }

                                        type = message.getType();
                                        lastMessage = message.getMessage();
                                        status = message.isSeen();
                                        if (  lastMessage.equals("default") || lastMessage== null){
                                            holder.lisTChatMessage.setVisibility(View.GONE);
                                        }else {
                                            if (mUser.getUid().equals(message.getIdSender())){

                                                setLastMessageUser(holder, type, lastMessage, "Bạn" );
                                            }else {
                                                if (!status){
                                                    holder.imgSeen.setImageResource(R.drawable.ic_new_message);
                                                    holder.ImageListChatStatus.setVisibility(View.GONE);
                                                    holder.listChatStatus.setVisibility(View.GONE);
                                                }
                                                setLastMessageUser(holder, type, lastMessage, name);
                                            }
                                        }
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
    private void setLastMessageUser(ListUserChatViewHolder holder, String type, String lastMessage, String sender) {
        if (type.equals("image")){
            holder.lisTChatMessage.setText(sender+ ": "+"Đã gửi hình ảnh");
        }else if (type.equals("video")){
            holder.lisTChatMessage.setText(sender+ ": "+"Đã gửi video");
        }else if (type.equals("pdf")){
            holder.lisTChatMessage.setText(sender+ ": "+"Đã gửi file");
        }else {
            holder.lisTChatMessage.setText(sender+ ": "+lastMessage);
        }
    }

    private void getGroupChatList(ListGroupChatViewHolder holder, ChatList list) {
        DatabaseReference  databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
        databaseReference.child(list.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {



                    Group group = snapshot.getValue(Group.class);
                    if (group != null){

                            holder.listChatName.setText(group.getGroupName());
                            Picasso.get().load(group.getGroupImage()).into(holder.imageListChat);
                            holder.listChatStatus.setVisibility(View.GONE);
                            holder.ImageListChatStatus.setVisibility(View.GONE);
                            arrGroup.add(group);

                            holder.layoutListChat.setOnClickListener(view->{
                                Intent intent = new Intent(view.getContext(), GroupChatActivity.class);
                                Bundle bundle = new Bundle();

                                intent.putExtra("id_group", group.getGroupID());
                                view.getContext().startActivity(intent);
                            });
                        }


                    for (int i =0; i < arrGroup.size(); i++){
                        getLastMessageGroup(arrGroup.get(i).getGroupID(), holder);
                    }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getLastMessageGroup(String groupID, ListGroupChatViewHolder holder) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
        databaseReference.child(groupID).child("Message").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                for (DataSnapshot dn : snapshot.getChildren()){
                    GroupChat message = dn.getValue(GroupChat.class);


                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Users");
                    databaseReference1.orderByChild("id").equalTo(message.getIdSender())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String name = "default";
                                    String type = "default";
                                    String last_message = "default";
                                    for (DataSnapshot dn : snapshot.getChildren()){
                                        name = dn.child("fullName").getValue()+"";

                                    }
                                   type = message.getType();
                                    last_message = message.getMessage();
                                    if (  last_message.equals("default") || last_message== null){
                                        holder.lisTChatMessage.setVisibility(View.GONE);
                                    }else {
                                        if (mUser.getUid().equals(message.getIdSender())){

                                            setLastMessageUserGroup(holder,type,last_message,"Bạn" );
                                         }else {
                                            if (!message.isSeen()){
                                                holder.imgSeen.setImageResource(R.drawable.ic_new_message);
                                                holder.ImageListChatStatus.setVisibility(View.GONE);
                                                holder.listChatStatus.setVisibility(View.GONE);
                                            }
                                            setLastMessageUserGroup(holder,type,last_message,name );

                                        }
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
    private void setLastMessageUserGroup(ListGroupChatViewHolder holder, String type, String lastMessage, String sender) {
        if (type.equals("image")){
            holder.lisTChatMessage.setText(sender+ ": "+"Đã gửi hình ảnh");
        }else if (type.equals("video")){
            holder.lisTChatMessage.setText(sender+ ": "+"Đã gửi video");
        }else if (type.equals("pdf")){
            holder.lisTChatMessage.setText(sender+ ": "+"Đã gửi file");
        }else {
            holder.lisTChatMessage.setText(sender+ ": "+lastMessage);
        }
    }
}
