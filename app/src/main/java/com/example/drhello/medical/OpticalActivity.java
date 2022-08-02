package com.example.drhello.medical;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.example.drhello.R;
import com.example.drhello.other.ShowDialogPython;
import com.example.drhello.adapter.OnClickDoctorInterface;
import com.example.drhello.adapter.SliderAdapter;
import com.example.drhello.databinding.ActivityOpticalBinding;
import com.example.drhello.model.SliderItem;
import com.example.drhello.textclean.RequestPermissions;
import com.example.drhello.ui.news.WebViewActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class OpticalActivity extends AppCompatActivity implements OnClickDoctorInterface {
    private ActivityOpticalBinding activityOpticalBinding;
    private ArrayList<SliderItem> sliderItems=new ArrayList<>();
    private String[] stringsOptical = {"CNV", "DRUSEN","DME", "NORMAL"};
    private String[] urls = {"https://eyewiki.aao.org/Choroidal_Neovascularization:_OCT_Angiography_Findings",
            "https://www.webmd.com/eye-health/what-are-retinal-drusen",
            "https://www.webmd.com/diabetes/diabetic-macular-edema-causes-symptoms",
            ""};



    private static final int Gallary_REQUEST_CODE = 1;
    PyObject main_program;
    String path = "";
    private Bitmap bitmap;
    private RequestPermissions requestPermissions;
    ShowDialogPython showDialogPython;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_optical);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) ;
        }else{
            getWindow().setStatusBarColor(Color.WHITE);
        }


        requestPermissions = new RequestPermissions(OpticalActivity.this,OpticalActivity.this);

        activityOpticalBinding = DataBindingUtil.setContentView(OpticalActivity.this, R.layout.activity_optical);
        activityOpticalBinding.shimmer.startShimmerAnimation();

        AsyncTaskGeneral asyncTaskGeneral = new AsyncTaskGeneral(path,"first",
                "optical",null,null,
                null,null,null,null,
                OpticalActivity.this,activityOpticalBinding,null,null
        );
        asyncTaskGeneral.execute();

        activityOpticalBinding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sliderItems.add(new SliderItem(R.drawable.cnv_eye,"CNV"));
        sliderItems.add(new SliderItem(R.drawable.drusen_eye,"DRUSEN"));
        sliderItems.add(new SliderItem(R.drawable.dme_eye,"DME"));
        sliderItems.add(new SliderItem(R.drawable.normal_eye,"NORMAL"));

        SliderAdapter sliderAdapter=new SliderAdapter(sliderItems,OpticalActivity.this,OpticalActivity.this);

        activityOpticalBinding.viewPagerImageSlider.setAdapter(sliderAdapter);

        activityOpticalBinding.viewPagerImageSlider.startAutoScroll();

        activityOpticalBinding.viewPagerImageSlider.setLoopEnabled(true);
        activityOpticalBinding.viewPagerImageSlider.setCanTouch(true);

        activityOpticalBinding.selImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityOpticalBinding.progressunknownoptical.setAdProgress((int) (Float.parseFloat(String.valueOf(0.0)) * 100));
                activityOpticalBinding.progresscnv.setAdProgress((int) (Float.parseFloat(String.valueOf(0.0)) * 100));
                activityOpticalBinding.progressdru.setAdProgress((int) (Float.parseFloat(String.valueOf(0.0)) * 100));
                activityOpticalBinding.progresssdmi.setAdProgress((int) (Float.parseFloat(String.valueOf(0.0)) * 100));
                activityOpticalBinding.progressnormal.setAdProgress((int) (Float.parseFloat(String.valueOf(0.0)) * 100));
                activityOpticalBinding.txtPrediction.setText("");

                if (requestPermissions.permissionStorageRead()) {
                    ActivityCompat.requestPermissions(OpticalActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
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

        activityOpticalBinding.result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bitmap != null) {
                    if(!path.equals("")){


                        activityOpticalBinding.txtPrediction.setText("00000");

                        AsyncTaskGeneral asyncTaskGeneral = new AsyncTaskGeneral(path,"optical",
                                "optical",null,null,
                                null,null,null,null,
                                OpticalActivity.this,activityOpticalBinding,null,null
                        );
                        asyncTaskGeneral.execute();
                    }

                    bitmap = null;
                }else{
                    Toast.makeText(OpticalActivity.this, "Please, Choose Image First!!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        activityOpticalBinding.txtGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OpticalActivity.this, WebViewActivity.class);
                if(activityOpticalBinding.txtPrediction.getText().toString().equals("2")){
                    intent.putExtra("url",urls[0]);
                    startActivity(intent);
                }else if(activityOpticalBinding.txtPrediction.getText().toString().equals("4")){
                    intent.putExtra("url",urls[1]);
                    startActivity(intent);
                }else if(activityOpticalBinding.txtPrediction.getText().toString().equals("4")){
                    intent.putExtra("url",urls[2]);
                    startActivity(intent);
                }
            }
        });
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Gallary_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(OpticalActivity.this.getContentResolver(), data.getData());
                activityOpticalBinding.imgCorona.setImageBitmap(bitmap);
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


}