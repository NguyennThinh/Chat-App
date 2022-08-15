package com.example.may.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.may.Model.UserChatParent;
import com.example.may.R;

import java.util.List;

public class ParentUserMessageAdapter extends RecyclerView
        .Adapter<ParentUserMessageAdapter.ParentViewHolder> {
    private List<UserChatParent> arrMessageParent;
    private RecyclerView.RecycledViewPool
            viewPool
            = new RecyclerView
            .RecycledViewPool();
    public ParentUserMessageAdapter(List<UserChatParent> arrMessageParent) {
        this.arrMessageParent = arrMessageParent;
    }

    @NonNull
    @Override
    public ParentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_user_message_parent,
                        parent, false);
        return new ParentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParentViewHolder holder, int position) {
        UserChatParent chatParent = arrMessageParent.get(position);

        holder.dateSend.setText(chatParent.getDateSend());
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(
                holder.itemView.getContext());
        layoutManager.setInitialPrefetchItemCount(chatParent.getArrUserChat().size());

        UserChatApdapter adapter
                = new UserChatApdapter(chatParent.getArrUserChat());
        holder.listMessage.setLayoutManager(layoutManager);
        holder.listMessage
                .setAdapter(adapter);
        holder.listMessage
                .setRecycledViewPool(viewPool);

    }

    @Override
    public int getItemCount() {
        return arrMessageParent.size();
    }

    static class ParentViewHolder
            extends RecyclerView.ViewHolder {

        private TextView dateSend;
        private RecyclerView listMessage;

        ParentViewHolder(final View itemView) {
            super(itemView);

            dateSend
                    = itemView
                    .findViewById(
                            R.id.dateSend);
            listMessage
                    = itemView
                    .findViewById(
                            R.id.list_chat_parent);
        }
    }
}
