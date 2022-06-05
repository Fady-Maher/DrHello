package com.example.drhello.medical;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.drhello.BotActivity;
import com.example.drhello.ChatBotlistener;
import com.example.drhello.ShowDialogPython;
import com.example.drhello.adapter.OnClickDoctorInterface;
import com.example.drhello.R;
import com.example.drhello.adapter.SliderAdapter;
import com.example.drhello.databinding.ActivityChestBinding;
import com.example.drhello.model.SliderItem;
import com.example.drhello.textclean.RequestPermissions;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class ChestActivity extends AppCompatActivity implements OnClickDoctorInterface {
    private ActivityChestBinding activityChestBinding;
    private ArrayList<SliderItem> sliderItems=new ArrayList<>();
    private String[] stringsChest = {"Covid19", "Lung Opacity","Normal", "Pneumonia"};
    private static final int Gallary_REQUEST_CODE = 1;
    PyObject main_program;
    String path = "";
    private Bitmap bitmap;
    private RequestPermissions requestPermissions;
    ShowDialogPython showDialogPython;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chest);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) ;
        }else{
            getWindow().setStatusBarColor(Color.WHITE);
        }

        requestPermissions = new RequestPermissions(ChestActivity.this,ChestActivity.this);

        activityChestBinding = DataBindingUtil.setContentView(ChestActivity.this, R.layout.activity_chest);
        activityChestBinding.shimmer.startShimmerAnimation();
        AsyncTaskD asyncTaskDownload = new AsyncTaskD(path,"first");
        asyncTaskDownload.execute();

        activityChestBinding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sliderItems.add(new SliderItem(R.drawable.normal_xray,"Normal"));
        sliderItems.add(new SliderItem(R.drawable.covid19,"Covid19"));
        sliderItems.add(new SliderItem(R.drawable.lung_opacity,"Lung Opacity"));
        sliderItems.add(new SliderItem(R.drawable.pneumonia,"Pneumonia"));

        SliderAdapter sliderAdapter=new SliderAdapter(sliderItems,ChestActivity.this,ChestActivity.this);

        activityChestBinding.viewPagerImageSlider.setAdapter(sliderAdapter);

        activityChestBinding.viewPagerImageSlider.startAutoScroll();

        activityChestBinding.viewPagerImageSlider.setLoopEnabled(true);
        activityChestBinding.viewPagerImageSlider.setCanTouch(true);

        activityChestBinding.selImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (requestPermissions.permissionStorageRead()) {
                    ActivityCompat.requestPermissions(ChestActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            Gallary_REQUEST_CODE);
                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    String[] mimetypes = {"image/*", "video/*"};
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                    startActivityForResult(intent, Gallary_REQUEST_CODE);
                }
            }
        });

        activityChestBinding.result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bitmap != null) {
                    if(!path.equals("")){
                        AsyncTaskD asyncTaskDownloadAudio = new AsyncTaskD(path,"");
                        asyncTaskDownloadAudio.execute();
                    }

                    bitmap = null;
                }else{
                    Toast.makeText(ChestActivity.this, "Please, Choose Image First!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Gallary_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(ChestActivity.this.getContentResolver(), data.getData());
                activityChestBinding.imgCorona.setImageBitmap(bitmap);
                File file = new File(getRealPathFromURI(getImageUri(getApplicationContext(),bitmap)));
                Log.e("file: ", file.getPath());
                path = file.getPath();
            } catch (IOException e) {
                Log.e("gallary exception: ", e.getMessage());
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
        }
    }

    @Override
    public void OnClick(String spec) {

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "IMG_" + Calendar.getInstance().getTime(), null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri,
                null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        Log.e("result: " ,cursor.getString(idx)+"     1");
        String result = cursor.getString(idx);
        cursor.close();
        return result;
    }


    public class AsyncTaskD extends AsyncTask<String, String, String> {

        String path;
        String action;
        String[] prop;
        public AsyncTaskD(String path,String action){
            this.path = path;
            this.action = action;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialogPython = new ShowDialogPython(ChestActivity.this,ChestActivity.this.getLayoutInflater(),"load");
        }

        @Override
        protected String doInBackground(String... f_url) {
            if(action.equals("first")){
                if (! Python.isStarted()) {
                    Python.start(new AndroidPlatform(ChestActivity.this));//error is here!
                }
                final Python py = Python.getInstance();
                main_program = py.getModule("prolog");
            }else{
                String result = main_program.callAttr("model",path,"Corona").toString();
                String[] listResult = result.split("@");
                int prediction = Integer.parseInt(listResult[0]);
                String probStr = listResult[1].replace("[","")
                        .replace("]","")
                        .replace("\"","");
                prop = probStr.split(" ");
            }
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            if(!action.equals("first")){
                activityChestBinding.progresscovid.setAdProgress((int) (Float.parseFloat(prop[0]) *100));
                activityChestBinding.progresslung.setAdProgress((int) (Float.parseFloat(prop[1]) *100));
                activityChestBinding.progressnormal.setAdProgress((int) (Float.parseFloat(prop[2]) *100));
                activityChestBinding.progresspneu.setAdProgress((int) (Float.parseFloat(prop[3]) *100));
            }
            showDialogPython.dismissDialog();
        }
    }
}