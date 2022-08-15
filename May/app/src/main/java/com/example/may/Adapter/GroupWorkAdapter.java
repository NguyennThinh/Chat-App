package com.example.may.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.may.Model.Group;
import com.example.may.R;
import com.example.may.View.GroupChatActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Nguyễn Phúc Thịnh on 5/11/2022.
 * Đại học công nghiệp TP HCM
 * nguyenthinhc9@gmail.com
 */
public class GroupWorkAdapter extends RecyclerView.Adapter<GroupWorkAdapter.GroupViewHolder> {
    List<Group> arrGroup;

    public GroupWorkAdapter(List<Group> arrGroup) {
        this.arrGroup = arrGroup;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_group, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        Group group = arrGroup.get(position);
        if (group.getGroupImage().equals("default")){
            holder.imgGroup.setImageResource(R.drawable.ic_logo);
        }else {
            Picasso.get().load(group.getGroupImage()).into(holder.imgGroup);
        }
        holder.groupName.setText(group.getGroupName());
        holder.slParticipant.setText(group.getSlParticipant()+" Thành viên");
        holder.layoutGroup.setOnClickListener(view->{
         Intent intent = new Intent(view.getContext(), GroupChatActivity.class);

            intent.putExtra("id_group", group.getGroupID());


            view.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return arrGroup.size();
    }

    class GroupViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView imgGroup;
        private TextView groupName, slParticipant;
        private LinearLayout layoutGroup;
        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);

            imgGroup = itemView.findViewById(R.id.item_avatar_group);
            groupName = itemView.findViewById(R.id.item_name_group);
            layoutGroup = itemView.findViewById(R.id.layoutGroup);
            slParticipant = itemView.findViewById(R.id.item_qty_participant);
        }
    }
}
