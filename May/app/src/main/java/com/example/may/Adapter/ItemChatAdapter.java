package com.example.may.Adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.may.Interface.iClickListener;
import com.example.may.Model.ItemChat;
import com.example.may.R;

import java.util.List;

/**
 * Created by Nguyễn Phúc Thịnh on 5/11/2022.
 * Đại học công nghiệp TP HCM
 * nguyenthinhc9@gmail.com
 */
public class ItemChatAdapter extends RecyclerView.Adapter<ItemChatAdapter.itemViewHolder>{
    List<ItemChat> arrItem;
    private iClickListener clickListener;

    public ItemChatAdapter(List<ItemChat> arrItem, iClickListener clickListener) {
        this.arrItem = arrItem;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public itemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_type, parent, false);
        return new itemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull itemViewHolder holder, int position) {
        ItemChat itemChat =arrItem.get(position);

        holder.itemName.setText(itemChat.getItemName());
        holder.imgItem.setImageResource(itemChat.getImgItem());

        holder.layoutItem.setOnClickListener(view -> {
            clickListener.clickItem(itemChat);
        });
    }

    @Override
    public int getItemCount() {
        return arrItem.size();
    }

    static class itemViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgItem;
        private TextView itemName;
        private LinearLayout layoutItem;
        public itemViewHolder(@NonNull View itemView) {
            super(itemView);

            imgItem = itemView.findViewById(R.id.img_item);
            itemName = itemView.findViewById(R.id.item_name);
            layoutItem = itemView.findViewById(R.id.layout_item);
        }
    }
}
