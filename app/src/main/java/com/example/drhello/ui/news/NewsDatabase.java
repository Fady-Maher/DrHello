package com.example.drhello.ui.news;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = NewsModel.class,version = RoomUtil.DATABASE_VERSION,exportSchema = false)
@TypeConverters(Converters.class)
public abstract class NewsDatabase extends RoomDatabase {
    private static NewsDatabase newsDatabase ;
    public abstract NewsDao newsDao();

    public static synchronized NewsDatabase getNewsDatabase(Context context ){
        if(newsDatabase == null){
            newsDatabase = Room.databaseBuilder(context,NewsDatabase.class, RoomUtil.DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return newsDatabase;
    }
}
