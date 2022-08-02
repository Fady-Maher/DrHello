package com.example.drhello.ui.hardware;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import com.example.drhello.R;
import com.example.drhello.ui.chats.StateOfUser;
import com.example.drhello.databinding.ActivityHardwareBinding;

public class HardwareActivity extends AppCompatActivity {

    private ActivityHardwareBinding activityHardwareBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hardware);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            getWindow().setStatusBarColor(Color.WHITE);
        }
        activityHardwareBinding=DataBindingUtil.setContentView(HardwareActivity.this, R.layout.activity_hardware);

        activityHardwareBinding.backAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        activityHardwareBinding.fabNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HardwareActivity.this,TestHardwareActivity.class);
                startActivity(intent);
            }
        });



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