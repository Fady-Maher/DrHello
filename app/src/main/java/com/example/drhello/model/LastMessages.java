package com.example.drhello.model;

import java.io.Serializable;

public class LastMessages implements Serializable {
    String id,image_person,name_person,date,image,message,nameSender,recieveid,record,senderid;

    public LastMessages() {
    }

    public LastMessages(String id, String image_person, String name_person,
                        String date, String image, String message, String nameSender,
                        String recieveid, String record, String senderid) {
        this.id = id;
        this.image_person = image_person;
        this.name_person = name_person;
        this.date = date;
        this.image = image;
        this.message = message;
        this.nameSender = nameSender;
        this.recieveid = recieveid;
        this.record = record;
        this.senderid = senderid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage_person() {
        return image_person;
    }

    public void setImage_person(String image_person) {
        this.image_person = image_person;
    }

    public String getName_person() {
        return name_person;
    }

    public void setName_person(String name_person) {
        this.name_person = name_person;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public String getRecieveid() {
        return recieveid;
    }

    public void setRecieveid(String recieveid) {
        this.recieveid = recieveid;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }
}
