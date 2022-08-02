package com.example.drhello.model;

import java.util.HashMap;
import java.util.Map;

public class CommentRejects {
    private String comment,user_image,user_id,user_name,date,comment_image;
    private Map<String,String> reactions=new HashMap<>();
    private int reactionNumber;
    private String id_outside,id_inside,post_id;
    private String email;

    public CommentRejects() {
    }

    public CommentRejects(String comment, String user_image, String user_id, String user_name,
                            String date , String comment_image
            , Map<String,String> reactions, int reactionNumber) {
        this.comment = comment;
        this.user_image = user_image;
        this.user_id = user_id;
        this.user_name = user_name;
        this.date = date;
        this.comment_image = comment_image;
        this.reactions = reactions;
        this.reactionNumber = reactionNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getComment_image() {
        return comment_image;
    }

    public void setComment_image(String comment_image) {
        this.comment_image = comment_image;
    }

    public Map<String, String> getReactions() {
        return reactions;
    }

    public void setReactions(Map<String, String> reactions) {
        this.reactions = reactions;
    }

    public int getReactionNumber() {
        return reactionNumber;
    }

    public void setReactionNumber(int reactionNumber) {
        this.reactionNumber = reactionNumber;
    }

    public String getId_outside() {
        return id_outside;
    }

    public void setId_outside(String id_outside) {
        this.id_outside = id_outside;
    }

    public String getId_inside() {
        return id_inside;
    }

    public void setId_inside(String id_inside) {
        this.id_inside = id_inside;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }
}
