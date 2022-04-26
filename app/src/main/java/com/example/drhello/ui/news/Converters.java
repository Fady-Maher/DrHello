package com.example.drhello.ui.news;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.room.TypeConverter;

import java.io.ByteArrayOutputStream;

public class Converters {
   @TypeConverter
    public byte[] fromBitmap (Bitmap bitmap){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG,80, outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
       return outputStream.toByteArray();
    }
    @TypeConverter
    public Bitmap toBitmap(byte[] bytes){
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
}
