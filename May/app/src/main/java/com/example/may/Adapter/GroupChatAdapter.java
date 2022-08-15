package com.example.may.Adapter;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.may.Model.CompanyChat;
import com.example.may.Model.GroupChat;
import com.example.may.Model.User;
import com.example.may.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChatAdapter extends RecyclerView.Adapter<GroupChatAdapter.ChatViewHolder> {

    List<GroupChat> arrMessage;
    String id;

    FirebaseAuth mAuth;
    FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    public GroupChatAdapter(List<GroupChat> arrMessage) {
        this.arrMessage = arrMessage;

    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == MSG_TYPE_LEFT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_group_left, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_group_right, parent, false);
        }
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        GroupChat message = arrMessage.get(position);
        setUserName(message, holder);


        holder.messageSendTime.setText(message.getTimeSend());

        String messageType = message.getType();
        if (messageType.equals("text")) {
            holder.messageText.setVisibility(View.VISIBLE);
            holder.messageImage.setVisibility(View.GONE);
            holder.messageVideo.setVisibility(View.GONE);
            holder.layoutMessageFile.setVisibility(View.GONE);

            holder.messageText.setText(message.getMessage());
        } else if (messageType.equals("image")) {
            holder.messageText.setVisibility(View.GONE);
            holder.messageImage.setVisibility(View.VISIBLE);
            holder.messageVideo.setVisibility(View.GONE);
            holder.layoutMessageFile.setVisibility(View.GONE);

            Picasso.get().load(message.getMessage()).into(holder.messageImage);

        } else if (messageType.equals("video")) {
            holder.messageText.setVisibility(View.GONE);
            holder.messageImage.setVisibility(View.GONE);
            holder.messageVideo.setVisibility(View.VISIBLE);
            holder.layoutMessageFile.setVisibility(View.GONE);

            holder.messageVideo.setVideoURI(Uri.parse(message.getMessage()));
//            MediaController mediaController = new MediaController(holder.itemView.getContext());
//            holder.messageVideo.setMediaController(mediaController);
//            mediaController.setAnchorView(holder.messageVideo);
        } else {
            holder.messageText.setVisibility(View.GONE);
            holder.messageImage.setVisibility(View.GONE);
            holder.messageVideo.setVisibility(View.GONE);
            holder.layoutMessageFile.setVisibility(View.VISIBLE);

            holder.fileName.setText(message.getFileName());
            long size = Long.parseLong(message.getFileSize());
            holder.fileSize.setText(getFileSize(size));

            String filenameArray[] = message.getFileName().split("\\.");
            String extension = filenameArray[filenameArray.length-1];

            if (extension.equals("doc")||extension.equals("docx")){
                holder.imgFile.setImageResource(R.drawable.word);
            }else if (extension.equals("xls")||extension.equals("xlsx")){
                holder.imgFile.setImageResource(R.drawable.excel);
            }else {
                holder.imgFile.setImageResource(R.drawable.ic_pdf_message);
            }

        }

        holder.layoutMessageFile.setOnClickListener(v -> {
            downloadFile(message, v);
        });
        if (position == arrMessage.size() - 1) {
            if (message.isSeen()) {
                holder.messageStatus.setText("Đã xem");
            } else {
                holder.messageStatus.setText("Đã nhân");

            }
        } else {
            holder.messageStatus.setVisibility(View.GONE);
        }

        holder.messageText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                DeleteMessage(v, message);
                return false;
            }
        });
        holder.messageImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                DeleteMessage(v, message);
                return false;
            }
        });
        holder.messageVideo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                DeleteMessage(v, message);
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrMessage.size();
    }

    @Override
    public int getItemViewType(int position) {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        GroupChat message = arrMessage.get(position);

        if (message != null && message.getIdSender().equals(user.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView imgUserSender;
        private TextView messageText, messageSendTime, messageStatus, fileName, fileSize, senderName;
        private ImageView messageImage, imgFile;
        private VideoView messageVideo;
        private RelativeLayout layoutMessageFile;

        public ChatViewHolder(View itemView) {
            super(itemView);

            imgUserSender = itemView.findViewById(R.id.imgUserSender);
            messageText = itemView.findViewById(R.id.message_text);
            messageSendTime = itemView.findViewById(R.id.time_send);
            messageStatus = itemView.findViewById(R.id.message_status);
            fileName = itemView.findViewById(R.id.file_name);
            fileSize = itemView.findViewById(R.id.file_size);
            messageImage = itemView.findViewById(R.id.message_image);
            messageVideo = itemView.findViewById(R.id.message_video);
            layoutMessageFile = itemView.findViewById(R.id.layout_message_file);
            senderName = itemView.findViewById(R.id.user_sender_name);
            imgFile = itemView.findViewById(R.id.img_file);

        }


    }

    public static String getFileSize(long size) {
        if (size <= 0)
            return "0";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    private void setUserName(GroupChat message, ChatViewHolder holder) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.orderByChild("id").equalTo(message.getIdSender())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dn : snapshot.getChildren()) {
                            User users = dn.getValue(User.class);
                            if (users != null) {
                                holder.senderName.setText(users.getFullName());
                                if (users.getAvatar().equals("default")) {
                                    holder.imgUserSender.setImageResource(R.drawable.ic_launcher_background);
                                } else {
                                    Picasso.get().load(users.getAvatar()).into(holder.imgUserSender);
                                }
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void DeleteMessage(View view, GroupChat message) {
        AlertDialog.Builder  builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Xóa tin nhắn");
        builder.setMessage("Bạn muốn xóa tin nhắn");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
                Query query = databaseReference.child(id).child("Message").orderByChild("idMessage").equalTo(message.getIdMessage());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dn : snapshot.getChildren()){
                            if (dn.child("idSender").getValue().equals(mUser.getUid())){
                                //    dn.getRef().removeValue();
                                if (dn.child("type").getValue().equals("text")){
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("message","Tin nhắn này đã bị xóa");
                                    dn.getRef().updateChildren(hashMap);

                                }else {
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("message","Tin nhắn này đã bị xóa");
                                    hashMap.put("type","text");
                                    dn.getRef().updateChildren(hashMap);
                                }
                            }else {
                                Toast.makeText(view.getContext(), "Bạn không thể xóa tin nhắn này", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void downloadFile(GroupChat message, View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setTitle("Tải xuống");
        builder.setMessage("Bạn muốn tải file?");

        builder.setPositiveButton("Tải xuống", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String filename[] = message.getFileName().split("\\.");

                DownloadManager.Request  request = new DownloadManager.Request(Uri.parse(message.getMessage()));
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                request.setTitle(message.getFileName());
                request.setDescription("Đang tải...");

                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename[0]);

                DownloadManager downloadManager = (DownloadManager) v.getContext().getSystemService(Context.DOWNLOAD_SERVICE);
                if (downloadManager != null){
                    downloadManager.enqueue(request);
                }
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();

    }
}
