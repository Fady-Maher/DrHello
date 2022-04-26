package com.example.drhello.ui.alarm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.drhello.R;
import com.example.drhello.database.ReminderDatabase;
import com.example.drhello.databinding.ActivityAlarmCancelBinding;
import com.example.drhello.model.Reminder;


public class AlarmCancelActivity extends AppCompatActivity  implements View.OnClickListener {

    ActivityAlarmCancelBinding cancelBinding;
    private int mReceivedID;

    // Constant Intent String

    public static final String EXTRA_REMINDER_ID = "Reminder_ID";



    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_cancel);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        cancelBinding= DataBindingUtil.setContentView(this,R.layout.activity_alarm_cancel);

        // Get reminder id from intent

        mReceivedID = Integer.parseInt(getIntent().getStringExtra(EXTRA_REMINDER_ID));


        // Get reminder using reminder id

        ReminderDatabase rb = new ReminderDatabase(this);

        Reminder mReceivedReminder = rb.getReminder(mReceivedID);


        // Get values from reminder

        String mTitle = mReceivedReminder.getTitle();

        String mDate = mReceivedReminder.getDate();

        String mTime = mReceivedReminder.getTime();


        cancelBinding.reminder.setText(mTitle);
        cancelBinding.date.setText(mTime +","+ mDate);

        cancelBinding.btnDone.setOnClickListener(this);
        cancelBinding.btnSnooze.setOnClickListener(this);
        cancelBinding.btnView.setOnClickListener(this);




    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_done:
                AlarmReceiver.r.stop();
                finish();
                break;
            case R.id.btn_Snooze:
                AlarmReceiver.r.stop();
                finish();
                break;
            case R.id.btn_view:
                AlarmReceiver.r.stop();
                Intent i = new Intent(this, ReminderEditActivity.class);

                i.putExtra(ReminderEditActivity.EXTRA_REMINDER_ID, Integer.toString(mReceivedID));
                startActivity(i);
                finish();
                break;


        }

    }
}