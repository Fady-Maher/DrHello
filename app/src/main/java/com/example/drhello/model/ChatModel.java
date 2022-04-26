package com.example.drhello.model;

import java.io.Serializable;

public class ChatModel implements Serializable {
    String message, date,senderid ,recieveid, image, nameSender , record ;

    public ChatModel() {
    }

    public ChatModel(String message, String date, String senderid, String recieveid, String image, String nameSender, String record) {
        this.message = message;
        this.date = date;
        this.senderid = senderid;
        this.recieveid = recieveid;
        this.image = image;
        this.nameSender = nameSender;
        this.record = record;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    public String getRecieveid() {
        return recieveid;
    }

    public void setRecieveid(String recieveid) {
        this.recieveid = recieveid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNameSender() {
        return nameSender;
    }

    public void setNameSender(String nameSender) {
        this.nameSender = nameSender;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }
}
