package com.example.drhello.model;

import java.io.Serializable;

public class LastChat implements Serializable {
    String idFriend,image_person,date,message,nameSender;

    public LastChat() {
    }

    public LastChat( String idFriend,String image_person, String date, String message, String nameSender) {
        this.idFriend = idFriend;
        this.image_person = image_person;
        this.date = date;
        this.message = message;
        this.nameSender = nameSender;
    }

    public String getImage_person() {
        return image_person;
    }

    public void setImage_person(String image_person) {
        this.image_person = image_person;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNameSender() {
        return nameSender;
    }

    public void setNameSender(String nameSender) {
        this.nameSender = nameSender;
    }

    public String getIdFriend() {
        return idFriend;
    }

    public void setIdFriend(String idFriend) {
        this.idFriend = idFriend;
    }
}
