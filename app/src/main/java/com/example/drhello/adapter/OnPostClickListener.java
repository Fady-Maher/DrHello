package com.example.drhello.adapter;

import android.view.MenuItem;
import android.widget.PopupMenu;

import com.example.drhello.model.Posts;

public interface OnPostClickListener {
    void onClickImage(String uri);
    void onClickNumReaction(Posts posts);
    void onClickComment(Posts posts);
    void onClickShare(Posts posts);
    void selectedReaction(String reaction,Posts posts);
    void onClickProfile(int position,String id);
    void onClickOption(int position, Posts posts, MenuItem menuItem );
}
