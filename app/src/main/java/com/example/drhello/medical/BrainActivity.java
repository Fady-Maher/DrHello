package com.example.drhello.medical;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
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
import com.example.drhello.adapter.OnClickDoctorInterface;
import com.example.drhello.R;
import com.example.drhello.adapter.SliderAdapter;
import com.example.drhello.databinding.ActivityBrainBinding;
import com.example.drhello.model.SliderItem;
import com.example.drhello.ui.news.WebViewActivity;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class BrainActivity extends AppCompatActivity implements OnClickDoctorInterface {
    private ActivityBrainBinding activityBrainBinding;
    private ArrayList<SliderItem> sliderItems = new ArrayList<>();
    private String[] stringsTumor = {"Glioma_Tumor", "Meningioma Tumor", "No Tumor", "Pituitary Tumor"};
    private String[] stringsgeneral ={"BrainTumor", "Chest", "HeartBeats", "Other", "Retinal", "SkinCancer"};
    private String[] urls = {
            "https://www.mayoclinic.org/diseases-conditions/glioma/symptoms-causes/syc-20350251", //glioma
            "https://www.mayoclinic.org/diseases-conditions/meningioma/symptoms-causes/syc-20355643", // meningioma
            "",
            "https://www.mayoclinic.org/diseases-conditions/pituitary-tumors/symptoms-causes/syc-20350548"} ; // pituitary
    private static final int Gallary_REQUEST_CODE = 1;
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

        AsyncTaskGeneral asyncTaskGeneral = new AsyncTaskGeneral(path,"first",
                "brain",null,null,
                activityBrainBinding,BrainActivity.this,null,null,
                null,null,null,null
        );
        asyncTaskGeneral.execute();

        activityBrainBinding = DataBindingUtil.setContentView(BrainActivity.this, R.layout.activity_brain);
        activityBrainBinding.shimmer.startShimmerAnimation();

        activityBrainBinding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sliderItems.add(new SliderItem(R.drawable.no_tumor, "No Tumor"));
        sliderItems.add(new SliderItem(R.drawable.pituitary, "Pituitary"));
        sliderItems.add(new SliderItem(R.drawable.meningioma, "Meningioma"));
        sliderItems.add(new SliderItem(R.drawable.glioma_tumor, "Glioma"));

        SliderAdapter sliderAdapter = new SliderAdapter(sliderItems, BrainActivity.this,BrainActivity.this);

        activityBrainBinding.viewPagerImageSlider.setAdapter(sliderAdapter);

        activityBrainBinding.viewPagerImageSlider.startAutoScroll();

        activityBrainBinding.viewPagerImageSlider.setLoopEnabled(true);
        activityBrainBinding.viewPagerImageSlider.setCanTouch(true);


        activityBrainBinding.selImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityBrainBinding.progressunknownbrain.setAdProgress((int) (Float.parseFloat(String.valueOf(0.0)) * 100));
                activityBrainBinding.progressglioma.setAdProgress((int) (Float.parseFloat(String.valueOf(0.0)) * 100));
                activityBrainBinding.progressmen.setAdProgress((int) (Float.parseFloat(String.valueOf(0.0)) * 100));
                activityBrainBinding.progressno.setAdProgress((int) (Float.parseFloat(String.valueOf(0.0)) * 100));
                activityBrainBinding.progresspit.setAdProgress((int) (Float.parseFloat(String.valueOf(0.0)) * 100));
                activityBrainBinding.txtPrediction.setText("");


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
                        activityBrainBinding.txtPrediction.setText("00000");
                        AsyncTaskGeneral asyncTaskGeneral = new AsyncTaskGeneral(path,"brain",
                                "brain",null,null,
                                activityBrainBinding,BrainActivity.this,null,null,
                                null,null,null,null
                        );
                        asyncTaskGeneral.execute();
                    }
                    bitmap = null;
                } else {
                    Toast.makeText(BrainActivity.this, "Please, Choose Image First!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        activityBrainBinding.txtGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BrainActivity.this, WebViewActivity.class);
                if(activityBrainBinding.txtPrediction.getText().toString().equals("0")){
                    intent.putExtra("url",urls[0]);
                    startActivity(intent);
                }else if(activityBrainBinding.txtPrediction.getText().toString().equals("1")){
                    intent.putExtra("url",urls[1]);
                    startActivity(intent);
                }else if(activityBrainBinding.txtPrediction.getText().toString().equals("3")){
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