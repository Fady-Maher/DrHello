package com.example.drhello.medical;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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
import com.example.drhello.databinding.ActivityBrainBinding;
import com.example.drhello.databinding.ActivitySkinBinding;
import com.example.drhello.model.SliderItem;
import com.example.drhello.textclean.RequestPermissions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.ml.modeldownloader.CustomModel;
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions;
import com.google.firebase.ml.modeldownloader.DownloadType;
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.tensorflow.lite.Interpreter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.UUID;

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
    public static ProgressDialog mProgress;
    private Bitmap bitmap;
    private StorageReference storageReference;
    private RequestPermissions requestPermissions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skin);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            getWindow().setStatusBarColor(Color.WHITE);
        }
        storageReference = FirebaseStorage.getInstance().getReference();

        requestPermissions = new RequestPermissions(SkinActivity.this,SkinActivity.this);

        mProgress = new ProgressDialog(SkinActivity.this);
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(SkinActivity.this));//error is here!
        }
        final Python py = Python.getInstance();
        main_program = py.getModule("prolog");

        activitySkinBinding = DataBindingUtil.setContentView(SkinActivity.this, R.layout.activity_skin);

        activitySkinBinding.back.setOnClickListener(new View.OnClickListener() {
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
                    byte[] bytesOutImg;
                    ByteArrayOutputStream bytesStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytesStream);
                    bytesOutImg = bytesStream.toByteArray();
                    uploadImage(bytesOutImg,storageReference);
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
                //  byte[] a = fromBitmap(bitmap);
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

    public class AsyncTaskD extends AsyncTask<String, String, String> {

        String url;
        public AsyncTaskD(String url){
            this.url = url;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... f_url) {

            String str = main_program.callAttr("model",url,"Skin").toString();
            int prediction = Integer.parseInt(str.split(",")[0].replaceAll("[^0-9]", ""));;
            String probability = str.split(",")[1].replaceAll("]", "");
            probability = probability.replaceAll("\"", "").replace("}","").substring(14);

            String[] arrayList = probability.split(" ");
            Log.e("prediction : ",prediction+"");
            Log.e("probability : ",probability);
            Log.e("arrayList : ",arrayList.toString());
            String result = String.format("%.2f", Float.parseFloat(arrayList[prediction]) * 100);

            if (prediction == 0) {
                activitySkinBinding.txtResult.setText(stringsSkin[0] + " :  " + result);
            } else if (prediction == 1) {
                activitySkinBinding.txtResult.setText(stringsSkin[1] + " :  " + result);
            } else if (prediction == 2) {
                activitySkinBinding.txtResult.setText(stringsSkin[2] + " :  " + result);
            } else if (prediction == 3) {
                activitySkinBinding.txtResult.setText(stringsSkin[3] + " :  " + result);
            } else if (prediction == 4) {
                activitySkinBinding.txtResult.setText(stringsSkin[4] + " :  " + result);
            }  else if (prediction == 5) {
                activitySkinBinding.txtResult.setText(stringsSkin[5] + " :  " + result);
            } else {
                activitySkinBinding.txtResult.setText(stringsSkin[6] + " :  " + result);
            }

            mProgress.dismiss();
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
        }
    }

    private void uploadImage(byte[] bytes, StorageReference storageReference) {
        mProgress.setMessage("Image Processing..");
        mProgress.setCancelable(false);
        mProgress.show();
        StorageReference ref = storageReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()
                + "/Model/"+ UUID.nameUUIDFromBytes(bytes));
        ref.putBytes(bytes)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            saveUri(ref);
                        } else {
                            //Toast.makeText(WritePostsActivity.this, "Loading not done", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(WritePostsActivity.this, "Image not loading error : "+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUri(StorageReference ref) {
        ref.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        AsyncTaskD asyncTask = new AsyncTaskD(uri.toString());
                        asyncTask.execute();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("errorH : ", e.getMessage());
            }
        });
    }

}