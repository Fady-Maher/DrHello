package com.example.drhello.ui.mapping;

import com.google.android.gms.maps.model.LatLng;

public class PlaceDetails {
    int place_img;
    String place_loc,place_name,distance,time,speed,place_address, countryName, governorate ,subaddress;
    double distance_double;
    LatLng place_latLng;

    public PlaceDetails() {
    }

    public PlaceDetails(int place_img, String place_loc, String place_name, String distance, String time, String speed,String place_address,
                        String countryName, String governorate, String subaddress, double distance_double, LatLng place_latLng) {
        this.place_img = place_img;
        this.place_loc = place_loc;
        this.place_name = place_name;
        this.distance = distance;
        this.time = time;
        this.speed = speed;
        this.place_address= place_address;
        this.countryName = countryName;
        this.governorate = governorate;
        this.subaddress = subaddress;
        this.distance_double = distance_double;
        this.place_latLng = place_latLng;
    }

    public int getPlace_img() {
        return place_img;
    }


    public String getPlace_loc() {
        return place_loc;
    }


    public String getPlace_name() {
        return place_name;
    }


    public String getDistance() {
        return distance;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSpeed() {
        return speed;
    }

    public String getPlace_address() {
        return place_address;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getGovernorate() {
        return governorate;
    }

    public String getSubaddress() {
        return subaddress;
    }

    public double getDistance_double() {
        return distance_double;
    }

    public LatLng getPlace_latLng() {
        return place_latLng;
    }

}
