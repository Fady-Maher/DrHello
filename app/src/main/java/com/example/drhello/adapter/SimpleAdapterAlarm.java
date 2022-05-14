package com.example.drhello.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bignerdranch.android.multiselector.SwappingHolder;
import com.example.drhello.OnClickSelectAlarm;
import com.example.drhello.R;
import com.example.drhello.database.ReminderDatabase;
import com.example.drhello.model.DateTimeSorter;
import com.example.drhello.model.Reminder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class SimpleAdapterAlarm extends RecyclerView.Adapter<SimpleAdapterAlarm.VerticalItemHolder> {

    private ArrayList<ReminderItem> mItems = new ArrayList<>();
    private Context context;
    private ReminderDatabase rb;
    private OnClickSelectAlarm onClickSelectAlarm;

    public SimpleAdapterAlarm(Context context,
                              ReminderDatabase rb
            , OnClickSelectAlarm onClickSelectAlarm) {
        this.context = context;
        this.rb = rb;
        this.onClickSelectAlarm = onClickSelectAlarm;
        mItems.addAll(generateData());
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItemCount() {
        mItems.clear();
        mItems.addAll(generateData());
        notifyDataSetChanged();
    }


    public void onDeleteItem() {
        mItems.clear();
        mItems.addAll(generateData());
    }


    public void removeItemSelected(int selected) {
        if (mItems.isEmpty()) return;
        mItems.remove(selected);
        notifyItemRemoved(selected);
    }

    @NonNull
    @Override
    public VerticalItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View root = inflater.inflate(R.layout.alarm_item_layout, parent, false);
        return new VerticalItemHolder(root, this);
    }

    @Override
    public void onBindViewHolder(@NonNull VerticalItemHolder holder, int position) {
        ReminderItem item = mItems.get(position);
        holder.setReminderTitle(item.mTitle);
        holder.setReminderDateTime(item.mDateTime);
        holder.setReminderRepeatInfo(item.mRepeat, item.mRepeatNo, item.mRepeatType);
        holder.setActiveImage(item.mActive);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    // Class for recycler view items
    public class ReminderItem {
        public String mTitle;
        public String mDateTime;
        public String mRepeat;
        public String mRepeatNo;
        public String mRepeatType;
        public String mActive;
        public int id;
        public ReminderItem(String Title, String DateTime, String Repeat, String RepeatNo,
                            String RepeatType, String Active,int id) {
            this.mTitle = Title;
            this.mDateTime = DateTime;
            this.mRepeat = Repeat;
            this.mRepeatNo = RepeatNo;
            this.mRepeatType = RepeatType;
            this.mActive = Active;
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    // Class to compare date and time so that items are sorted in ascending order

    public class DateTimeComparator implements Comparator {
        @SuppressLint("SimpleDateFormat")
        DateFormat f = new SimpleDateFormat("dd/MM/yyyy hh:mm");

        public int compare(Object a, Object b) {
            String o1 = ((DateTimeSorter) a).getDateTime();
            String o2 = ((DateTimeSorter) b).getDateTime();
            try {
                return Objects.requireNonNull(f.parse(o1)).compareTo(f.parse(o2));
            } catch (ParseException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    // UI and data class for recycler view items
    public class VerticalItemHolder extends SwappingHolder {
        private final TextView mTitleText, mDateAndTimeText, mRepeatInfoText;
        private final ImageView mActiveImage, mThumbnailImage;
        private final ColorGenerator mColorGenerator = ColorGenerator.DEFAULT;

        public VerticalItemHolder(View itemView, SimpleAdapterAlarm adapter) {
            super(itemView);
            // Initialize adapter for the items
            // Initialize views
            mTitleText = itemView.findViewById(R.id.recycle_title);
            mDateAndTimeText = itemView.findViewById(R.id.recycle_date_time);
            mRepeatInfoText = itemView.findViewById(R.id.recycle_repeat_info);
            mActiveImage = itemView.findViewById(R.id.active_image);
            mThumbnailImage = itemView.findViewById(R.id.thumbnail_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickSelectAlarm.OnClick(mItems.get(getAdapterPosition()).getId());
                }
            });

            mActiveImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickSelectAlarm.OnClickDelete(getAdapterPosition(),mItems.get(getAdapterPosition()).getId());
                }
            });
        }

        // Set reminder title view
        public void setReminderTitle(String title) {
            mTitleText.setText(title);
            String letter = "A";
            if (title != null && !title.isEmpty()) {
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
            if (repeat.equals("true")) {
                mRepeatInfoText.setText("Every " + repeatNo + " " + repeatType + "(s)");
            } else if (repeat.equals("false")) {

                mRepeatInfoText.setText("Repeat Off");
            }
        }

        // Set active image as on or off
        public void setActiveImage(String active) {
            if (active.equals("true")) {
                mActiveImage.setImageResource(R.drawable.ic_baseline_notification_on_24);
            } else if (active.equals("false")) {
                mActiveImage.setImageResource(R.drawable.ic_notifications_off_24);
            }
        }
    }

    // Generate real data for each item
    public List<ReminderItem> generateData() {
        ArrayList<ReminderItem> items = new ArrayList<>();
        // Get all reminders from the database
        List<Reminder> reminders = rb.getAllReminders();
        // Initialize lists
        List<String> Titles = new ArrayList<>();
        List<String> Repeats = new ArrayList<>();
        List<String> RepeatNos = new ArrayList<>();
        List<String> RepeatTypes = new ArrayList<>();
        List<String> Actives = new ArrayList<>();
        List<String> DateAndTime = new ArrayList<>();
        List<Integer> IDList = new ArrayList<>();
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
        for (int k = 0; k < Titles.size(); k++) {
            DateTimeSortList.add(new DateTimeSorter(key, DateAndTime.get(k)));
            key++;
        }

        // Sort items according to date and time in ascending order
        Collections.sort(DateTimeSortList, new DateTimeComparator());
        int k = 0;
        // Add data to each recycler view item
        for (DateTimeSorter item : DateTimeSortList) {
            int i = item.getIndex();
            items.add(new ReminderItem(Titles.get(i), DateAndTime.get(i), Repeats.get(i),
                    RepeatNos.get(i), RepeatTypes.get(i), Actives.get(i),IDList.get(i)));
            k++;
        }

        return items;

    }

}
