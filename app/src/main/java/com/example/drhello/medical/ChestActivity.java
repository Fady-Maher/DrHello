package com.example.drhello.medical;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

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
import android.os.Handler;
import android.os.Looper;
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
import com.example.drhello.databinding.ActivityChatBinding;
import com.example.drhello.databinding.ActivityChestBinding;
import com.example.drhello.databinding.ActivityNumReactionBinding;
import com.example.drhello.fragment.HomeFragment;
import com.example.drhello.model.CommentModel;
import com.example.drhello.model.Posts;
import com.example.drhello.model.SliderItem;
import com.example.drhello.textclean.RequestPermissions;
import com.example.drhello.ui.writecomment.InsideCommentActivity;
import com.example.drhello.ui.writecomment.WriteCommentActivity;
import com.example.drhello.ui.writepost.NumReactionActivity;
import com.example.drhello.ui.writepost.WritePostsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ChestActivity extends AppCompatActivity implements OnClickDoctorInterface {
    private ActivityChestBinding activityChestBinding;
    private ArrayList<SliderItem> sliderItems=new ArrayList<>();
    private String[] stringsHeart = {"Fusion", "Normal", "Supraventricular", "Unknown","Ventricular"};
    private static final int Gallary_REQUEST_CODE = 1;
    PyObject main_program;
    public static ProgressDialog mProgress;
    PyObject str;
    String path = "";

    private Bitmap bitmap;
    private StorageReference storageReference;
    private RequestPermissions requestPermissions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chest);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) ;
        }else{
            getWindow().setStatusBarColor(Color.WHITE);
        }

        storageReference = FirebaseStorage.getInstance().getReference();

        requestPermissions = new RequestPermissions(ChestActivity.this,ChestActivity.this);

        mProgress = new ProgressDialog(ChestActivity.this);


        activityChestBinding = DataBindingUtil.setContentView(ChestActivity.this, R.layout.activity_chest);

        AsyncTaskD asyncTaskDownloadAudio = new AsyncTaskD(path,"first");
        asyncTaskDownloadAudio.execute();

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
                    /*
                    byte[] bytesOutImg;
                    ByteArrayOutputStream bytesStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytesStream);
                    bytesOutImg = bytesStream.toByteArray();
                    uploadImage(bytesOutImg,storageReference);
                     */
                    if(!path.equals("")){
                        AsyncTaskD asyncTaskDownloadAudio = new AsyncTaskD(path,"");
                        asyncTaskDownloadAudio.execute();
                    }

                    bitmap = null;
                }else{
                    Toast.makeText(ChestActivity.this, "Please, Choose Image First!!", Toast.LENGTH_SHORT).show();
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

                /*
                String str = main_program.callAttr("modelTest",file.getPath()).toString();
                Log.e("str result: ", str.toString());
                 */
/*
                DataInputStream a = new DataInputStream(new BufferedInputStream(new FileInputStream(new File(file.getPath()))));
                Log.e("DataInputStream: ", a.toString());
                Map<String,DataInputStream> map = new HashMap<>();
                map.put("file",a);

                URL url = new URL("https://chat-model.herokuapp.com/predict_text");
                HttpURLConnection http = (HttpURLConnection)url.openConnection();
                http.setRequestMethod("POST");
                http.setDoOutput(true);
                http.setRequestProperty("accept", "application/json");
                http.setRequestProperty("Content-Type", "application/json");

                OutputStream stream = http.getOutputStream();

                stream.write(map);

                InputStream in = http.getInputStream();
                String s = new String(in.readAllBytes(), StandardCharsets.UTF_8);
                System.out.println(s);

                System.out.println(http.getResponseCode() + " " + http.getResponseMessage() + " " );
                http.disconnect();
*/


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
        public AsyncTaskD(String path,String action){
            this.path = path;
            this.action = action;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... f_url) {
/*
            String str = main_program.callAttr("model",url,"Corona").toString();
            int prediction = Integer.parseInt(str.split(",")[0].replaceAll("[^0-9]", ""));;
            String probability = str.split(",")[1].replaceAll("]", "");
            probability = probability.replaceAll("\"", "").replace("}","").substring(14);

            String[] arrayList = probability.split(" ");
            Log.e("prediction : ",prediction+"");
            Log.e("probability : ",probability);
            Log.e("arrayList : ",arrayList.toString());
            String result = String.format("%.2f", Float.parseFloat(arrayList[prediction]) * 100);
*/
            if(action.equals("first")){
                if (! Python.isStarted()) {
                    Python.start(new AndroidPlatform(ChestActivity.this));//error is here!
                }
                final Python py = Python.getInstance();
                main_program = py.getModule("prolog");
            }else{
                int prediction = main_program.callAttr("modelTest",path).toInt();
                if (prediction == 0) {
                    activityChestBinding.txtResult.setText(stringsHeart[0] + " :  " + "result");
                } else if (prediction == 1) {
                    activityChestBinding.txtResult.setText(stringsHeart[1] + " :  " + "result");
                } else if (prediction == 2) {
                    activityChestBinding.txtResult.setText(stringsHeart[2] + " :  " + "result");
                } else if (prediction == 3) {
                    activityChestBinding.txtResult.setText(stringsHeart[3] + " :  " + "result");
                } else {
                    activityChestBinding.txtResult.setText(stringsHeart[4] + " :  " + "result");
                }

            }

            mProgress.dismiss();
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
        }
    }

    /*
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
                            AsyncTaskD asyncTaskDownloadAudio = new AsyncTaskD(uri.toString());
                            asyncTaskDownloadAudio.execute();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("errorH : ", e.getMessage());
                }
            });
    }
*/

}