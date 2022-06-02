package com.example.drhello.model;

import java.io.PipedReader;
import java.io.Serializable;

public class FollowersModel implements Serializable {
    private String id;

    public FollowersModel() {
    }

    public FollowersModel(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
