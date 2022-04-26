package com.example.drhello.ui.news;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherInterface {
    @GET("weather")
    Single<Source> getWeather(
            @Query("lat") String lat,
            @Query("lon") String lon,
            @Query("appid") String apiKey);
}
