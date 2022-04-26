package com.example.drhello.ui.news;

import com.google.gson.annotations.SerializedName;

public class WeatherModel {
    @SerializedName("temp")
    private String temperature;

    public WeatherModel(String temperature) {
        this.temperature = temperature;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }
}
