package com.example.drhello.adapter;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.example.drhello.fragment.fragmentfriends.AddFriendFragment;
import com.example.drhello.fragment.fragmentfriends.RequestsFriendFragment;

public class TapFriendAdapter extends FragmentPagerAdapter {

    private Context context;
    private int totalTabs;

    public TapFriendAdapter(@NonNull FragmentManager fm,int totalTabs,Context context ) {
        super(fm);
        this.context = context;
        this.totalTabs = totalTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        switch (position) {
            case 0:
                AddFriendFragment addFriendFragment=new AddFriendFragment();
                addFriendFragment.setArguments(bundle);
                return addFriendFragment;
            case 1:
                RequestsFriendFragment requestsFriendFragment=new RequestsFriendFragment();
                requestsFriendFragment.setArguments(bundle);
                return requestsFriendFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
