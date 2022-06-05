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
import com.example.drhello.ShowDialogPython;
import com.example.drhello.adapter.OnClickDoctorInterface;
import com.example.drhello.R;
import com.example.drhello.adapter.SliderAdapter;
import com.example.drhello.databinding.ActivitySkinBinding;
import com.example.drhello.model.SliderItem;
import com.example.drhello.textclean.RequestPermissions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class SkinActivity extends AppCompatActivity implements OnClickDoctorInterface {
    private ActivitySkinBinding activitySkinBinding;
    private ArrayList<SliderItem> sliderItems = new ArrayList<>();
    /*
    * {0: 'Actinic_keratoses',
            1: 'Basal_Cell_Carcinoma',
            2: 'Benign_Keratosis_Like',
            3: 'Dermatofibroma',
            4: 'Melanocytic_Nevi',
            5: 'Melanoma',
            6: 'Vascular_Lesions'}
            * */
    private String[] stringsSkin = {"Actinic_keratoses", "Basal_Cell_Carcinoma", "Benign_Keratosis_Like",
            "Dermatofibroma", "Melanocytic_Nevi","Melanoma", "Vascular_Lesions"};
    private static final int Gallary_REQUEST_CODE = 1;
    PyObject main_program;
    private Bitmap bitmap;
    private RequestPermissions requestPermissions;
    String path = "";
    ShowDialogPython showDialogPython;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skin);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            getWindow().setStatusBarColor(Color.WHITE);
        }

        requestPermissions = new RequestPermissions(SkinActivity.this,SkinActivity.this);

        AsyncTaskD asyncTaskDownload = new AsyncTaskD(path,"first");
        asyncTaskDownload.execute();

        activitySkinBinding = DataBindingUtil.setContentView(SkinActivity.this, R.layout.activity_skin);
        activitySkinBinding.shimmer.startShimmerAnimation();
        activitySkinBinding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sliderItems.add(new SliderItem(R.drawable.actinic_keratoses, "Actinic keratoses"));
        sliderItems.add(new SliderItem(R.drawable.basal_cell_carcinoma, "BCC"));
        sliderItems.add(new SliderItem(R.drawable.benign_keratosis_like, "BKL"));
        sliderItems.add(new SliderItem(R.drawable.der, "Dermatofibroma"));
        sliderItems.add(new SliderItem(R.drawable.melonskin, "Dermatofibroma"));
        sliderItems.add(new SliderItem(R.drawable.melanocytic_nevi, "Melanocytic Nevi"));
        sliderItems.add(new SliderItem(R.drawable.vascular_lesions, "Vascular Lesions"));


        SliderAdapter sliderAdapter = new SliderAdapter(sliderItems, SkinActivity.this,SkinActivity.this);


        activitySkinBinding.viewPagerImageSlider.setAdapter(sliderAdapter);

        activitySkinBinding.viewPagerImageSlider.startAutoScroll();

        activitySkinBinding.viewPagerImageSlider.setLoopEnabled(true);
        activitySkinBinding.viewPagerImageSlider.setCanTouch(true);

        activitySkinBinding.selImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (requestPermissions.permissionStorageRead()) {
                    ActivityCompat.requestPermissions(SkinActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
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

        activitySkinBinding.result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bitmap != null) {
                    if(!path.equals("")){
                        AsyncTaskD asyncTaskDownloadAudio = new AsyncTaskD(path,"");
                        asyncTaskDownloadAudio.execute();
                    }
                    bitmap = null;
                }else{
                    Toast.makeText(SkinActivity.this, "Please, Choose Image First!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Gallary_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(SkinActivity.this.getContentResolver(), data.getData());
                activitySkinBinding.imgCorona.setImageBitmap(bitmap);
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
            showDialogPython = new ShowDialogPython(SkinActivity.this,SkinActivity.this.getLayoutInflater(),"load");

        }

        @Override
        protected String doInBackground(String... f_url) {
            if(action.equals("first")){
                if (! Python.isStarted()) {
                    Python.start(new AndroidPlatform(SkinActivity.this));//error is here!
                }
                final Python py = Python.getInstance();
                main_program = py.getModule("prolog");
            }else{
                String result = main_program.callAttr("model",path,"Skin").toString();
                String[] listResult = result.split("@");
                int prediction = Integer.parseInt(listResult[0]);
                Log.e("listResult[1]: ", listResult[1].replace("\n","") + "");
                String probStr = listResult[1].replace("[","")
                        .replace("]","").replace("\n","");
                Log.e("probStr: ", probStr.trim());
                prop = probStr.trim().split(" ");
            }

            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            if(!action.equals("first")){
                activitySkinBinding.progressactinic.setAdProgress((int) (Float.parseFloat(prop[0]) *100));
                activitySkinBinding.progressbasal.setAdProgress((int) (Float.parseFloat(prop[1]) *100));
                activitySkinBinding.progressbenign.setAdProgress((int) (Float.parseFloat(prop[2]) *100));
                activitySkinBinding.progressderma.setAdProgress((int) (Float.parseFloat(prop[3]) *100));
                activitySkinBinding.progressmelan.setAdProgress((int) (Float.parseFloat(prop[4]) *100));
                activitySkinBinding.progressmelanoma.setAdProgress((int) (Float.parseFloat(prop[6]) *100));
                activitySkinBinding.progressvascular.setAdProgress((int) (Float.parseFloat(prop[7]) *100));
            }
            showDialogPython.dismissDialog();
        }
    }
}