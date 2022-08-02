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
import com.example.drhello.other.ShowDialogPython;
import com.example.drhello.adapter.OnClickDoctorInterface;
import com.example.drhello.R;
import com.example.drhello.adapter.SliderAdapter;
import com.example.drhello.databinding.ActivityChestBinding;
import com.example.drhello.model.SliderItem;
import com.example.drhello.textclean.RequestPermissions;
import com.example.drhello.ui.news.WebViewActivity;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class ChestActivity extends AppCompatActivity implements OnClickDoctorInterface {
    private ActivityChestBinding activityChestBinding;
    private ArrayList<SliderItem> sliderItems=new ArrayList<>();
    private String[] stringsChest = {"Covid19", "Lung Opacity","Normal", "Pneumonia"};
    String[] urls = {"https://www.mayoclinic.org/diseases-conditions/coronavirus/symptoms-causes/syc-20479963", // corona
             "https://www.medicalnewstoday.com/articles/ground-glass-opacity#questions-to-ask",
             "", // normal
             "https://www.mayoclinic.org/diseases-conditions/pneumonia/symptoms-causes/syc-20354204"};  // pneumonia
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

        AsyncTaskGeneral asyncTaskGeneral = new AsyncTaskGeneral(path,"first",
                "corona",activityChestBinding,ChestActivity.this,
                null,null,null,null,
                null,null,null,null
        );
        asyncTaskGeneral.execute();

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
                activityChestBinding.progressunknownchest.setAdProgress((int) (Float.parseFloat(String.valueOf(0.0)) * 100));
                activityChestBinding.txtPrediction.setText("00000");
                activityChestBinding.progresscovid.setAdProgress((int) (Float.parseFloat(String.valueOf(0.0)) * 100));
                activityChestBinding.progresslung.setAdProgress((int) (Float.parseFloat(String.valueOf(0.0)) * 100));
                activityChestBinding.progressnormal.setAdProgress((int) (Float.parseFloat(String.valueOf(0.0)) * 100));
                activityChestBinding.progresspneu.setAdProgress((int) (Float.parseFloat(String.valueOf(0.0)) * 100));

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
                        activityChestBinding.txtPrediction.setText("");
                        AsyncTaskGeneral asyncTaskGeneral = new AsyncTaskGeneral(path,"corona",
                                "corona",activityChestBinding,ChestActivity.this,
                                null,null,null,null,
                                null,null,null,null
                                );
                        asyncTaskGeneral.execute();
                    }
                    bitmap = null;
                }else{
                    Toast.makeText(ChestActivity.this, "Please, Choose Image First!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        activityChestBinding.txtGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChestActivity.this, WebViewActivity.class);
                if(activityChestBinding.txtPrediction.getText().toString().equals("0")){
                    intent.putExtra("url",urls[0]);
                    startActivity(intent);
                }else if(activityChestBinding.txtPrediction.getText().toString().equals("1")){
                    intent.putExtra("url",urls[1]);
                    startActivity(intent);
                }else if(activityChestBinding.txtPrediction.getText().toString().equals("3")){
                    intent.putExtra("url",urls[3]);
                    startActivity(intent);
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
}