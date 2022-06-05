package com.example.drhello.model;

import java.io.Serializable;

public class UserInformation implements Serializable {
    private String type,country,address_home,city,state_address,phone
            ,address_work,name_education,date_of_birth,gender,specification,
            image_proof,name_clinic, specification_in;

    public UserInformation() {
    }

    public UserInformation(String country, String address_home, String city,
                           String state_address,
                           String phone, String address_work,
                           String name_education, String date_of_birth,
                           String gender,String type) {
        this.country = country;
        this.address_home = address_home;
        this.city = city;
        this.state_address = state_address;
        this.phone = phone;
        this.address_work = address_work;
        this.name_education = name_education;
        this.date_of_birth = date_of_birth;
        this.gender = gender;
        this.type = type;
    }

    public UserInformation(String type, String country, String address_home,
                           String city, String state_address, String phone,
                           String address_work, String name_education,
                           String date_of_birth, String gender,
                           String image_proof, String name_clinic) {
        this.type = type;
        this.country = country;
        this.address_home = address_home;
        this.city = city;
        this.state_address = state_address;
        this.phone = phone;
        this.address_work = address_work;
        this.name_education = name_education;
        this.date_of_birth = date_of_birth;
        this.gender = gender;
        this.image_proof = image_proof;
        this.name_clinic = name_clinic;
    }

    public String getSpecification_in() {
        return specification_in;
    }

    public void setSpecification_in(String specification_in) {
        this.specification_in = specification_in;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getImage_proof() {
        return image_proof;
    }

    public void setImage_proof(String image_proof) {
        this.image_proof = image_proof;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAddress_home() {
        return address_home;
    }

    public void setAddress_home(String address_home) {
        this.address_home = address_home;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState_address() {
        return state_address;
    }

    public void setState_address(String state_address) {
        this.state_address = state_address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress_work() {
        return address_work;
    }

    public void setAddress_work(String address_work) {
        this.address_work = address_work;
    }

    public String getName_education() {
        return name_education;
    }

    public void setName_education(String name_education) {
        this.name_education = name_education;
    }

    public String getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(String date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
