package com.example.drhello.model;

public class UserState {

    private String user_name,img_user,img_state;

    public UserState() {
    }

    public UserState(String img_user, String img_state, String user_name) {
        this.user_name = user_name;
        this.img_user = img_user;
        this.img_state = img_state;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getImg_user() {
        return img_user;
    }

    public void setImg_user(String img_user) {
        this.img_user = img_user;
    }

    public String getImg_state() {
        return img_state;
    }

    public void setImg_state(String img_state) {
        this.img_state = img_state;
    }
}
