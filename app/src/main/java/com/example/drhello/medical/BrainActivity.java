package com.example.drhello.medical;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

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
import com.example.drhello.adapter.OnClickDoctorInterface;
import com.example.drhello.R;
import com.example.drhello.adapter.SliderAdapter;
import com.example.drhello.databinding.ActivityBrainBinding;
import com.example.drhello.model.SliderItem;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class BrainActivity extends AppCompatActivity implements OnClickDoctorInterface {
    private ActivityBrainBinding activityBrainBinding;
    private ArrayList<SliderItem> sliderItems = new ArrayList<>();
    private String[] stringsTumor = {"Glioma_Tumor", "Meningioma Tumor", "No Tumor", "Pituitary Tumor"};
    private static final int Gallary_REQUEST_CODE = 1;
    PyObject main_program;
    public static ProgressDialog mProgress;
    private Bitmap bitmap;
    String path = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brain);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            getWindow().setStatusBarColor(Color.WHITE);
        }

        mProgress = new ProgressDialog(BrainActivity.this);
        AsyncTaskD asyncTaskDownload = new AsyncTaskD(path,"first");
        asyncTaskDownload.execute();

        activityBrainBinding = DataBindingUtil.setContentView(BrainActivity.this, R.layout.activity_brain);
        activityBrainBinding.txtResult0.setText(stringsTumor[0]);
        activityBrainBinding.txtResult1.setText(stringsTumor[1]);
        activityBrainBinding.txtResult2.setText(stringsTumor[2]);
        activityBrainBinding.txtResult3.setText(stringsTumor[3]);

        activityBrainBinding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        sliderItems.add(new SliderItem(R.drawable.no_tumor, "No Tumor"));
        sliderItems.add(new SliderItem(R.drawable.pituitary, "Pituitary"));
        sliderItems.add(new SliderItem(R.drawable.meningioma, "Meningioma"));
        sliderItems.add(new SliderItem(R.drawable.pneumonia, "Pneumonia"));

        SliderAdapter sliderAdapter = new SliderAdapter(sliderItems, BrainActivity.this,BrainActivity.this);

        activityBrainBinding.viewPagerImageSlider.setAdapter(sliderAdapter);

        activityBrainBinding.viewPagerImageSlider.startAutoScroll();

        activityBrainBinding.viewPagerImageSlider.setLoopEnabled(true);
        activityBrainBinding.viewPagerImageSlider.setCanTouch(true);


        activityBrainBinding.selImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                String[] mimetypes = {"image/*", "video/*"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                startActivityForResult(intent, Gallary_REQUEST_CODE);
            }
        });

        activityBrainBinding.result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bitmap != null) {
                    if(!path.equals("")){
                        AsyncTaskD asyncTaskDownloadAudio = new AsyncTaskD(path,"");
                        asyncTaskDownloadAudio.execute();
                    }

                    bitmap = null;
                } else {
                    Toast.makeText(BrainActivity.this, "Please, Choose Image First!!", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Gallary_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(BrainActivity.this.getContentResolver(), data.getData());
                activityBrainBinding.imgCorona.setImageBitmap(bitmap);
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

    @Override
    public void OnClick(String spec) {

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
            mProgress.setMessage("Image Processing..");
            mProgress.setCancelable(false);
            mProgress.show();
        }

        @Override
        protected String doInBackground(String... f_url) {
            if(action.equals("first")){
                if (! Python.isStarted()) {
                    Python.start(new AndroidPlatform(BrainActivity.this));//error is here!
                }
                final Python py = Python.getInstance();
                main_program = py.getModule("prolog");
            }else{
                String result = main_program.callAttr("model",path,"Brain").toString();
                String[] listResult = result.split("@");
                int prediction = Integer.parseInt(listResult[0]);
                String probStr = listResult[1].replace("[","")
                        .replace("]","")
                        .replace("\"","");
                 prop = probStr.split(" ");

            }
            mProgress.dismiss();
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            if(!action.equals("first")){
                activityBrainBinding.progress0.setAdProgress((int) (Float.parseFloat(prop[0]) *100));
                activityBrainBinding.progress1.setAdProgress((int) (Float.parseFloat(prop[1]) *100));
                activityBrainBinding.progress2.setAdProgress((int) (Float.parseFloat(prop[2]) *100));
                activityBrainBinding.progress3.setAdProgress((int) (Float.parseFloat(prop[3]) *100));
            }
        }
    }

}