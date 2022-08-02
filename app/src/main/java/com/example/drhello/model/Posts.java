package com.example.drhello.model;

import androidx.room.Entity;
import com.example.drhello.ui.news.RoomUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Entity(tableName = RoomUtil.TABLE_NAME_POST)

public class Posts implements Serializable {

    private String writePost;
    private String userId;
    private String postId;
    private String nameUser;
    private String imageUser;
    private String TokneId;
    private String date;
    private int reactionNumber;
    private int commentNum;
    private List<String> imgUri=new ArrayList<>();
    private Map<String,String> reactions=new HashMap<>();


    public Posts() {
    }

    public Posts(String writePost, String userId,String postId, List<String> imgUri
            , String date ,String nameUser ,String imageUser,String TokneId
            ,int reactionNumber  , int commentNum,
                 Map<String,String> reactions) {
        this.writePost = writePost;
        this.userId = userId;
        this.postId=postId;
        this.imgUri = imgUri;
        this.date = date;
        this.nameUser = nameUser;
        this.imageUser  = imageUser;
        this.TokneId = TokneId;
        this.reactionNumber = reactionNumber;
        this.commentNum =commentNum;
        this.reactions =reactions ;
    }

    public Map<String, String> getReactions() {
        return reactions;
    }

    public void setReactions(Map<String, String> reactions) {
        this.reactions = reactions;
    }


    public int getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(int commentNum) {
        this.commentNum = commentNum;
    }

    public int getReactionNumber() {
        return reactionNumber;
    }

    public void setReactionNumber(int reactionNumber) {
        this.reactionNumber = reactionNumber;
    }

    public String getTokneId() {
        return TokneId;
    }

    public void setTokneId(String tokneId) {
        TokneId = tokneId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getWritePost() {
        return writePost;
    }

    public void setWritePost(String writePost) {
        this.writePost = writePost;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getImgUri() {
        return imgUri;
    }

    public void setImgUri(List<String> imgUri) {
        this.imgUri = imgUri;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public String getImageUser() {
        return imageUser;
    }

    public void setImageUser(String imageUser) {
        this.imageUser = imageUser;
    }
}
