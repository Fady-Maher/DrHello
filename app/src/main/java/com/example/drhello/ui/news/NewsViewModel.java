package com.example.drhello.ui.news;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Maybe;
import io.reactivex.MaybeObserver;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.android.schedulers.AndroidSchedulers;


public class NewsViewModel extends ViewModel {
    private static final String TAG = "NewsViewModel";
    MutableLiveData<List<NewsModel>> newsMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<Source> weatherMutableLiveData = new MutableLiveData<>();

    public void getNews() {
        Maybe<Source> single = NewsClient.getNewsClient().getNews()
                .subscribeOn(Schedulers.io())
                .filter(new Predicate<Source>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public boolean test(@NonNull Source source) {
                        for(NewsModel news : source.getNewsList()){
                            String timeModified = news.getDate().substring(0,19) + ".000" + news.getDate().substring(19);
                            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
                            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a", Locale.ENGLISH);
                            LocalDateTime date = LocalDateTime.parse(timeModified, inputFormatter);
                            String formattedDate = outputFormatter.format(date);
                            news.setDate(formattedDate);
                        }
                        return true;
                    }
                }).observeOn(AndroidSchedulers.mainThread());
        MaybeObserver<Source> singleObserver = new MaybeObserver<Source>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(Source value) {
                newsMutableLiveData.setValue(value.getNewsList());

            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG, "onError: " + e);
            }

            @Override
            public void onComplete() {

            }
        };
        single.subscribe(singleObserver);
    }



    public void deleteNews(Context context) {
        NewsDatabase.getNewsDatabase(context).newsDao()
                .deleteAll().subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: Deleted");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }
                });
    }

    public void getNewsOffline(Context context) {
        NewsDatabase.getNewsDatabase(context).newsDao()
                .getNewsOff().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<NewsModel>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull List<NewsModel> newsModels) {
                        newsMutableLiveData.setValue(newsModels);
                      //  Log.d(TAG, "onSuccess: " + newsModels.get(1).getImage());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                     //   Log.d(TAG, "onError: in Getting News from room " + e.getMessage());
                    }

                });
    }
    @SuppressLint("CheckResult")
    public void insertNewsModelOffline(Context context, List<NewsModel> newsListOff) {
        Completable completable1 = Completable.fromAction(() -> url2Bitmap(context,newsListOff));
        Completable completable2 = Completable.fromAction(() -> {
            Thread.sleep(500);
            insertNews(context,newsListOff);
        });
        completable1.mergeWith(completable2)
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: Insertion");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(TAG, "onError: "+e);
                    }
                });
    }
    public void insertNews(Context context, List<NewsModel> newsModelList){
        NewsDatabase newsDatabase = NewsDatabase.getNewsDatabase(context);
        for (NewsModel newsModel : newsModelList) {
            Log.d(TAG, "insertNewsModel: " + newsModel.getImage());
            newsDatabase.newsDao().insertNews(newsModel);
        }
    }
    public void url2Bitmap(Context context, List<NewsModel> newsModelList){
        for(NewsModel newsModel : newsModelList) {
            Glide.with(context)
                    .asBitmap()
                    .load(newsModel.getImageUrl())
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            Bitmap resized = Bitmap.createScaledBitmap(resource,600, 800, true);
                            newsModel.setImage(resized);
                            Log.d(TAG, "onResourceReady: " + newsModel.getImage());
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });
        }
    }

    public void getWeather(double latitude, double longitude){
        Single<Source> source = WeatherClient.getNewsClient().getWeather(latitude+"",longitude+"")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        SingleObserver<Source> singleObserver = new SingleObserver<Source>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(Source source) {
                Log.d(TAG, "onSuccess Getting Weather: " + source.getWeatherModel().getTemperature());
                weatherMutableLiveData.setValue(source);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage() );
            }
        };
        source.subscribe(singleObserver);
    }
}
