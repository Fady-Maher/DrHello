package com.example.drhello.ui.alarm;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;
import com.example.drhello.R;
import com.example.drhello.ui.chats.StateOfUser;
import com.example.drhello.database.ReminderDatabase;
import com.example.drhello.databinding.ActivityReminderEditBinding;
import com.example.drhello.model.Reminder;
import org.jetbrains.annotations.NotNull;
import java.util.Calendar;

public class ReminderEditActivity extends AppCompatActivity implements View.OnClickListener, TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    ActivityReminderEditBinding editBinding;
    private String mTitle, mTime, mDate, mRepeat, mRepeatNo, mRepeatType, mActive;
    private int mReceivedID;
    private int mYear, mMonth, mHour, mMinute, mDay;
    private long mRepeatTime;
    private Calendar mCalendar;
    private Reminder mReceivedReminder;
    private ReminderDatabase rb;
    private AlarmReceiver mAlarmReceiver;
    // Constant Intent String
    public static final String EXTRA_REMINDER_ID = "Reminder_ID";
    // Values for orientation change
    private static final String KEY_TITLE = "title_key";
    private static final String KEY_TIME = "time_key";
    private static final String KEY_DATE = "date_key";
    private static final String KEY_REPEAT = "repeat_key";
    private static final String KEY_REPEAT_NO = "repeat_no_key";
    private static final String KEY_REPEAT_TYPE = "repeat_type_key";
    private static final String KEY_ACTIVE = "active_key";
    // Constant values in milliseconds
    private static final long milMinute = 60000L;
    private static final long milHour = 3600000L;
    private static final long milDay = 86400000L;
    private static final long milWeek = 604800000L;
    private static final long milMonth = 2592000000L;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_edit);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            getWindow().setStatusBarColor(Color.WHITE);
        }
        editBinding = DataBindingUtil.setContentView(this, R.layout.activity_reminder_edit);

        editBinding.imgBackAlarmEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editBinding.reminderTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTitle = s.toString().trim();
                editBinding.reminderTitle.setError(null);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }

        });


        // Get reminder id from intent
        mReceivedID = Integer.parseInt(getIntent().getStringExtra(EXTRA_REMINDER_ID));
        // Get reminder using reminder id
        rb = new ReminderDatabase(this);
        mReceivedReminder = rb.getReminder(mReceivedID);

        // Get values from reminder
        mTitle = mReceivedReminder.getTitle();
        mDate = mReceivedReminder.getDate();
        mTime = mReceivedReminder.getTime();
        mRepeat = mReceivedReminder.getRepeat();
        mRepeatNo = mReceivedReminder.getRepeatNo();
        mRepeatType = mReceivedReminder.getRepeatType();
        mActive = mReceivedReminder.getActive();
        editBinding.repeatSwitch.setOnClickListener(this);
        editBinding.reminderTitle.setText(mTitle);
        editBinding.setDate.setText(mDate);
        editBinding.setTime.setText(mTime);
        editBinding.setRepeatNo.setText(mRepeatNo);
        editBinding.setRepeatType.setText(mRepeatType);
        editBinding.setRepeat.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");
        // To save state on device rotation
        if (savedInstanceState != null) {
            String savedTitle = savedInstanceState.getString(KEY_TITLE);
            editBinding.reminderTitle.setText(savedTitle);
            mTitle = savedTitle;
            String savedTime = savedInstanceState.getString(KEY_TIME);
            editBinding.setTime.setText(savedTime);
            mTime = savedTime;
            String savedDate = savedInstanceState.getString(KEY_DATE);
            editBinding.setDate.setText(savedDate);
            mDate = savedDate;
            String saveRepeat = savedInstanceState.getString(KEY_REPEAT);
            editBinding.repeatText.setText(saveRepeat);
            mRepeat = saveRepeat;
            String savedRepeatNo = savedInstanceState.getString(KEY_REPEAT_NO);
            editBinding.repeatNoText.setText(savedRepeatNo);
            mRepeatNo = savedRepeatNo;
            String savedRepeatType = savedInstanceState.getString(KEY_REPEAT_TYPE);
            editBinding.repeatTypeText.setText(savedRepeatType);
            mRepeatType = savedRepeatType;
            mActive = savedInstanceState.getString(KEY_ACTIVE);
        }

        // Setup repeat switch
        if (mRepeat.equals("false")) {
            editBinding.repeatSwitch.setChecked(false);
            editBinding.repeatSwitch.setText(R.string.repeat_off);
        } else if (mRepeat.equals("true")) {
            editBinding.repeatSwitch.setChecked(true);
        }


        // Obtain Date and Time details
        mCalendar = Calendar.getInstance();
        mAlarmReceiver = new AlarmReceiver();
        String[] mDateSplit = mDate.split("/");
        String[] mTimeSplit = mTime.split(":");
        mDay = Integer.parseInt(mDateSplit[0]);
        mMonth = Integer.parseInt(mDateSplit[1]);
        mYear = Integer.parseInt(mDateSplit[2]);
        mHour = Integer.parseInt(mTimeSplit[0]);
        mMinute = Integer.parseInt(mTimeSplit[1]);
        editBinding.linearTime.setOnClickListener(this);
        editBinding.linearDate.setOnClickListener(this);
        editBinding.repeateInterval.setOnClickListener(this);
        editBinding.repeatType.setOnClickListener(this);
        editBinding.btnSave.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linear_time:
                setTime();
                break;
            case R.id.linear_date:
                setDate();
                break;
            case R.id.repeate_interval:
                setRepeatNo();
                break;
            case R.id.repeatType:
                selectRepeatType();
                break;
            case R.id.btn_save:
                updateReminder();
                break;
            case R.id.repeat_switch:
                onSwitchRepeat(v);
        }
    }


    // To save state on device rotation
    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(KEY_TITLE, editBinding.reminderTitle.getText());
        outState.putCharSequence(KEY_TIME, editBinding.setTime.getText());
        outState.putCharSequence(KEY_DATE, editBinding.setDate.getText());
        outState.putCharSequence(KEY_REPEAT, editBinding.repeatText.getText());
        outState.putCharSequence(KEY_REPEAT_NO, editBinding.repeatNoText.getText());
        outState.putCharSequence(KEY_REPEAT_TYPE, editBinding.repeatTypeText.getText());
        outState.putCharSequence(KEY_ACTIVE, mActive);
    }

    // On clicking Time picker
    public void setTime() {
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = new TimePickerDialog(this,
                this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false
        );
        tpd.show();
    }

    // On clicking Date picker
    public void setDate() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = new DatePickerDialog(this,
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show();
    }


    @SuppressLint("SetTextI18n")
    public void onSwitchRepeat(View view) {
        boolean on = ((Switch) view).isChecked();
        if (on) {
            mRepeat = "true";
            editBinding.setRepeat.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");
        } else {
            mRepeat = "false";
            editBinding.setRepeat.setText(R.string.repeat_off);
        }
    }

    // On clicking repeat interval button
    @SuppressLint("SetTextI18n")
    public void setRepeatNo() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Enter Number");
        // Create EditText box to input repeat number
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        alert.setView(input);
        alert.setPositiveButton("Ok",
                (dialog, whichButton) -> {
                    if (input.getText().toString().length() == 0) {
                        mRepeatNo = Integer.toString(1);
                        editBinding.setRepeatNo.setText(mRepeatNo);
                        editBinding.setRepeat.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");
                    } else {
                        mRepeatNo = input.getText().toString().trim();
                        editBinding.setRepeatNo.setText(mRepeatNo);
                        editBinding.setRepeat.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");
                    }
                });

        alert.setNegativeButton("Cancel", (dialog, whichButton) -> {
            // do nothing
        });

        alert.show();
    }

    @SuppressLint("SetTextI18n")
    public void selectRepeatType() {
        final String[] items = new String[5];
        items[0] = "Minute";
        items[1] = "Hour";
        items[2] = "Day";
        items[3] = "Week";
        items[4] = "Month";
        // Create List Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Type");
        builder.setItems(items, (dialog, item) -> {
            mRepeatType = items[item];
            editBinding.setRepeatType.setText(mRepeatType);
            editBinding.setRepeat.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");
        });

        AlertDialog alert = builder.create();
        alert.show();
    }


    // On clicking the update button

    public void updateReminder(){
        // Set new values in the reminder
        mReceivedReminder.setTitle(mTitle);
        mReceivedReminder.setDate(mDate);
        mReceivedReminder.setTime(mTime);
        mReceivedReminder.setRepeat(mRepeat);
        mReceivedReminder.setRepeatNo(mRepeatNo);
        mReceivedReminder.setRepeatType(mRepeatType);
        mReceivedReminder.setActive(mActive);

        // Update reminder
        rb.updateReminder(mReceivedReminder);
        // Set up calender for creating the notification
        mCalendar.set(Calendar.MONTH, --mMonth);
        mCalendar.set(Calendar.YEAR, mYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, mDay);
        mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
        mCalendar.set(Calendar.MINUTE, mMinute);
        mCalendar.set(Calendar.SECOND, 0);

        // Cancel existing notification of the reminder by using its ID
        mAlarmReceiver.cancelAlarm(getApplicationContext(), mReceivedID);

        // Check repeat type
        switch (mRepeatType) {
            case "Minute":
                mRepeatTime = Integer.parseInt(mRepeatNo) * milMinute;
                break;
            case "Hour":
                mRepeatTime = Integer.parseInt(mRepeatNo) * milHour;
                break;
            case "Day":
                mRepeatTime = Integer.parseInt(mRepeatNo) * milDay;
                break;
            case "Week":
                mRepeatTime = Integer.parseInt(mRepeatNo) * milWeek;
                break;
            case "Month":
                mRepeatTime = Integer.parseInt(mRepeatNo) * milMonth;
                break;
        }

        // Create a new notification
        if (mActive.equals("true")) {
            if (mRepeat.equals("true")) {
                mAlarmReceiver.setRepeatAlarm(getApplicationContext(), mCalendar, mReceivedID, mRepeatTime);
            } else if (mRepeat.equals("false")) {
                mAlarmReceiver.setAlarm(getApplicationContext(), mCalendar, mReceivedID);
            }
        }

        // Create toast to confirm update
        Toast.makeText(getApplicationContext(), "Edited",
                Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        month++;
        mDay = dayOfMonth;
        mMonth = month;
        mYear = year;
        mDate = dayOfMonth + "/" + month + "/" + year;
        editBinding.setDate.setText(mDate);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mHour = hourOfDay;
        mMinute = minute;
        if (minute < 10) {
            mTime = hourOfDay + ":" + "0" + minute;
        } else {
            mTime = hourOfDay + ":" + minute;
        }
        editBinding.setTime.setText(mTime);
    }
    @Override
    protected void onResume() {
        super.onResume();
        StateOfUser stateOfUser = new StateOfUser();
        stateOfUser.changeState("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        StateOfUser stateOfUser = new StateOfUser();
        stateOfUser.changeState("Offline");
    }
}