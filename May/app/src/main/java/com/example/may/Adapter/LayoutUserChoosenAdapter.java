package com.example.may.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.may.Model.User;

import com.example.may.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class LayoutUserChoosenAdapter extends RecyclerView.Adapter<LayoutUserChoosenAdapter.ListUserSelect>{
    List<User> arrUsers;

    public LayoutUserChoosenAdapter(List<User> arrUsers) {
        this.arrUsers = arrUsers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ListUserSelect onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_choosen, parent, false);
        return new ListUserSelect(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListUserSelect holder, int position) {
        User users = arrUsers.get(position);

        if (users.getAvatar().equals("default")){
            holder.imgAvatar.setImageResource(R.drawable.ic_logo);
        }else {
            Picasso.get().load(users.getAvatar()).into(holder.imgAvatar);
        }
    }

    @Override
    public int getItemCount() {
        return arrUsers.size();
    }

    class ListUserSelect extends RecyclerView.ViewHolder {
        private CircleImageView imgAvatar;
        public ListUserSelect(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.user_select);

        }
    }
}
