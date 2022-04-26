package com.example.drhello.ui.news;

import io.reactivex.Single;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherClient {
    private static WeatherInterface weatherInterface;
    private static WeatherClient weatherClient;

    public WeatherClient(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiUtil.BASE_URL_WEATHER)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        weatherInterface = retrofit.create(WeatherInterface.class);
    }

    public static WeatherClient getNewsClient() {
        if(weatherClient == null){
            weatherClient = new WeatherClient();
        }
        return weatherClient;
    }
    public Single<Source> getWeather(String lat , String log){
        return weatherInterface.getWeather(lat, log, ApiUtil.API_KEY_WEATHER);
    }

}