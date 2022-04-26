package com.example.drhello.model;

public class ReactionType {
    String reactionType;

    public ReactionType() {
    }


    public ReactionType(String reactionType,String idUser) {
        this.reactionType = reactionType;
    }

    public String getReactionType() {
        return reactionType;
    }

    public void setReactionType(String reactionType) {
        this.reactionType = reactionType;
    }
}
