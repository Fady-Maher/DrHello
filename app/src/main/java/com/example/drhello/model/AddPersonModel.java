package com.example.drhello.model;

import java.io.Serializable;

public class AddPersonModel implements Serializable {
    private String name_person,image_person,id,type;

    public AddPersonModel() {
    }

    public AddPersonModel(String name_person, String image_person,String id,String type) {
        this.name_person = name_person;
        this.image_person = image_person;
        this.id = id;
        this.type = type;
    }

    public String getName_person() {
        return name_person;
    }

    public void setName_person(String name_person) {
        this.name_person = name_person;
    }

    public String getImage_person() {
        return image_person;
    }

    public void setImage_person(String image_person) {
        this.image_person = image_person;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
