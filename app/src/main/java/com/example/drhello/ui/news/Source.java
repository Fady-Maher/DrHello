package com.example.drhello.ui.news;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Source {
    @SerializedName("articles")
    private List<NewsModel> newsList;


    @SerializedName("main")
    WeatherModel weatherModel;

    public Source() {
    }

    public List<NewsModel> getNewsList() {
        return newsList;
    }

    public WeatherModel getWeatherModel() {
        return weatherModel;
    }

    public void setWeatherModel(WeatherModel weatherModel) {
        this.weatherModel = weatherModel;
    }
}
