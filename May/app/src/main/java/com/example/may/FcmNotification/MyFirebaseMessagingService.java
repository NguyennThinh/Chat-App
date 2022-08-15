package com.example.may.FcmNotification;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.may.Model.UserChat;
import com.example.may.R;
import com.example.may.Utilities.Constants;


import com.example.may.Utilities.MyApplication;
import com.example.may.View.GroupChatActivity;
import com.example.may.View.UserChatActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String status = remoteMessage.getData().get(Constants.REMOTE_MSG_STATUS);
        if (status != null) {
            String msg = remoteMessage.getData().get(Constants.KEY_MESSAGE);
            String name = remoteMessage.getData().get(Constants.KEY_NAME);
            String avatar = remoteMessage.getData().get(Constants.KEY_AVATAR);
            String id = remoteMessage.getData().get(Constants.KEY_ID);
            String item = remoteMessage.getData().get(Constants.KEY_ITEM);
            int i = Integer.parseInt(id.replaceAll("[\\D]", ""));
            if (status.equals("false")) {

                if (item.equals("Single")) {

                    Intent intent = new Intent(this, UserChatActivity.class);
                    intent.putExtra("id_user", id);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

                    notification(pendingIntent, i, msg, name, avatar, id);
                } else if (item.equals("Group")) {

                    Intent intent = new Intent(this, GroupChatActivity.class);
                    intent.putExtra("group", id);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);


                    notification(pendingIntent, i, msg, name, avatar, id);
                }


                super.onMessageReceived(remoteMessage);

            } else if (status.equals("remind_work")) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MyApplication.CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_message)
                        .setColor(Color.BLUE)
                        .setContentTitle(name)
                        .setContentText(name + " vừa nhắc bạn làm công việc: " + msg)
                        .setLargeIcon(getBitmapFromURL(avatar))
                        .setWhen(System.currentTimeMillis())
                        .setShowWhen(true);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

                int j = 0;
                if (i > 0) {
                    j = i;
                }
                notificationManager.notify((int) (j + System.currentTimeMillis()), builder.build());
                super.onMessageReceived(remoteMessage);
            }else if (status.equals("create_work")){
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MyApplication.CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_message)
                        .setColor(Color.BLUE)
                        .setContentTitle(name)
                        .setContentText(name + " vừa tạo cho bạn công việc: " + msg)
                        .setLargeIcon(getBitmapFromURL(avatar))
                        .setWhen(System.currentTimeMillis())
                        .setShowWhen(true);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

                int j = 0;
                if (i > 0) {
                    j = i;
                }
                notificationManager.notify((int) (j + System.currentTimeMillis()), builder.build());
                super.onMessageReceived(remoteMessage);
            }

            else {
                System.out.println(status);
                Log.d("API", "ERROR");
            }
        }
    }

    private void notification(PendingIntent pendingIntent, int i, String msg, String name, String avatar, String id) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MyApplication.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_message)
                .setColor(Color.BLUE)
                .setContentTitle(name)
                .setContentText(msg)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setLargeIcon(getBitmapFromURL(avatar))
                .setWhen(System.currentTimeMillis())
                .setShowWhen(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        int j = 0;
        if (i > 0) {
            j = i;
        }

        notificationManager.notify(j, builder.build());
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }
}
