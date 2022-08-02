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
import com.example.drhello.databinding.ActivitySkinBinding;
import com.example.drhello.model.SliderItem;
import com.example.drhello.textclean.RequestPermissions;
import com.example.drhello.ui.news.WebViewActivity;
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
    private String[] urls = {"https://www.mayoclinic.org/diseases-conditions/actinic-keratosis/symptoms-causes/syc-20354969",//actinic
                    "https://www.mayoclinic.org/diseases-conditions/basal-cell-carcinoma/symptoms-causes/syc-20354187",//basal
                    "https://www.mayoclinic.org/diseases-conditions/seborrheic-keratosis/symptoms-causes/syc-20353878",//keratosis
                    "https://www.medicalnewstoday.com/articles/318870",  // Dermatofibroma
                    "https://www.nationwidechildrens.org/conditions/congenital-melanocytic-nevi",// melanocytic
    "https://www.mayoclinic.org/diseases-conditions/eye-melanoma/symptoms-causes/syc-20372371", // MELANOMA
    "https://www.ssmhealth.com/cardinal-glennon/pediatric-plastic-reconstructive-surgery/hemangiomas"};//Vascular_Lesions

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

        AsyncTaskGeneral asyncTaskGeneral = new AsyncTaskGeneral(path,"first",
                "skin",null,null,
                null,null,null,null,
                null,null,SkinActivity.this,activitySkinBinding
        );
        asyncTaskGeneral.execute();

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
                activitySkinBinding.progressunknownskin.setAdProgress((int) (Float.parseFloat(String.valueOf(0.0)) * 100));
                activitySkinBinding.progressactinic.setAdProgress((int) (Float.parseFloat(String.valueOf(0.0)) * 100));
                activitySkinBinding.progressbasal.setAdProgress((int) (Float.parseFloat(String.valueOf(0.0)) * 100));
                activitySkinBinding.progressbenign.setAdProgress((int) (Float.parseFloat(String.valueOf(0.0)) * 100));
                activitySkinBinding.progressderma.setAdProgress((int) (Float.parseFloat(String.valueOf(0.0)) * 100));
                activitySkinBinding.progressmelan.setAdProgress((int) (Float.parseFloat(String.valueOf(0.0)) * 100));
                activitySkinBinding.progressmelanoma.setAdProgress((int) (Float.parseFloat(String.valueOf(0.0)) * 100));
                activitySkinBinding.progressvascular.setAdProgress((int) (Float.parseFloat(String.valueOf(0.0)) * 100));
                activitySkinBinding.txtPrediction.setText("");

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


                        activitySkinBinding.txtPrediction.setText("00000");
                        AsyncTaskGeneral asyncTaskGeneral = new AsyncTaskGeneral(path,"skin",
                                "skin",null,null,
                                null,null,null,null,
                                null,null,SkinActivity.this,activitySkinBinding
                        );
                        asyncTaskGeneral.execute();
                    }
                    bitmap = null;
                }else{
                    Toast.makeText(SkinActivity.this, "Please, Choose Image First!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        activitySkinBinding.txtGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SkinActivity.this, WebViewActivity.class);
                if(activitySkinBinding.txtPrediction.getText().toString().equals("0")){
                    intent.putExtra("url",urls[0]);
                }else if(activitySkinBinding.txtPrediction.getText().toString().equals("1")){
                    intent.putExtra("url",urls[1]);
                }else if(activitySkinBinding.txtPrediction.getText().toString().equals("2")){
                    intent.putExtra("url",urls[2]);
                }else if(activitySkinBinding.txtPrediction.getText().toString().equals("3")){
                    intent.putExtra("url",urls[3]);
                }else if(activitySkinBinding.txtPrediction.getText().toString().equals("3")){
                    intent.putExtra("url",urls[4]);
                }else if(activitySkinBinding.txtPrediction.getText().toString().equals("3")){
                    intent.putExtra("url",urls[5]);
                }else if(activitySkinBinding.txtPrediction.getText().toString().equals("3")){
                    intent.putExtra("url",urls[6]);
                }
                startActivity(intent);
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


}