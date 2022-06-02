package com.example.drhello.model;

import com.example.drhello.ui.profile.UserInformation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserAccount implements Serializable {
    private String img_profile , name , email , pass ,date , id , tokenID , sign_up_method ,state,follows;
    private Map<String, LastChat> map = new HashMap<>();
    private UserInformation userInformation;
    private Map<String, AddPersonModel> friendsmap = new HashMap<>();
    private Map<String, AddPersonModel> requests = new HashMap<>();
    private Map<String, AddPersonModel> requestSsent = new HashMap<>();
    private int followers = 0;
    private Map<String, FollowersModel> followersModelMap = new HashMap<>();
    private ArrayList<String> postArray = new ArrayList<>();

    public UserInformation getUserInformation() {
        return userInformation;
    }

    public void setUserInformation(UserInformation userInformation) {
        this.userInformation = userInformation;
    }

    public UserAccount() {
    }


    public UserAccount(String img_profile, String name, String email, String pass, String tokenID, String sign_up_method) {
        this.img_profile = img_profile;
        this.name = name;
        this.email = email;
        this.pass = pass;
        this.tokenID = tokenID;
        this.sign_up_method = sign_up_method;
    }

    public UserAccount(String img_profile, String name, String email, String pass, String date, String id, String tokenID, String sign_up_method) {
        this.img_profile = img_profile;
        this.name = name;
        this.email = email;
        this.pass = pass;
        this.date = date;
        this.id = id;
        this.tokenID = tokenID;
        this.sign_up_method = sign_up_method;
    }

    public Map<String, LastChat> getMap() {
        return map;
    }

    public void setMap(Map<String, LastChat> map) {
        this.map = map;
    }

    public String getImg_profile() {
        return img_profile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTokenID() {
        return tokenID;
    }

    public void setTokenID(String tokenID) {
        this.tokenID = tokenID;
    }

    public String getSign_up_method() {
        return sign_up_method;
    }

    public void setSign_up_method(String sign_up_method) {
        this.sign_up_method = sign_up_method;
    }


    public Map<String, AddPersonModel> getFriendsmap() {
        return friendsmap;
    }

    public void setFriendsmap(Map<String, AddPersonModel> friendsmap) {
        this.friendsmap = friendsmap;
    }

    public Map<String, AddPersonModel> getRequests() {
        return requests;
    }

    public void setRequests(Map<String, AddPersonModel> requests) {
        this.requests = requests;
    }

    public Map<String, AddPersonModel> getRequestSsent() {
        return requestSsent;
    }

    public void setRequestSsent(Map<String, AddPersonModel> requestSsent) {
        this.requestSsent = requestSsent;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setImg_profile(String img_profile) {
        this.img_profile = img_profile;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public String getFollows() {
        return follows;
    }

    public void setFollows(String follows) {
        this.follows = follows;
    }

    public Map<String, FollowersModel> getFollowersModelMap() {
        return followersModelMap;
    }

    public void setFollowersModelMap(Map<String, FollowersModel> followersModelMap) {
        this.followersModelMap = followersModelMap;
    }

    public ArrayList<String> getPostArray() {
        return postArray;
    }

    public void setPostArray(ArrayList<String> postArray) {
        this.postArray = postArray;
    }
}
