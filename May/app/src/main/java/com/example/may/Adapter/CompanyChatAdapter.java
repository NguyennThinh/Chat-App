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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.may.Model.CompanyChat;
import com.example.may.Model.User;
import com.example.may.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

public class CompanyChatAdapter extends RecyclerView.Adapter<CompanyChatAdapter.CompanyViewHolder> {
    private List<CompanyChat> arrCompanyChats;

    public CompanyChatAdapter(List<CompanyChat> arrCompanyChats) {
        this.arrCompanyChats = arrCompanyChats;
    }

    @NonNull
    @Override
    public CompanyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_company_chat, parent, false);
        return new CompanyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompanyViewHolder holder, int position) {
        CompanyChat chat = arrCompanyChats.get(position);

        getUserName(holder, chat);
       if (chat.getType().equals("text")){
           holder.message.setVisibility(View.VISIBLE);
           holder.message.setText(chat.getMessage());
           holder.imgMessage.setVisibility(View.GONE);
           holder.videoMessage.setVisibility(View.GONE);
           holder.layoutMessage.setVisibility(View.GONE);
           holder.layout_file.setVisibility(View.GONE);

       }else if (chat.getType().equals("image")){
           holder.message.setVisibility(View.GONE);
           holder.layoutMessage.setVisibility(View.VISIBLE);

           holder.videoMessage.setVisibility(View.GONE);
           holder.imgMessage.setVisibility(View.VISIBLE);
           holder.layout_file.setVisibility(View.GONE);

           Picasso.get().load(chat.getMessage()).into(holder.imgMessage);

       }else if (chat.getType().equals("video")){
           holder.message.setVisibility(View.GONE);
           holder.videoMessage.setVisibility(View.VISIBLE);
           holder.imgMessage.setVisibility(View.GONE);
           holder.layoutMessage.setVisibility(View.VISIBLE);
           holder.layout_file.setVisibility(View.GONE);


           holder.videoMessage.setVideoURI(Uri.parse(chat.getMessage()));

       }else {
           holder.message.setVisibility(View.GONE);
           holder.videoMessage.setVisibility(View.VISIBLE);
           holder.imgMessage.setVisibility(View.GONE);
           holder.layoutMessage.setVisibility(View.GONE);
           holder.layout_file.setVisibility(View.VISIBLE);

           holder.fileName.setText(chat.getFileName());
           Long size = Long.parseLong(chat.getFileSize());
           holder.fileSize.setText(getFileSize(size));

           String filenameArray[] = chat.getFileName().split("\\.");
           String extension = filenameArray[filenameArray.length-1];

           if (extension.equals("doc")||extension.equals("docx")){
               holder.imgType.setImageResource(R.drawable.word);
           }else if (extension.equals("xls")||extension.equals("xlsx")){
               holder.imgType.setImageResource(R.drawable.excel);
           }else {
               holder.imgType.setImageResource(R.drawable.ic_pdf_message);
           }
       }

       holder.layout_file.setOnClickListener(v -> {
           downloadFile(chat, v);
       });
    }


    @Override
    public int getItemCount() {
        return arrCompanyChats.size();
    }

    static class CompanyViewHolder extends RecyclerView.ViewHolder {
        private TextView senderName, message;
        private ImageView imgMessage, imgType;
        private VideoView videoMessage;
        private LinearLayout layoutMessage;
        private RelativeLayout layout_file;
        private TextView fileName, fileSize;
        public CompanyViewHolder(@NonNull View itemView) {
            super(itemView);

            senderName = itemView.findViewById(R.id.user_sender_name);
            message = itemView.findViewById(R.id.message_text);
            imgMessage = itemView.findViewById(R.id.message_image);
            videoMessage = itemView.findViewById(R.id.message_video);
            layoutMessage = itemView.findViewById(R.id.layout_message);
            layout_file = itemView.findViewById(R.id.layout_message_file);
            fileName = itemView.findViewById(R.id.file_name);
            fileSize = itemView.findViewById(R.id.file_size);
            imgType = itemView.findViewById(R.id.img_type);
        }
    }
    private void getUserName(CompanyViewHolder holder, CompanyChat chat) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users");
        dbRef.child(chat.getIdSender()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    User user = snapshot.getValue(User.class);
                    holder.senderName.setText(user.getFullName()+": ");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public static String getFileSize(long size) {
        if (size <= 0)
            return "0";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }



    private void downloadFile(CompanyChat message, View v) {
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
