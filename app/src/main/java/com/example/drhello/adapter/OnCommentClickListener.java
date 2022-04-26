package com.example.drhello.adapter;

import com.example.drhello.model.CommentModel;
import com.example.drhello.model.Posts;

public interface OnCommentClickListener {
    void onClickComment(CommentModel commentModel);
    void selectedReaction(String reaction, CommentModel commentModel);
    void onClickReaction(CommentModel commentModel);
}
