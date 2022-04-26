package com.example.drhello.medical;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import com.example.drhello.R;
import com.example.drhello.adapter.SliderAdapter;
import com.example.drhello.databinding.ActivityBrainBinding;
import com.example.drhello.databinding.ActivitySkinBinding;
import com.example.drhello.model.SliderItem;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.modeldownloader.CustomModel;
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions;
import com.google.firebase.ml.modeldownloader.DownloadType;
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader;

import org.tensorflow.lite.Interpreter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

public class SkinActivity extends AppCompatActivity {
    private ActivitySkinBinding activitySkinBinding;
    private ArrayList<SliderItem> sliderItems = new ArrayList<>();
    private String[] stringsSkin = {"Actinic_keratoses", "Basal_Cell_Carcinoma", "Benign_Keratosis_Like",
            "Dermatofibroma", "Melanocytic_Nevi", "Vascular_Lesions"};
    private static final int Gallary_REQUEST_CODE = 1;
    PyObject main_program;
    public static ProgressDialog mProgress;
    PyObject str;
    ByteBuffer input;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skin);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            getWindow().setStatusBarColor(Color.WHITE);
        }

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


        SliderAdapter sliderAdapter = new SliderAdapter(sliderItems, SkinActivity.this);


        activitySkinBinding.viewPagerImageSlider.setAdapter(sliderAdapter);

        activitySkinBinding.viewPagerImageSlider.startAutoScroll();

        activitySkinBinding.viewPagerImageSlider.setLoopEnabled(true);
        activitySkinBinding.viewPagerImageSlider.setCanTouch(true);

        activitySkinBinding.selImg.setOnClickListener(new View.OnClickListener() {
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

        activitySkinBinding.result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bitmap != null) {
                    AsyncTaskD asyncTaskDownloadAudio = new AsyncTaskD("Skin");
                    asyncTaskDownloadAudio.execute("");
                } else {
                    Toast.makeText(SkinActivity.this, "Please, Choose Image First!!", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void imageModel(String name_model, int width, int height, String[] stringArrayList) {
        bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);

        CustomModelDownloadConditions conditions = new CustomModelDownloadConditions.Builder()
                .requireWifi()
                .build();
        FirebaseModelDownloader.getInstance()
                .getModel(name_model, DownloadType.LOCAL_MODEL, conditions)
                .addOnSuccessListener(new OnSuccessListener<CustomModel>() {
                    @Override
                    public void onSuccess(CustomModel model) {
                        // which you can use to instantiate a TensorFlow Lite interpreter.
                        File modelFile = model.getFile();
                        if (modelFile != null) {
                            Interpreter interpreter = new Interpreter(modelFile);
                            int bufferSize = 4 * java.lang.Float.SIZE / java.lang.Byte.SIZE;
                            ByteBuffer modelOutput = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder());
                            interpreter.run(input, modelOutput);
                            modelOutput.rewind();
                            FloatBuffer probabilities = modelOutput.asFloatBuffer();
                            double max = probabilities.get(0);
                            int k = 0;
                            Log.e("capacity: ", probabilities.capacity() + "");
                            for (int i = 0; i < probabilities.capacity(); i++) {
                                Log.e("probabilities: ", probabilities.get(i) + "");
                                if (max < probabilities.get(i)) {
                                    k = i;
                                    max = probabilities.get(i);
                                }
                            }
                            String result = String.format("%.2f", max * 100);
                            if (k == 0) {
                                activitySkinBinding.txtResult.setText(stringArrayList[0] + " :  " + result);
                            } else if (k == 1) {
                                activitySkinBinding.txtResult.setText(stringArrayList[1] + " :  " + result);
                            } else if (k == 2) {
                                activitySkinBinding.txtResult.setText(stringArrayList[2] + " :  " + result);
                            } else if (k == 3) {
                                activitySkinBinding.txtResult.setText(stringArrayList[2] + " :  " + result);
                            } else if (k == 4) {
                                activitySkinBinding.txtResult.setText(stringArrayList[2] + " :  " + result);
                            } else {
                                activitySkinBinding.txtResult.setText(stringArrayList[3] + " :  " + result);
                            }
                            mProgress.dismiss();
                            interpreter.close();
                            Log.e("probabilities: ", max + "     " + k);
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

    public class AsyncTaskD extends AsyncTask<String, String, String> {
        private String name_model;

        public AsyncTaskD(String name_model) {
            this.name_model = name_model;
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
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();

            str = main_program.callAttr("call", byteArray, "Corona");
            input = ByteBuffer.allocateDirect(500 * 500 * 1 * 4)
                    .order(ByteOrder.nativeOrder());


            String A = str.asList().toString();
            A = A.replace("[", "");
            A = A.replace(",", "");
            A = A.replace("]", "");
            String[] s = A.split(" ");

            for (int y = 0; y < s.length; y++) {
                if (!s[y].equals("")) {
                    input.putFloat((float) (Float.parseFloat(s[y]) / 255.0));
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            imageModel("Chest_X_Ray_Model", 500, 500, stringsSkin);
        }
    }

}