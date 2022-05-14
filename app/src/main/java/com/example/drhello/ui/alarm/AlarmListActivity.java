package com.example.drhello.ui.alarm;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bignerdranch.android.multiselector.ModalMultiSelectorCallback;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.SwappingHolder;
import com.example.drhello.OnClickSelectAlarm;
import com.example.drhello.R;
import com.example.drhello.adapter.SimpleAdapterAlarm;
import com.example.drhello.ui.chats.StateOfUser;
import com.example.drhello.database.ReminderDatabase;
import com.example.drhello.databinding.ActivityAlarmListBinding;
import com.example.drhello.model.DateTimeSorter;
import com.example.drhello.model.Reminder;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
public class AlarmListActivity extends AppCompatActivity implements OnClickSelectAlarm {
    ActivityAlarmListBinding alarmListBinding;
    private ReminderDatabase rb;
    private AlarmReceiver mAlarmReceiver;
    private SimpleAdapterAlarm mAdapter;
    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        alarmListBinding = DataBindingUtil.setContentView(this, R.layout.activity_alarm_list);
        // Initialize reminder database
        rb = new ReminderDatabase(getApplicationContext());
        // To check is there are saved reminders
        // If there are no reminders display a message asking the user to create reminders
        List<Reminder> mTest = rb.getAllReminders();
        if (mTest.isEmpty()) {
            alarmListBinding.noReminderText.setVisibility(View.VISIBLE);
        }
        // Create recycler view
        alarmListBinding.reminderList.setLayoutManager(new LinearLayoutManager(this));
        registerForContextMenu(alarmListBinding.reminderList);
        mAdapter = new SimpleAdapterAlarm(getApplicationContext(),rb, AlarmListActivity.this);

        mAdapter.setItemCount();
        alarmListBinding.reminderList.setAdapter(mAdapter);

        alarmListBinding.addMedcine.setOnClickListener(v -> {
            Intent intent = new Intent(AlarmListActivity.this, AlarmAddActivity.class);
            startActivity(intent);
        });

        // Initialize alarm
        mAlarmReceiver = new AlarmReceiver();
    }

    // On clicking a reminder item
    private void selectReminder(int mClickID) {
        String mStringClickID = Integer.toString(mClickID);
        Intent i = new Intent(this, ReminderEditActivity.class);
        i.putExtra(ReminderEditActivity.EXTRA_REMINDER_ID, mStringClickID);
        startActivityForResult(i, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mAdapter.setItemCount();
    }

    // Recreate recycler view
    // This is done so that newly created reminders are displayed
    @Override
    public void onResume() {
        super.onResume();
        StateOfUser stateOfUser = new StateOfUser();
        stateOfUser.changeState("Online");
        // To check is there are saved reminders
        // If there are no reminders display a message asking the user to create reminders
        List<Reminder> mTest = rb.getAllReminders();
        if (mTest.isEmpty()) {
            alarmListBinding.noReminderText.setVisibility(View.VISIBLE);
        } else {
            alarmListBinding.noReminderText.setVisibility(View.GONE);
        }
        mAdapter.setItemCount();
    }

    @Override
    protected void onPause() {
        super.onPause();
        StateOfUser stateOfUser = new StateOfUser();
        stateOfUser.changeState("Offline");
    }

    @Override
    public void OnClick(int mReminderClickID) {
         selectReminder(mReminderClickID);
    }

    @Override
    public void OnClickDelete(int position,int id) {
        // Get reminder from reminder database using id
        Reminder temp = rb.getReminder(id);
        // Delete reminder
        rb.deleteReminder(temp);
        // Remove reminder from recycler view
        mAdapter.removeItemSelected(position);
        // Delete reminder alarm
        mAlarmReceiver.cancelAlarm(getApplicationContext(), id);
        // Recreate the recycler items
        // This is done to remap the item and reminder ids
        mAdapter.onDeleteItem();
        // Display toast to confirm delete
        Toast.makeText(getApplicationContext(),
                "Deleted",
                Toast.LENGTH_SHORT).show();
        // To check is there are saved reminders
        // If there are no reminders display a message asking the user to create reminders
        List<Reminder> mTest = rb.getAllReminders();
        if (mTest.isEmpty()) {
            alarmListBinding.noReminderText.setVisibility(View.VISIBLE);
        } else {
            alarmListBinding.noReminderText.setVisibility(View.GONE);
        }
    }
}