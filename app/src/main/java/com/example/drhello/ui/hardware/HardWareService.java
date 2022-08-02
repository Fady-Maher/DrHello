package com.example.drhello.ui.hardware;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.drhello.R;
import com.example.drhello.other.Restarter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

public class HardWareService extends Service {
    public int counter=0;
    public Hardware lastHardware = new Hardware(0.0,0.0,0.0,0.0,"1");
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("SERVICE "," onCreate");

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground()
    {
        String NOTIFICATION_CHANNEL_ID = "example.permanence";
        String channelName = "Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startTimer();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        SharedPreferences prefs = getSharedPreferences("com.example.drhello", MODE_PRIVATE);
        String id = prefs.getString("id", "id");//"No name defined" is the default value.
        if(!id.equals("id")){
            DatabaseReference myRef = database.getReference().child(id);
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Hardware hardware = dataSnapshot.getValue(Hardware.class);
                    if(hardware.getTemperature_F() != lastHardware.getTemperature_F()
                            ||hardware.getTemperature_C() != lastHardware.getTemperature_C()
                            ||hardware.getSPO2() != lastHardware.getSPO2()
                            ||hardware.getHeart_Rate() != lastHardware.getHeart_Rate()){
                        lastHardware = hardware;
                        createNotification("Heart_Rate: "+hardware.getHeart_Rate().toString(),
                                "Temperature: "+hardware.getTemperature_C().toString());
                    }
                    Log.i("hardware", "=========  "+ (hardware.getHeart_Rate()));
                    //    Toast.makeText(HardWareService.this, hardware.getHeart_Rate().toString(), Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.e("Failed to read value.", error.toException().toString());
                }
            });
        }

        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stoptimertask();
        Log.i("onDestroy: ", "SERVICE INSIDE");

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, Restarter.class);
        this.sendBroadcast(broadcastIntent);
    }



    private Timer timer;
    private TimerTask timerTask;
    public void startTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {

                Log.i("Count", "=========  "+ (counter++));
            }
        };
        timer.schedule(timerTask, 1000, 1000); //
    }

    private void createNotification(String title, String body) {
        NotificationManager manager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
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


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"CHANEL_ID");

        builder.setSmallIcon(R.drawable.ic_boydrcare)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(false);
        manager.notify(0,builder.build());
    }


    public void stoptimertask() {
        Log.i("stoptimertask: ", "SERVICE INSIDE");
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}