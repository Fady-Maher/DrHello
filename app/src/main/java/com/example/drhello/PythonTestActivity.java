package com.example.drhello;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.modeldownloader.CustomModel;
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions;
import com.google.firebase.ml.modeldownloader.DownloadType;
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.Interpreter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class PythonTestActivity extends AppCompatActivity {
    PyObject main_program;
    Button btn_result_chest, btn_gallary, btn_result_tumor;
    TextView txt_result;
    ImageView img_corona;
    private static final int Gallary_REQUEST_CODE = 1;
    private Bitmap bitmap;
    private String[] stringsChest = {"Covid_19", "Lung_Opacity", "Normal", "Pneumonia"};
    private String[] stringsTumor =  { "Glioma_Tumor", "Meningioma Tumor", "No Tumor", "Pituitary Tumor"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_python_test);
        btn_result_chest = findViewById(R.id.btn_result_chest);
        btn_gallary = findViewById(R.id.btn_gallary);
        txt_result = findViewById(R.id.txt_result);
        img_corona = findViewById(R.id.img_corona);
        btn_result_tumor = findViewById(R.id.btn_result_tumor);


        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(this));//error is here!
        }
        final Python py = Python.getInstance();
        main_program = py.getModule("prolog");

        PyObject str = main_program.callAttr("clean_text","i hate you and i will kill you in the future");
        Log.e("RESULT: ", str.toString());
        String[] arrayList = str.toString().split("");
        loadModel(arrayList,"word_dictEN.json");


        btn_result_chest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




            }
        });





    }

    private String JsonDataFromAsset(String name){
        String json = null;
        try {
            InputStream inputStream = PythonTestActivity.this.getAssets().open(name);
            int sizeOfFile  = inputStream.available();
            byte[] bufferData = new byte[sizeOfFile];
            inputStream.read(bufferData);
            inputStream.close();
            json = new String(bufferData,"UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }


    public void loadModel(String[] arr, String name_json){
        float pad[] = new float[300];
        try {
            ArrayList<Float> res=new ArrayList<>();
            JSONObject jsonObject = new JSONObject(Objects.requireNonNull(JsonDataFromAsset(name_json)));
            HashMap map = new Gson().fromJson(jsonObject.toString(), HashMap.class);
            int j=0;
            for(int i=0;i<300;i++){
                if(arr.length+i<300) {
                    pad[i] = 0;
                }
                else{
                    if(map.containsKey(arr[j])){
                        String m = map.get(arr[j]).toString();
                        pad[i]=Float.parseFloat(m);
                        j++;
                    }
                }
            }

            CustomModelDownloadConditions conditions = new CustomModelDownloadConditions.Builder()
                    .requireWifi()
                    .build();
            FirebaseModelDownloader.getInstance()
                    .getModel("HateAbusiveModelEN", DownloadType.LOCAL_MODEL, conditions)
                    .addOnSuccessListener(new OnSuccessListener<CustomModel>() {
                        @Override
                        public void onSuccess(CustomModel model) {
                         // float[][] s = new float[][]{{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5534, 1260, 22}};
                              float[][] s = new float[][]{pad};
                            File modelFile = model.getFile();
                            if (modelFile != null) {
                                Interpreter interpreter = new Interpreter(modelFile);
                                int bufferSize = 1000 * Float.SIZE / Byte.SIZE;
                                ByteBuffer modelOutput = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder());
                                interpreter.run(s, modelOutput);
                                modelOutput.rewind();
                                FloatBuffer probabilities = modelOutput.asFloatBuffer();
                                float probability = probabilities.get(0);
                                Toast.makeText(getApplicationContext(),"REFUSE THIS TEXT",Toast.LENGTH_SHORT).show();
                                Log.e("REFUSE THIS TEXT" ,probability+" ");
                            }
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}