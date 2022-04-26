package com.example.drhello.ui.news;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface NewsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNews(NewsModel newsModel);
    @Query("delete from "+RoomUtil.TABLE_NAME)
    Completable deleteAll();
    @Query("select * from "+ RoomUtil.TABLE_NAME)
    Single<List<NewsModel>> getNewsOff();
}
