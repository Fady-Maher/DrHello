package com.example.drhello.adapter;

import com.example.drhello.model.Posts;

public interface OnPostClickListener {
    void onClickImage(String uri);
    void onClickNumReaction(Posts posts);
    void onClickComment(Posts posts);
    void selectedReaction(String reaction,Posts posts);
}
