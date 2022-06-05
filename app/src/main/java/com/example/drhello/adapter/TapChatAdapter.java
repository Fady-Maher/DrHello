package com.example.drhello.adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;

import com.example.drhello.R;
import com.example.drhello.fragment.fragmentchat.DoctorsFragment;
import com.example.drhello.fragment.fragmentchat.NormalUsersFragment;
import com.example.drhello.fragment.fragmentfriends.AddFriendFragment;
import com.example.drhello.fragment.fragmentfriends.RequestsFriendFragment;

public class TapChatAdapter extends FragmentPagerAdapter {
    private int totalTabs;
    private FragmentManager fm;
    public TapChatAdapter(@NonNull FragmentManager fm, int totalTabs) {
        super(fm);
        this.fm = fm;
        this.totalTabs = totalTabs;
        Log.e("bundle:","TapChatAdapter");
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Log.e("bundle:","DoctorsFragment");
                DoctorsFragment doctorsFragment = new DoctorsFragment();
                return doctorsFragment;
            case 1:
                Log.e("bundle:","NormalUsersFragment");
                NormalUsersFragment usersFragment=new NormalUsersFragment();
                return usersFragment;
            default:
                Log.e("bundle: ","null");

                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
