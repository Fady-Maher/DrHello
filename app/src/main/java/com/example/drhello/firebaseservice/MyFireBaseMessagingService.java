package com.example.drhello.firebaseservice;


import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.drhello.ui.chats.AddPersonActivity;
import com.example.drhello.R;
import com.example.drhello.ui.chats.ChatActivity;
import com.example.drhello.ui.main.MainActivity;
import com.example.drhello.ui.writecomment.WriteCommentActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;

public class MyFireBaseMessagingService extends FirebaseMessagingService {

    public static final String CHANEL_ID = "Chanel_id2";
    public static final String CHANEL_NAME = "Chanel_name2";
    public static final String CHANEL_DESC = "Chanel_description2";
    NotificationManager mNotificationManager;

    @Override
    public void onNewToken(@NonNull String token)
    {
        Log.e("token_name:",token);
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


        if(remoteMessage.getData()!=null){

            String title = remoteMessage.getData().get("title");
            String body = remoteMessage.getData().get("body");
            String postID = remoteMessage.getData().get("color");
            String imageUser = remoteMessage.getData().get("icon");
            String idUser = remoteMessage.getData().get("idUser");

            Log.e("POST ID : ",title + postID  + body);

            if(!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(idUser) && !isForeground(MyFireBaseMessagingService.this)){
                Log.e("FirebaseAuth : ","FirebaseAuth");
                createNotification(title,body,postID,imageUser);
            }
        }

    }

    private void createNotification(String title, String body,String postID,String imageUser) {

        NotificationManager manager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            // only active for android o and higher because it need NotificationChannel
            @SuppressLint("WrongConstant")
            NotificationChannel channel = new NotificationChannel(CHANEL_ID,CHANEL_NAME,NotificationManager.IMPORTANCE_MAX);

            // configure the notification channel
            channel.setDescription(CHANEL_DESC);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.setVibrationPattern(new long[]{0,1000,500,1000});
            channel.enableVibration(true);
            manager.createNotificationChannel(channel);
        }


        Glide.with(this)
                .asBitmap()
                .load(imageUser)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Intent intent = null;
                        if(title.equals("Post")){
                            intent= new Intent(MyFireBaseMessagingService.this, MainActivity.class);
                            intent.putExtra("post","post");
                            Log.e("POST : ",postID  + "MainActivity");
                        }else if(title.equals("Comment")){
                            intent= new Intent(MyFireBaseMessagingService.this, WriteCommentActivity.class);
                            intent.putExtra("postID",postID);

                            Log.e("POST ID : ",postID  + "POST");
                        }else if(title.equals("Message")){
                            intent= new Intent(MyFireBaseMessagingService.this, ChatActivity.class);
                            intent.putExtra("chatchannel",postID);
                            Log.e("POST : ",postID  + "MainActivity");
                        }else if(title.equals("Request")){
                            intent= new Intent(MyFireBaseMessagingService.this, AddPersonActivity.class);
                            Log.e("POST : ",postID  + "MainActivity");
                        }else if(title.equals("Accept")){
                            intent= new Intent(MyFireBaseMessagingService.this, MainActivity.class);
                            Log.e("POST : ",postID  + "MainActivity");
                        }


                            assert intent != null;
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            @SuppressLint("UnspecifiedImmutableFlag")
                            PendingIntent pendingIntent = PendingIntent.getActivity(MyFireBaseMessagingService.this,
                                    0,intent,0);

                            NotificationCompat.Builder builder = new NotificationCompat.Builder(MyFireBaseMessagingService.this,CHANEL_ID);

                            builder.setSmallIcon(R.drawable.ic_boydrcare)
                                    .setLargeIcon(resource)
                                    .setContentTitle(title)
                                    .setContentText(body)
                                    .setContentIntent(pendingIntent)
                                    .setAutoCancel(true);

                            manager.notify(0,builder.build());

                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }



    private static boolean isForeground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : tasks) {
            if (ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND == appProcess.importance && packageName.equals(appProcess.processName)) {
                return true;
            }
        }
        return false;
    }




}

