package com.example.drhello.medical;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.drhello.OnClickDoctorInterface;
import com.example.drhello.R;
import com.example.drhello.adapter.SliderAdapter;
import com.example.drhello.databinding.ActivityHeartBinding;
import com.example.drhello.model.SliderItem;
import com.example.drhello.textclean.RequestPermissions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class HeartActivity extends AppCompatActivity implements OnClickDoctorInterface {
    private ActivityHeartBinding activityHeartBinding;
    private String[] stringsHeart = {"Fusion", "Normal", "Supraventricular",
            "Unknown", "Ventricular"};
    private static final int Gallary_REQUEST_CODE = 1;
    PyObject main_program;
    public static ProgressDialog mProgress;
    private Bitmap bitmap;
    private RequestPermissions requestPermissions;
    String path = "";
    private ArrayList<SliderItem> sliderItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            getWindow().setStatusBarColor(Color.WHITE);
        }

        requestPermissions = new RequestPermissions(HeartActivity.this,HeartActivity.this);

        mProgress = new ProgressDialog(HeartActivity.this);
        AsyncTaskD asyncTaskDownload = new AsyncTaskD(path,"first");
        asyncTaskDownload.execute();

        activityHeartBinding = DataBindingUtil.setContentView(HeartActivity.this, R.layout.activity_heart);

        activityHeartBinding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sliderItems.add(new SliderItem(R.drawable.actinic_keratoses, "Actinic keratoses"));
        sliderItems.add(new SliderItem(R.drawable.basal_cell_carcinoma, "Basal Cell Carcinoma"));
        sliderItems.add(new SliderItem(R.drawable.benign_keratosis_like, "Benign Keratosis Like"));
        sliderItems.add(new SliderItem(R.drawable.der, "Dermatofibroma"));
        sliderItems.add(new SliderItem(R.drawable.melanocytic_nevi, "Melanocytic Nevi"));
        sliderItems.add(new SliderItem(R.drawable.vascular_lesions, "Vascular Lesions"));


        SliderAdapter sliderAdapter = new SliderAdapter(sliderItems, HeartActivity.this,HeartActivity.this);


        activityHeartBinding.viewPagerImageSlider.setAdapter(sliderAdapter);

        activityHeartBinding.viewPagerImageSlider.startAutoScroll();

        activityHeartBinding.viewPagerImageSlider.setLoopEnabled(true);
        activityHeartBinding.viewPagerImageSlider.setCanTouch(true);

        activityHeartBinding.selImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (requestPermissions.permissionStorageRead()) {
                    ActivityCompat.requestPermissions(HeartActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
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

        activityHeartBinding.result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bitmap != null) {
                    if(!path.equals("")){
                        AsyncTaskD asyncTaskDownloadAudio = new AsyncTaskD(path,"");
                        asyncTaskDownloadAudio.execute();
                    }
                    bitmap = null;
                }else{
                    Toast.makeText(HeartActivity.this, "Please, Choose Image First!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Gallary_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(HeartActivity.this.getContentResolver(), data.getData());
                activityHeartBinding.imgCorona.setImageBitmap(bitmap);
                File file = new File(getRealPathFromURI(getImageUri(getApplicationContext(),bitmap)));
                Log.e("file: ", file.getPath());
                path = file.getPath();
            } catch (IOException e) {
                Log.e("gallary exception: ", e.getMessage());
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // Toast.makeText(getBaseContext(), "Canceled", Toast.LENGTH_SHORT).show();
        }
    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
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

    @Override
    public void OnClick(String spec) {

    }


    public class AsyncTaskD extends AsyncTask<String, String, String> {

        String path;
        String action;
        public AsyncTaskD(String path,String action){
            this.path = path;
            this.action = action;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgress.setMessage("Image Processing..");
            mProgress.setCancelable(false);
            mProgress.show();
        }

        @Override
        protected String doInBackground(String... f_url) {
            if(action.equals("first")){
                if (! Python.isStarted()) {
                    Python.start(new AndroidPlatform(HeartActivity.this));//error is here!
                }
                final Python py = Python.getInstance();
                main_program = py.getModule("prolog");
            }else{
                String result = main_program.callAttr("model",path,"Heart").toString();
                String[] listResult = result.split("@");
                int prediction = Integer.parseInt(listResult[0]);
                String probStr = listResult[1].replace("[","")
                        .replace("]","")
                        .replace("\"","");
                String[] prop = probStr.split(" ");
                if (prediction == 0) {
                    activityHeartBinding.txtResult.setText(stringsHeart[0] + " :  " + String.format("%.2f", Float.parseFloat(prop[0]) * 100) );
                } else if (prediction == 1) {
                    activityHeartBinding.txtResult.setText(stringsHeart[1] + " :  " + String.format("%.2f", Float.parseFloat(prop[1]) * 100) );
                } else if (prediction == 2) {
                    activityHeartBinding.txtResult.setText(stringsHeart[2] + " :  " + String.format("%.2f", Float.parseFloat(prop[2]) * 100) );
                } else if (prediction == 3) {
                    activityHeartBinding.txtResult.setText(stringsHeart[3] + " :  " + String.format("%.2f", Float.parseFloat(prop[3]) * 100) );
                }else if (prediction == 4) {
                    activityHeartBinding.txtResult.setText(stringsHeart[4] + " :  " + String.format("%.2f", Float.parseFloat(prop[4]) * 100) );
                }
            }
            mProgress.dismiss();
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
        }
    }
}