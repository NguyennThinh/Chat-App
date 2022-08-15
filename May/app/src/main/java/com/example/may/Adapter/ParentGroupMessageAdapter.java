package com.example.may.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.may.Model.GroupChatParent;
import com.example.may.Model.UserChatParent;
import com.example.may.R;

import java.util.List;

public class ParentGroupMessageAdapter extends RecyclerView
        .Adapter<ParentGroupMessageAdapter.ParentViewHolder> {
    private List<GroupChatParent> groupChatParents;
    private RecyclerView.RecycledViewPool
            viewPool
            = new RecyclerView
            .RecycledViewPool();
    public ParentGroupMessageAdapter(List<GroupChatParent> groupChatParents) {
        this.groupChatParents = groupChatParents;
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
        GroupChatParent chatParent = groupChatParents.get(position);

        holder.dateSend.setText(chatParent.getDateSend());
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(
                holder.itemView.getContext());
        layoutManager.setInitialPrefetchItemCount(chatParent.getArrGroupChat().size());

        GroupChatAdapter adapter
                = new GroupChatAdapter(chatParent.getArrGroupChat());
        holder.listMessage.setLayoutManager(layoutManager);
        holder.listMessage
                .setAdapter(adapter);
        holder.listMessage
                .setRecycledViewPool(viewPool);

    }

    @Override
    public int getItemCount() {
        return groupChatParents.size();
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
