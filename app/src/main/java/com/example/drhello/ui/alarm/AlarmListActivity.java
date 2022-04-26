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
import com.example.drhello.R;
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

public class AlarmListActivity extends AppCompatActivity {

    private SimpleAdapter mAdapter;
    private final MultiSelector mMultiSelector = new MultiSelector();
    ActivityAlarmListBinding alarmListBinding;

    private final LinkedHashMap<Integer, Integer> IDmap = new LinkedHashMap<>();

    private ReminderDatabase rb;

    //private MultiSelector mMultiSelector = new MultiSelector();

    private AlarmReceiver mAlarmReceiver;

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

        mAdapter = new SimpleAdapter();

        mAdapter.setItemCount(getDefaultItemCount());

        alarmListBinding.reminderList.setAdapter(mAdapter);


        alarmListBinding.addMedcine.setOnClickListener(v -> {
            Intent intent=new Intent(AlarmListActivity.this,AlarmAddActivity.class);
            startActivity(intent);
        });
        // Initialize alarm

        mAlarmReceiver = new AlarmReceiver();
    }





    // On clicking a reminder item

    private void selectReminder(int mClickID) {

        String mStringClickID = Integer.toString(mClickID);



        // Create intent to edit the reminder

        // Put reminder id as extra

        Intent i = new Intent(this, ReminderEditActivity.class);

        i.putExtra(ReminderEditActivity.EXTRA_REMINDER_ID, mStringClickID);

        startActivityForResult(i, 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mAdapter.setItemCount(getDefaultItemCount());
    }


    // Recreate recycler view

    // This is done so that newly created reminders are displayed

    @Override

    public void onResume(){

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



        mAdapter.setItemCount(getDefaultItemCount());

    }

    @Override
    protected void onPause() {
        super.onPause();
        StateOfUser stateOfUser = new StateOfUser();
        stateOfUser.changeState("Offline");
    }

    // Multi select items in recycler view

    private final ModalMultiSelectorCallback mDeleteMode = new ModalMultiSelectorCallback(mMultiSelector) {

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            getMenuInflater().inflate(R.menu.menu_add_reminder, menu);
            return true;
        }

        @SuppressLint("NonConstantResourceId")
        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {

            switch (menuItem.getItemId()) {



                // On clicking discard reminders

                case R.id.discard_reminder:

                    // Close the context menu

                    actionMode.finish();



                    // Get the reminder id associated with the recycler view item

                    for (int i = IDmap.size(); i >= 0; i--) {

                        if (mMultiSelector.isSelected(i, 0)) {

                            int id = IDmap.get(i);



                            // Get reminder from reminder database using id

                            Reminder temp = rb.getReminder(id);

                            // Delete reminder

                            rb.deleteReminder(temp);

                            // Remove reminder from recycler view

                            mAdapter.removeItemSelected(i);

                            // Delete reminder alarm

                            mAlarmReceiver.cancelAlarm(getApplicationContext(), id);

                        }

                    }



                    // Clear selected items in recycler view

                    mMultiSelector.clearSelections();

                    // Recreate the recycler items

                    // This is done to remap the item and reminder ids

                    mAdapter.onDeleteItem(getDefaultItemCount());



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



                    return true;



                // On clicking save reminders

                case R.id.save_reminder:

                    // Close the context menu

                    actionMode.finish();

                    // Clear selected items in recycler view

                    mMultiSelector.clearSelections();

                    return true;



                default:

                    break;

            }

            return false;

        }

    };

    protected int getDefaultItemCount() {

        return 100;

    }


    // Adapter class for recycler view

    public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.VerticalItemHolder> {

        private final ArrayList<ReminderItem> mItems;


        public SimpleAdapter() {

            mItems = new ArrayList<>();

        }

        @SuppressLint("NotifyDataSetChanged")
        public void setItemCount(int count) {

            mItems.clear();

            mItems.addAll(generateData(count));

            notifyDataSetChanged();

        }



        public void onDeleteItem(int count) {

            mItems.clear();

            mItems.addAll(generateData(count));

        }



        public void removeItemSelected(int selected) {

            if (mItems.isEmpty()) return;

            mItems.remove(selected);

            notifyItemRemoved(selected);

        }



        // View holder for recycler view items

        @NotNull
        @Override
        public VerticalItemHolder onCreateViewHolder(ViewGroup container, int viewType) {

            LayoutInflater inflater = LayoutInflater.from(container.getContext());

            View root = inflater.inflate(R.layout.alarm_item_layout, container, false);



            return new VerticalItemHolder(root, this);

        }



        @Override

        public void onBindViewHolder(VerticalItemHolder itemHolder, int position) {

            ReminderItem item = mItems.get(position);

            itemHolder.setReminderTitle(item.mTitle);

            itemHolder.setReminderDateTime(item.mDateTime);

            itemHolder.setReminderRepeatInfo(item.mRepeat, item.mRepeatNo, item.mRepeatType);

            itemHolder.setActiveImage(item.mActive);

        }



        @Override

        public int getItemCount() {

            return mItems.size();

        }



        // Class for recycler view items

        public  class ReminderItem {

            public String mTitle;

            public String mDateTime;

            public String mRepeat;

            public String mRepeatNo;

            public String mRepeatType;

            public String mActive;



            public ReminderItem(String Title, String DateTime, String Repeat, String RepeatNo, String RepeatType, String Active) {

                this.mTitle = Title;

                this.mDateTime = DateTime;

                this.mRepeat = Repeat;

                this.mRepeatNo = RepeatNo;

                this.mRepeatType = RepeatType;

                this.mActive = Active;

            }

        }



        // Class to compare date and time so that items are sorted in ascending order

        public class DateTimeComparator implements Comparator {

            @SuppressLint("SimpleDateFormat")
            DateFormat f = new SimpleDateFormat("dd/MM/yyyy hh:mm");



            public int compare(Object a, Object b) {

                String o1 = ((DateTimeSorter)a).getDateTime();

                String o2 = ((DateTimeSorter)b).getDateTime();



                try {

                    return Objects.requireNonNull(f.parse(o1)).compareTo(f.parse(o2));

                } catch (ParseException e) {

                    throw new IllegalArgumentException(e);

                }

            }

        }



        // UI and data class for recycler view items

        public  class VerticalItemHolder extends SwappingHolder

                implements View.OnClickListener, View.OnLongClickListener {

            private final TextView mTitleText, mDateAndTimeText, mRepeatInfoText;

            private final ImageView mActiveImage , mThumbnailImage;

            private final ColorGenerator mColorGenerator = ColorGenerator.DEFAULT;

            private final SimpleAdapter mAdapter;



            public VerticalItemHolder(View itemView, SimpleAdapter adapter) {

                super(itemView, mMultiSelector);

                itemView.setOnClickListener(this);

                itemView.setOnLongClickListener(this);

                itemView.setLongClickable(true);



                // Initialize adapter for the items

                mAdapter = adapter;



                // Initialize views

                mTitleText = itemView.findViewById(R.id.recycle_title);

                mDateAndTimeText = itemView.findViewById(R.id.recycle_date_time);

                mRepeatInfoText =  itemView.findViewById(R.id.recycle_repeat_info);

                mActiveImage = itemView.findViewById(R.id.active_image);

                mThumbnailImage =  itemView.findViewById(R.id.thumbnail_image);

            }



            // On clicking a reminder item

            @Override

            public void onClick(View v) {

                if (!mMultiSelector.tapSelection(this)) {

                    int mTempPost = alarmListBinding.reminderList.getChildAdapterPosition(v);



                    int mReminderClickID = IDmap.get(mTempPost);

                    selectReminder(mReminderClickID);



                } else if(mMultiSelector.getSelectedPositions().isEmpty()){

                    mAdapter.setItemCount(getDefaultItemCount());

                }

            }



            // On long press enter action mode with context menu

            @Override

            public boolean onLongClick(View v) {

                AppCompatActivity activity = AlarmListActivity.this;

                activity.startSupportActionMode(mDeleteMode);

                mMultiSelector.setSelected(this, true);

                return true;

            }



            // Set reminder title view

            public void setReminderTitle(String title) {

                mTitleText.setText(title);

                String letter = "A";



                if(title != null && !title.isEmpty()) {

                    letter = title.substring(0, 1);

                }



                int color = mColorGenerator.getRandomColor();



                // Create a circular icon consisting of  a random background colour and first letter of title

                TextDrawable mDrawableBuilder = TextDrawable.builder()

                        .buildRound(letter, color);

                mThumbnailImage.setImageDrawable(mDrawableBuilder);

            }



            // Set date and time views

            public void setReminderDateTime(String datetime) {

                mDateAndTimeText.setText(datetime);

            }



            // Set repeat views

            @SuppressLint("SetTextI18n")
            public void setReminderRepeatInfo(String repeat, String repeatNo, String repeatType) {

                if(repeat.equals("true")){

                    mRepeatInfoText.setText("Every " + repeatNo + " " + repeatType + "(s)");

                }else if (repeat.equals("false")) {

                    mRepeatInfoText.setText("Repeat Off");

                }

            }



            // Set active image as on or off

            public void setActiveImage(String active){

                if(active.equals("true")){

                    mActiveImage.setImageResource(R.drawable.ic_baseline_notification_on_24);

                }else if (active.equals("false")) {

                    mActiveImage.setImageResource(R.drawable.ic_notifications_off_24);

                }

            }

        }

        // Generate real data for each item

        public List<ReminderItem> generateData(int count) {

            ArrayList<SimpleAdapter.ReminderItem> items = new ArrayList<>();



            // Get all reminders from the database

            List<Reminder> reminders = rb.getAllReminders();



            // Initialize lists

            List<String> Titles = new ArrayList<>();

            List<String> Repeats = new ArrayList<>();

            List<String> RepeatNos = new ArrayList<>();

            List<String> RepeatTypes = new ArrayList<>();

            List<String> Actives = new ArrayList<>();

            List<String> DateAndTime = new ArrayList<>();

            List<Integer> IDList= new ArrayList<>();

            List<DateTimeSorter> DateTimeSortList = new ArrayList<>();



            // Add details of all reminders in their respective lists

            for (Reminder r : reminders) {

                Titles.add(r.getTitle());

                DateAndTime.add(r.getDate() + " " + r.getTime());

                Repeats.add(r.getRepeat());

                RepeatNos.add(r.getRepeatNo());

                RepeatTypes.add(r.getRepeatType());

                Actives.add(r.getActive());

                IDList.add(r.getID());

            }



            int key = 0;



            // Add date and time as DateTimeSorter objects

            for(int k = 0; k<Titles.size(); k++){

                DateTimeSortList.add(new DateTimeSorter(key, DateAndTime.get(k)));

                key++;

            }



            // Sort items according to date and time in ascending order

            Collections.sort(DateTimeSortList, new DateTimeComparator());



            int k = 0;



            // Add data to each recycler view item

            for (DateTimeSorter item:DateTimeSortList) {

                int i = item.getIndex();



                items.add(new SimpleAdapter.ReminderItem(Titles.get(i), DateAndTime.get(i), Repeats.get(i),

                        RepeatNos.get(i), RepeatTypes.get(i), Actives.get(i)));

                IDmap.put(k, IDList.get(i));

                k++;

            }

            return items;

        }

    }



}