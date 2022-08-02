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
import com.example.drhello.databinding.ActivityAlarmAddBinding;
import com.example.drhello.model.Reminder;
import org.jetbrains.annotations.NotNull;
import java.util.Calendar;

public class AlarmAddActivity extends AppCompatActivity implements View.OnClickListener, TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    @SuppressLint("StaticFieldLeak")
    public static ActivityAlarmAddBinding alarmAddBinding;
    private Calendar mCalendar;
    private int mYear, mMonth, mHour, mMinute, mDay;
    private long mRepeatTime;
    private String mTitle, mTime, mDate, mRepeat, mRepeatNo, mRepeatType, mActive;
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
        setContentView(R.layout.activity_alarm_add);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            getWindow().setStatusBarColor(Color.WHITE);
        }
        alarmAddBinding = DataBindingUtil.setContentView(this, R.layout.activity_alarm_add);

        // Initialize default values

        mActive = "true";
        mRepeat = "true";
        mRepeatNo = Integer.toString(1);
        mRepeatType = "Hour";
        mCalendar = Calendar.getInstance();
        mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        mMinute = mCalendar.get(Calendar.MINUTE);
        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH) + 1;
        mDay = mCalendar.get(Calendar.DATE);
        mDate = mDay + "/" + mMonth + "/" + mYear;
        mTime = mHour + ":" + mMinute;
        alarmAddBinding.imgBackAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        alarmAddBinding.reminderTitle.addTextChangedListener(new TextWatcher() {
            @Override

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTitle = s.toString().trim();
                alarmAddBinding.reminderTitle.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

        });

        alarmAddBinding.repeatSwitch.setOnClickListener(this);
        alarmAddBinding.setDate.setText(mDate);
        alarmAddBinding.setTime.setText(mTime);
        alarmAddBinding.setRepeatNo.setText(mRepeatNo);
        alarmAddBinding.setRepeatType.setText(mRepeatType);
        alarmAddBinding.setRepeat.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");

        // To save state on device rotation
        if (savedInstanceState != null) {
            String savedTitle = savedInstanceState.getString(KEY_TITLE);
            alarmAddBinding.reminderTitle.setText(savedTitle);
            mTitle = savedTitle;
            String savedTime = savedInstanceState.getString(KEY_TIME);
            alarmAddBinding.setTime.setText(savedTime);
            mTime = savedTime;
            String savedDate = savedInstanceState.getString(KEY_DATE);
            alarmAddBinding.setDate.setText(savedDate);
            mDate = savedDate;
            String saveRepeat = savedInstanceState.getString(KEY_REPEAT);
            alarmAddBinding.repeatText.setText(saveRepeat);
            mRepeat = saveRepeat;
            String savedRepeatNo = savedInstanceState.getString(KEY_REPEAT_NO);
            alarmAddBinding.repeatNoText.setText(savedRepeatNo);
            mRepeatNo = savedRepeatNo;
            String savedRepeatType = savedInstanceState.getString(KEY_REPEAT_TYPE);
            alarmAddBinding.repeatTypeText.setText(savedRepeatType);
            mRepeatType = savedRepeatType;
            mActive = savedInstanceState.getString(KEY_ACTIVE);
        }

        alarmAddBinding.linearTime.setOnClickListener(this);
        alarmAddBinding.linearDate.setOnClickListener(this);
        alarmAddBinding.repeateInterval.setOnClickListener(this);
        alarmAddBinding.repeatType.setOnClickListener(this);
        alarmAddBinding.btnSave.setOnClickListener(this);
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
                saveReminder();
                break;
            case R.id.repeat_switch:
                onSwitchRepeat(v);
        }

    }

    // To save state on device rotation

    @Override

    protected void onSaveInstanceState(@NotNull Bundle outState) {

        super.onSaveInstanceState(outState);


        outState.putCharSequence(KEY_TITLE, alarmAddBinding.reminderTitle.getText());

        outState.putCharSequence(KEY_TIME, alarmAddBinding.setTime.getText());

        outState.putCharSequence(KEY_DATE, alarmAddBinding.setDate.getText());

        outState.putCharSequence(KEY_REPEAT, alarmAddBinding.repeatText.getText());

        outState.putCharSequence(KEY_REPEAT_NO, alarmAddBinding.repeatNoText.getText());

        outState.putCharSequence(KEY_REPEAT_TYPE, alarmAddBinding.repeatTypeText.getText());

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

        //tpd.setThemeDark(false);

        tpd.show();
        //tpd.show(getFragmentManager(), "Timepickerdialog");

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
        //dpd.show(getFragmentManager(), "Datepickerdialog");

    }


    // On clicking repeat type button

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

            alarmAddBinding.setRepeatType.setText(mRepeatType);

            alarmAddBinding.setRepeat.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");

        });

        AlertDialog alert = builder.create();

        alert.show();

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

                        alarmAddBinding.setRepeatNo.setText(mRepeatNo);

                        alarmAddBinding.setRepeat.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");

                    } else {

                        mRepeatNo = input.getText().toString().trim();

                        alarmAddBinding.setRepeatNo.setText(mRepeatNo);

                        alarmAddBinding.setRepeat.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");

                    }

                });

        alert.setNegativeButton("Cancel", (dialog, whichButton) -> {

            // do nothing

        });

        alert.show();

    }


    // On clicking the save button

    public void saveReminder() {
        ReminderDatabase rb = new ReminderDatabase(this);
        // Creating Reminder
        int ID = rb.addReminder(new Reminder(mTitle, mDate, mTime, mRepeat, mRepeatNo, mRepeatType, mActive));
        // Set up calender for creating the notification
        mCalendar.set(Calendar.MONTH, --mMonth);
        mCalendar.set(Calendar.YEAR, mYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, mDay);
        mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
        mCalendar.set(Calendar.MINUTE, mMinute);
        mCalendar.set(Calendar.SECOND, 0);
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
                new AlarmReceiver().setRepeatAlarm(getApplicationContext(), mCalendar, ID, mRepeatTime);
            } else if (mRepeat.equals("false")) {
                new AlarmReceiver().setAlarm(getApplicationContext(), mCalendar, ID);
            }
        }
        // Create toast to confirm new reminder
        Toast.makeText(getApplicationContext(), "Saved",
                Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    // On pressing the back button
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        month++;
        mDay = dayOfMonth;
        mMonth = month;
        mYear = year;
        mDate = dayOfMonth + "/" + month + "/" + year;
        alarmAddBinding.setDate.setText(mDate);
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
        alarmAddBinding.setTime.setText(mTime);
    }

    // On clicking the repeat switch
    @SuppressLint("SetTextI18n")
    public void onSwitchRepeat(View view) {
        boolean on = ((Switch) view).isChecked();
        if (on) {
            mRepeat = "true";
            alarmAddBinding.setRepeat.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");
        } else {
            mRepeat = "false";
            alarmAddBinding.setRepeat.setText(R.string.repeat_off);
        }
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