package com.example.drhello.ui.chats;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import com.example.drhello.R;
import com.example.drhello.adapter.TapFriendAdapter;
import com.example.drhello.databinding.ActivityAddPersonBinding;
import com.google.android.material.tabs.TabLayout;

public class AddPersonActivity extends AppCompatActivity {

    private ActivityAddPersonBinding activityAddPersonBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            getWindow().setStatusBarColor(Color.WHITE);
        }


        activityAddPersonBinding = DataBindingUtil.setContentView(AddPersonActivity.this, R.layout.activity_add_person);

        activityAddPersonBinding.backAddPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setSupportActionBar(activityAddPersonBinding.toolbarCo);
        getSupportActionBar().setTitle("");

        activityAddPersonBinding.TabReaction.addTab(activityAddPersonBinding.TabReaction.newTab().setText("ADD Friends"),0);
        activityAddPersonBinding.TabReaction.addTab(activityAddPersonBinding.TabReaction.newTab().setText("Requests Friends"),1);

        TapFriendAdapter adapter = new TapFriendAdapter( getSupportFragmentManager(),
                activityAddPersonBinding.TabReaction.getTabCount(),
               AddPersonActivity.this
                );
        activityAddPersonBinding.viewPager.setAdapter(adapter);

        activityAddPersonBinding.viewPager.addOnPageChangeListener(new
                TabLayout.TabLayoutOnPageChangeListener(activityAddPersonBinding.TabReaction));

        activityAddPersonBinding.TabReaction.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                activityAddPersonBinding.viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }



}