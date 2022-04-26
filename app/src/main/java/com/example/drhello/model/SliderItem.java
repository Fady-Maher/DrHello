package com.example.drhello.model;

public class SliderItem {
    private int image;
    private String img_name;

    public SliderItem(int image, String img_name) {
        this.image = image;
        this.img_name = img_name;
    }

    public SliderItem() {
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getImg_name() {
        return img_name;
    }

    public void setImg_name(String img_name) {
        this.img_name = img_name;
    }
}
