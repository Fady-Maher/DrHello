package com.example.drhello.ui.additional;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.example.drhello.R;
import com.example.drhello.databinding.ActivityAboutInfoBinding;

public class AboutInfoActivity extends AppCompatActivity {
    ActivityAboutInfoBinding activityAboutInfoBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_info);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            getWindow().setStatusBarColor(Color.WHITE);
        }

        activityAboutInfoBinding= DataBindingUtil.setContentView(AboutInfoActivity.this, R.layout.activity_about_info);
        activityAboutInfoBinding.shimmer.startShimmerAnimation();
        activityAboutInfoBinding.imgBackPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}