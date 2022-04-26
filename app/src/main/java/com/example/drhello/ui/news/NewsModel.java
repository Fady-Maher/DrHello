package com.example.drhello.ui.news;

import android.graphics.Bitmap;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
@Entity(tableName = RoomUtil.TABLE_NAME)
public class NewsModel {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;

    @Ignore
    @SerializedName("urlToImage")
    private String imageUrl;
    @SerializedName("publishedAt")
    private String date;

    @Ignore
    @SerializedName("url")
    private String url;

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private Bitmap image;


    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }


    public String getDescription() {
        return description;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public String getUrl() {
        return url;
    }
}
