package com.example.drhello.model;

public class ChatBotModel {
    String text,date,temp;
    int type ;

    public ChatBotModel() {
    }

    public ChatBotModel(String text, String date ,int type,String temp) {
        this.text = text;
        this.date = date;
        this.type = type;
        this.temp  = temp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }
}
