package com.example.drhello;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.drhello.ui.alarm.ReminderEditActivity;
import com.example.drhello.ui.hardware.Hardware;
import com.example.drhello.ui.hardware.TestHardwareActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public  class MYService extends Service {
        @Override
        public void onCreate() {
        }

        @Override
        public void onStart(Intent intent, int startId) {
            //do something

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference();

                myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                                Hardware hardware = dataSnapshot.getValue(Hardware.class);
                                Toast.makeText(MYService.this, hardware.getHeart_Rate().toString(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                                // Failed to read value
                                // Log.e("Failed to read value.", error.toException().toString());
                        }
                });
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
}
