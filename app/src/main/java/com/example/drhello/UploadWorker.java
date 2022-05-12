package com.example.drhello;

import android.annotation.SuppressLint;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.drhello.firebaseservice.MyFireBaseMessagingService;
import com.example.drhello.ui.chats.AddPersonActivity;
import com.example.drhello.ui.chats.ChatActivity;
import com.example.drhello.ui.hardware.Hardware;
import com.example.drhello.ui.main.MainActivity;
import com.example.drhello.ui.writecomment.WriteCommentActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UploadWorker extends Worker {
    private Context context;
    public UploadWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
    }

    @Override
    public Result doWork() {

        // Do the work here--in this case, upload the images.
     //   uploadImages();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Hardware hardware = dataSnapshot.getValue(Hardware.class);
                createNotification(hardware.getHeart_Rate().toString(),hardware.getHeart_Rate().toString());
                Toast.makeText(context, hardware.getHeart_Rate().toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                // Log.e("Failed to read value.", error.toException().toString());
            }
        });

        // Indicate whether the work finished successfully with the Result
        return Result.success();
    }

    private void createNotification(String title, String body) {

        NotificationManager manager =(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            // only active for android o and higher because it need NotificationChannel
            @SuppressLint("WrongConstant")
            NotificationChannel channel = new NotificationChannel("CHANEL_ID","CHANEL_NAME"
                    ,NotificationManager.IMPORTANCE_MAX);

            // configure the notification channel
            channel.setDescription("CHANEL_DESC");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.setVibrationPattern(new long[]{0,1000,500,1000});
            channel.enableVibration(true);
            manager.createNotificationChannel(channel);
        }


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"CHANEL_ID");

        builder.setSmallIcon(R.drawable.ic_boydrcare)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(false);

        manager.notify(0,builder.build());
    }


}
