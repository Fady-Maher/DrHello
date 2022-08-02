package com.example.drhello;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import com.example.drhello.databinding.ActivityFirstSplashBinding;

public class FirstSplashActivity extends AppCompatActivity {
    private ActivityFirstSplashBinding activityFirstSplashBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_splash);
        //Permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        activityFirstSplashBinding = DataBindingUtil.setContentView(this, R.layout.activity_first_splash);
        activityFirstSplashBinding.txtSplash.animate().translationY(3000).setDuration(1000).setStartDelay(4000);
        activityFirstSplashBinding.img.animate().translationY(3000).setDuration(1000).setStartDelay(4000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(FirstSplashActivity.this, IntroScreenActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                finish();
            }
        },6000);
    }


}