package com.example.drhello.ui.writepost;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.drhello.R;
import com.example.drhello.ui.chats.StateOfUser;
import com.example.drhello.textclean.RequestPermissions;
import com.example.drhello.databinding.ActivityShowImageBinding;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ShowImageActivity extends AppCompatActivity {

    private static final int CODE = 1000 ;
    Animation rotateOpen, rotateClose, fromBottom, toBottom;
    Bitmap bitmap = null;
    private static final String PHOTOS_FOLDER = "Photos";
    boolean clicked = false;
    private String uri;
    private byte[] image_show;
    private byte[] byteArray;
    private ActivityShowImageBinding activityShowImageBinding;
    private RequestPermissions requestPermissions;
    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        requestPermissions = new RequestPermissions(ShowImageActivity.this,ShowImageActivity.this);
        activityShowImageBinding = DataBindingUtil.setContentView(this, R.layout.activity_show_image);

        rotateOpen = AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim);
        fromBottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim);


        uri = getIntent().getStringExtra("uri_image");
        Log.e("url  : " ,uri);
        if(uri != null){
            try{
                Glide.with(ShowImageActivity.this).load(uri).
                        placeholder(R.drawable.ic_chat).
                        error(R.drawable.ic_chat).into(activityShowImageBinding.imageViewShow);
            }catch (Exception e){
                activityShowImageBinding.imageViewShow.setImageResource(R.drawable.ic_chat);
                Log.e("url  catch : " ,e.getMessage());
            }
        }



        activityShowImageBinding.fabAdd.setOnClickListener(v -> onAddButtonClicked());

        activityShowImageBinding.fabSaveImage.setOnClickListener(v -> {
            if(isStoragePermissionGranted()){
                if(bitmap!=null){
                    savePhoto(bitmap,getFilename());
                }
            }
        });

        activityShowImageBinding.fabShareImage.setOnClickListener(v -> {
            if(isStoragePermissionGranted()){
                if(bitmap!=null){
                    Log.e("bitmap: " , 1 +"");
                    savePhotoAndShare(bitmap);
                }else{
                    Log.e("bitmap: " , "else");
                }
            }else{
            Log.e("bitmap: " , "isStoragePermissionGranted");
        }
        });

    }
    private void getBitmapFromImage() {
        try {
            bitmap = ((BitmapDrawable) activityShowImageBinding.imageViewShow.getDrawable()).getBitmap();
            Log.e("url  try : " ,uri);
        } catch (Exception E) {
            bitmap = Bitmap.createBitmap(activityShowImageBinding.imageViewShow.getDrawable().getIntrinsicWidth(),
                    activityShowImageBinding.imageViewShow.getDrawable().getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            activityShowImageBinding.imageViewShow.getDrawable().setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            activityShowImageBinding.imageViewShow.getDrawable().draw(canvas);
            Log.e("catch  catch : " ,E.getMessage());
        }
    }

    private void shareImage(Uri uri) {
        //Toast.makeText(this, uri.toString(), Toast.LENGTH_SHORT).show();
        Intent n = new Intent();
        n.setAction(Intent.ACTION_SEND);
        n.setType("image/*");
        n.putExtra(Intent.EXTRA_STREAM, uri);
        Intent n1 = Intent.createChooser(n, "share Image");
        try {
            startActivity(n1);
            Toast.makeText(this, "here", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {

            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    void savePhotoAndShare(Bitmap bitmap) {
        try {
            String filename = getFilename();
            File outputFile = new File(filename);
            savePhoto(bitmap,filename);
            Log.e("savePhotoAndShare :","1");
            shareImage(FileProvider.getUriForFile(this, "com.example.drhello.provider", outputFile));

        } catch (Exception e) {
            Log.e("error share image :",e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }

    private void onAddButtonClicked() {
        setVisibility();
        setAnimation();
        setClickable();
        clicked = !clicked;
    }

    private void setVisibility() {
        if (!clicked) {
            activityShowImageBinding.fabShareImage.setVisibility(View.VISIBLE);
            activityShowImageBinding.fabSaveImage.setVisibility(View.VISIBLE);
        } else {
            activityShowImageBinding.fabShareImage.setVisibility(View.INVISIBLE);
            activityShowImageBinding.fabSaveImage.setVisibility(View.INVISIBLE);
        }
    }

    private void setAnimation() {
        if(!clicked){
            activityShowImageBinding.fabSaveImage.startAnimation(fromBottom);
            activityShowImageBinding.fabShareImage.startAnimation(fromBottom);
            activityShowImageBinding.fabAdd.startAnimation(rotateOpen);
        }else{
            activityShowImageBinding.fabSaveImage.startAnimation(toBottom);
            activityShowImageBinding.fabShareImage.startAnimation(toBottom);
            activityShowImageBinding.fabAdd.startAnimation(rotateClose);
        }
    }

    void setClickable(){
        if(!clicked){
            activityShowImageBinding.fabShareImage.setClickable(true);
            activityShowImageBinding.fabSaveImage.setClickable(true);
        }else{
            activityShowImageBinding.fabShareImage.setClickable(false);
            activityShowImageBinding.fabSaveImage.setClickable(false);
        }
    }

    private void savePhoto(Bitmap bitmap,String filename) {
        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
            Toast.makeText(getApplicationContext(),"Error when save photo!!",Toast.LENGTH_SHORT).show();
            Log.e("error : ",e.getMessage());
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
        try {
            outStream.flush();
            Toast.makeText(getApplicationContext(),"Successful save photo!!",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(),"Error when save photo!!",Toast.LENGTH_SHORT).show();
            Log.e("error : ",e.getMessage());
        }
        try {
            Toast.makeText(getApplicationContext(),"Successful save photo!!",Toast.LENGTH_SHORT).show();
            outStream.close();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(),"Error when save photo!!",Toast.LENGTH_SHORT).show();
            Log.e("error : ",e.getMessage());
        }
    }

    public String getFilename(){

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
                ,PHOTOS_FOLDER);
        Log.e("file : ", file.getAbsolutePath());
        if(!file.exists()){
            file.mkdirs();
        }

        return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!requestPermissions.permissionStorage()) {
                getBitmapFromImage();
                return true;
            } else {

                ActivityCompat.requestPermissions(ShowImageActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CODE);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case CODE:{
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(ShowImageActivity.this, "Permission granted", Toast.LENGTH_SHORT).show();
                    getBitmapFromImage();
                    savePhoto(bitmap,getFilename());
                } else {
                    Toast.makeText(ShowImageActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();

                }
                return;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        StateOfUser stateOfUser = new StateOfUser();
        stateOfUser.changeState("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        StateOfUser stateOfUser = new StateOfUser();
        stateOfUser.changeState("Offline");
    }


}