package com.example.drhello.adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.example.drhello.fragment.fragmentreactions.AllFragment;
import com.example.drhello.fragment.fragmentreactions.AngryFragment;
import com.example.drhello.fragment.fragmentreactions.HahaFragment;
import com.example.drhello.fragment.fragmentreactions.LikeFragment;
import com.example.drhello.fragment.fragmentreactions.LoveFragment;
import com.example.drhello.fragment.fragmentreactions.SadFragment;
import com.example.drhello.fragment.fragmentreactions.WowFragment;
import com.example.drhello.model.UserAccount;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class TabAdapter extends FragmentPagerAdapter {

    private Context myContext;
     private int totalTabs;
     private Map<String,String> map;
    private ArrayList<String>strings=new ArrayList<>();
    private ArrayList<UserAccount> userAccountArrayList = new ArrayList<>();

    public TabAdapter(Context context, FragmentManager fm, int totalTabs,
                      Map<String,String> map, ArrayList<UserAccount> userAccountArrayList, ArrayList<String> strings) {
        super(fm);
        myContext = context;
        this.totalTabs = totalTabs;
        this.map=map;
        this.strings=strings;
        this.userAccountArrayList =userAccountArrayList;
        Log.e("hosstap :",userAccountArrayList.size()+"");
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        switch (position) {
            case 0:
                AllFragment allFragment=new AllFragment();
                bundle.putSerializable("post", (Serializable) map);
                bundle.putSerializable("userAccountArrayList", userAccountArrayList);
                allFragment.setArguments(bundle);
                return allFragment;
            case 1:
                return getSpecialFragment(0);
            case 2:
                return getSpecialFragment(1);
            case 3:
                return getSpecialFragment(2);
            case 4:
                return getSpecialFragment(3);
            case 5:
                return getSpecialFragment(4);
            case 6:
                return getSpecialFragment(5);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }

    private Fragment getSpecialFragment(int position){
        Bundle bundle = new Bundle();
        switch (strings.get(position)){
            case "Like":
                LikeFragment likeFragment=new LikeFragment();
                bundle.putSerializable("post", (Serializable) map);
                bundle.putSerializable("userAccountArrayList", userAccountArrayList);
                likeFragment.setArguments(bundle);
                return likeFragment;
            case "Love":
                LoveFragment loveFragment=new LoveFragment();
                bundle.putSerializable("post", (Serializable) map);
                bundle.putSerializable("userAccountArrayList", userAccountArrayList);
                loveFragment.setArguments(bundle);
                return loveFragment;
            case "Haha":
                HahaFragment hahaFragment=new HahaFragment();
                bundle.putSerializable("post", (Serializable) map);
                bundle.putSerializable("userAccountArrayList", userAccountArrayList);
                hahaFragment.setArguments(bundle);
                return hahaFragment;
            case "Sad":
                SadFragment sadFragment=new SadFragment();
                bundle.putSerializable("post", (Serializable) map);
                bundle.putSerializable("userAccountArrayList", userAccountArrayList);
                sadFragment.setArguments(bundle);
                return sadFragment;
            case "Wow":
                WowFragment wowFragment=new WowFragment();
                bundle.putSerializable("post", (Serializable) map);
                bundle.putSerializable("userAccountArrayList", userAccountArrayList);
                wowFragment.setArguments(bundle);
                return wowFragment;
            case "Angry":
                AngryFragment angryFragment=new AngryFragment();
                bundle.putSerializable("post", (Serializable) map);
                bundle.putSerializable("userAccountArrayList", userAccountArrayList);
                angryFragment.setArguments(bundle);
                return angryFragment;
            default:
                return null;
        }
    }
}
