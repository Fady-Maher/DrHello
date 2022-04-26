package com.example.drhello.model;

public class ReactionModel {
    private String name_user,img_user,reaction;

    public ReactionModel() {
    }

    public ReactionModel(String name_user, String img_user, String reaction) {
        this.name_user = name_user;
        this.img_user = img_user;
        this.reaction = reaction;
    }

    public String getName_user() {
        return name_user;
    }

    public void setName_user(String name_user) {
        this.name_user = name_user;
    }

    public String getImg_user() {
        return img_user;
    }

    public void setImg_user(String img_user) {
        this.img_user = img_user;
    }

    public String getReaction() {
        return reaction;
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
    }
}
