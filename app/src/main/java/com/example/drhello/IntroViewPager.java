package com.example.drhello;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.drhello.databinding.ActivitySplashScreenBinding;

import java.util.List;

public class IntroViewPager extends PagerAdapter {

    Context mContext ;
    List<ScreenItem> mListScreen;
    ActivitySplashScreenBinding activitySplashScreenBinding;

    public IntroViewPager(Context mContext, List<ScreenItem> mListScreen, ActivitySplashScreenBinding activitySplashScreenBinding) {
        this.mContext = mContext;
        this.mListScreen = mListScreen;
        this.activitySplashScreenBinding = activitySplashScreenBinding;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layoutScreen = inflater.inflate(R.layout.layout_screen,null);
        ImageView imgSlide = layoutScreen.findViewById(R.id.intro_img);
        ImageView img_below = layoutScreen.findViewById(R.id.img_below);
        imgSlide.setImageResource(mListScreen.get(position).getScreenImage());
        img_below.setImageResource(mListScreen.get(position).getImg_below());
        activitySplashScreenBinding.tvSkip.setTextColor(mContext.getResources().getColor(R.color.appColor));

         if(mListScreen.get(position).getType().equals("bot")) {
            activitySplashScreenBinding.btnNext.setVisibility(View.VISIBLE);
            activitySplashScreenBinding.tvSkip.setVisibility(View.VISIBLE);
            activitySplashScreenBinding.tabIndicator.setVisibility(View.VISIBLE);
            activitySplashScreenBinding.btnGetStarted.setVisibility(View.INVISIBLE);
        } else if(mListScreen.get(position).getType().equals("hard")){
            activitySplashScreenBinding.btnNext.setVisibility(View.VISIBLE);
            activitySplashScreenBinding.tvSkip.setVisibility(View.VISIBLE);
            activitySplashScreenBinding.tabIndicator.setVisibility(View.VISIBLE);
            activitySplashScreenBinding.btnGetStarted.setVisibility(View.INVISIBLE);
        }else if(mListScreen.get(position).getType().equals("ray")) {
            activitySplashScreenBinding.btnNext.setVisibility(View.VISIBLE);
            activitySplashScreenBinding.tvSkip.setVisibility(View.VISIBLE);
            activitySplashScreenBinding.tabIndicator.setVisibility(View.VISIBLE);
            activitySplashScreenBinding.btnGetStarted.setVisibility(View.INVISIBLE);
        }else if(mListScreen.get(position).getType().equals("social")) {
             activitySplashScreenBinding.btnNext.setVisibility(View.VISIBLE);
             activitySplashScreenBinding.tvSkip.setVisibility(View.VISIBLE);
             activitySplashScreenBinding.tabIndicator.setVisibility(View.VISIBLE);
             activitySplashScreenBinding.btnGetStarted.setVisibility(View.INVISIBLE);
        }else{
             activitySplashScreenBinding.btnGetStarted.setVisibility(View.INVISIBLE);
         }

        container.addView(layoutScreen);
        return layoutScreen;

    }

    @Override
    public int getCount() {
        return mListScreen.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((View)object);

    }
}
