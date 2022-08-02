package com.example.drhello;

public class ScreenItem {
    int screenImage,img_below;
    String type;

    public ScreenItem(int img_below, int screenImage,String type) {
        this.screenImage = screenImage;
        this.img_below = img_below;
        this.type = type;

    }


    public int getScreenImage() {
        return screenImage;
    }

    public void setScreenImage(int screenImage) {
        this.screenImage = screenImage;
    }

    public int getImg_below() {
        return img_below;
    }

    public void setImg_below(int img_below) {
        this.img_below = img_below;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
