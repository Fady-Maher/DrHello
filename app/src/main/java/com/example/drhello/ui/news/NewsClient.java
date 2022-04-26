package com.example.drhello.ui.news;

import io.reactivex.Single;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewsClient {
    private static NewsInterface newsInterface;
    private static NewsClient newsClient;

    public NewsClient(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiUtil.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
            newsInterface = retrofit.create(NewsInterface.class);
    }

    public static NewsClient getNewsClient() {
        if(newsClient == null){
            newsClient = new NewsClient();
        }
        return newsClient;
    }
    public Single<Source> getNews(){
        return newsInterface.getNews(ApiUtil.COUNTRY, ApiUtil.CATEGORY, ApiUtil.API_KEY);
    }

}
