package com.example.drhello.fragment;

import static android.content.Context.CAMERA_SERVICE;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.drhello.AutoScrollRecyclerView;
import com.example.drhello.DoctorsActivity;
import com.example.drhello.MyCallBack;
import com.example.drhello.OnClickDoctorInterface;
import com.example.drhello.R;
import com.example.drhello.adapter.SliderAdapter;
import com.example.drhello.firebaseinterface.MyCallbackAllUser;
import com.example.drhello.firebaseinterface.MyCallbackSignIn;
import com.example.drhello.medical.BrainActivity;
import com.example.drhello.medical.ChestActivity;
import com.example.drhello.medical.SkinActivity;
import com.example.drhello.model.SliderItem;
import com.example.drhello.model.UserAccount;
import com.example.drhello.ui.chats.AddPersonActivity;
import com.example.drhello.ui.chats.AsyncTaskDownloadAudio;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.ml.modeldownloader.CustomModel;
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions;
import com.google.firebase.ml.modeldownloader.DownloadType;
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements OnClickDoctorInterface {
    private CardView chest,brain,skin,cancer;
    private ArrayList<SliderItem> sliderItems = new ArrayList<>();
    private String[] stringsChest = {"Covid_19", "Lung_Opacity", "Normal", "Pneumonia"};
    private RecyclerView recyclerView;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<UserAccount> userAccountArrayList = new ArrayList<>();
    SliderItem sliderItem;
    public static ProgressDialog mProgress;

    private Bitmap bitmap;
    private static final int Gallary_REQUEST_CODE = 1;
    private ImageView img;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mProgress = new ProgressDialog(getActivity());

        chest = view.findViewById(R.id.chest);
        brain = view.findViewById(R.id.brain);
        skin = view.findViewById(R.id.skin);
        cancer = view.findViewById(R.id.cancer);
        recyclerView = view.findViewById(R.id.viewPagerImageSlider);

        img = view.findViewById(R.id.img);

        chest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChestActivity.class);
                startActivity(intent);
            }
        });
        brain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BrainActivity.class);
                startActivity(intent);
            }
        });
        skin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SkinActivity.class);
                startActivity(intent);
            }
        });
        cancer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), AddPersonActivity.class);
//                startActivity(intent);
            }
        });


        readData(new MyCallbackAllUser() {
            @Override
            public void onCallback(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (int i = 0; i < task.getResult().size(); i++) {
                        UserAccount userAccount = task.getResult().getDocuments().get(i).toObject(UserAccount.class);
                        if (userAccount.getUserInformation().getType().equals("Doctor")){
                            if(userAccount.getUserInformation().getSpecification().equals("Occupational and environmental medicine")){
                                sliderItem = new SliderItem(R.drawable.occupational_and_environmental_medicine, userAccount.getUserInformation().getSpecification());
                            }else if(userAccount.getUserInformation().getSpecification().equals("Obstetrics and gynaecology")){
                                sliderItem = new SliderItem(R.drawable.obstetrics_gynecology, userAccount.getUserInformation().getSpecification());
                            }else if(userAccount.getUserInformation().getSpecification().equals("Sport and exercise medicine")){
                                sliderItem = new SliderItem(R.drawable.sport_and_exercise_medicine, userAccount.getUserInformation().getSpecification());
                            }else if(userAccount.getUserInformation().getSpecification().equals("Dermatology, Emergency medicine")){
                                sliderItem = new SliderItem(R.drawable.dermatology_emergency_medicine, userAccount.getUserInformation().getSpecification());
                            }else if(userAccount.getUserInformation().getSpecification().equals("Physician")){
                                sliderItem = new SliderItem(R.drawable.physician, userAccount.getUserInformation().getSpecification());
                            }else if(userAccount.getUserInformation().getSpecification().equals("Medical administration")){
                                sliderItem = new SliderItem(R.drawable.medical_administration, userAccount.getUserInformation().getSpecification());
                            }else if(userAccount.getUserInformation().getSpecification().equals("Anaesthesia")){
                                sliderItem = new SliderItem(R.drawable.anesthesia, userAccount.getUserInformation().getSpecification());
                            }else if(userAccount.getUserInformation().getSpecification().equals("Pathology")){
                                sliderItem = new SliderItem(R.drawable.pathology, userAccount.getUserInformation().getSpecification());
                            }else if(userAccount.getUserInformation().getSpecification().equals("Palliative medicine")){
                                sliderItem = new SliderItem(R.drawable.palliative_medicine, userAccount.getUserInformation().getSpecification());
                            }else if(userAccount.getUserInformation().getSpecification().equals("Sexual health medicine")){
                                sliderItem = new SliderItem(R.drawable.sexual_health_medicine, userAccount.getUserInformation().getSpecification());
                            }else if(userAccount.getUserInformation().getSpecification().equals("Radiation oncology")){
                                sliderItem = new SliderItem(R.drawable.radiation_oncology, userAccount.getUserInformation().getSpecification());
                            }else if(userAccount.getUserInformation().getSpecification().equals("Surgery")){
                                sliderItem = new SliderItem(R.drawable.surgery, userAccount.getUserInformation().getSpecification());
                            }else if(userAccount.getUserInformation().getSpecification().equals("Radiology")){
                                sliderItem = new SliderItem(R.drawable.radiology, userAccount.getUserInformation().getSpecification());
                            }else if(userAccount.getUserInformation().getSpecification().equals("General practice")){
                                sliderItem = new SliderItem(R.drawable.general_practice, userAccount.getUserInformation().getSpecification());
                            }else if(userAccount.getUserInformation().getSpecification().equals("Intensive care medicine")){
                                sliderItem = new SliderItem(R.drawable.intensive_care_medicine, userAccount.getUserInformation().getSpecification());
                            }else if(userAccount.getUserInformation().getSpecification().equals("Paediatrics and child health")){
                                sliderItem = new SliderItem(R.drawable.paediatrics_and_child_health, userAccount.getUserInformation().getSpecification());
                            }else if(userAccount.getUserInformation().getSpecification().equals("Rehabilitation medicine")){
                                sliderItem = new SliderItem(R.drawable.rehabilitation_medicine, userAccount.getUserInformation().getSpecification());
                            }else if(userAccount.getUserInformation().getSpecification().equals("Ophthalmology")){
                                sliderItem = new SliderItem(R.drawable.ophthalmology, userAccount.getUserInformation().getSpecification());
                            }else if(userAccount.getUserInformation().getSpecification().equals("Psychiatry")){
                                sliderItem = new SliderItem(R.drawable.psychiatry, userAccount.getUserInformation().getSpecification());
                            }else if(userAccount.getUserInformation().getSpecification().equals("Public health medicine")){
                                sliderItem = new SliderItem(R.drawable.public_health_medicine, userAccount.getUserInformation().getSpecification());
                            }else if(userAccount.getUserInformation().getSpecification().equals("Addiction medicine")){
                                sliderItem = new SliderItem(R.drawable.addiction_medicine, userAccount.getUserInformation().getSpecification());
                            }else if(userAccount.getUserInformation().getSpecification().equals("Pain medicine")){
                                sliderItem = new SliderItem(R.drawable.pain_medicine, userAccount.getUserInformation().getSpecification());
                            }

                            userAccountArrayList.add(userAccount);
                            if(!sliderItems.contains(sliderItem))
                                sliderItems.add(sliderItem);
                        }
                    }

                    if(sliderItems.size() > 0 ){
                        SliderAdapter sliderAdapter=new SliderAdapter(sliderItems,getActivity(),HomeFragment.this);
                        recyclerView.setAdapter(sliderAdapter);
                        sliderAdapter.notifyDataSetChanged();
                    }
                }

                mProgress.dismiss();
            }
        });




/*
        img.setOnClickListener(new View.OnClickListener() {
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


 */








        return view;
    }


    public void readData(MyCallbackAllUser myCallback) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            mProgress.setMessage("Loading..");
            mProgress.show();
            db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    myCallback.onCallback(task);
                }
            });
        }
    }

    // firebase text vision
/*
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Gallary_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                img.setImageBitmap(bitmap);

                TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

                InputImage image = InputImage.fromBitmap(bitmap, 0);
                Task<Text> result =
                        recognizer.process(image)
                                .addOnSuccessListener(new OnSuccessListener<Text>() {
                                    @Override
                                    public void onSuccess(Text visionText) {
                                        // Task completed successfully
                                        // ...
                                        Log.e("TEXT:  "  , visionText.getText());
                                    }
                                })
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Task failed with an exception
                                                // ...
                                            }
                                        });

            } catch (IOException e) {
                Log.e("gallary exception: ", e.getMessage());
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // Toast.makeText(getBaseContext(), "Canceled", Toast.LENGTH_SHORT).show();
        }

    }
 */

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 0);
        ORIENTATIONS.append(Surface.ROTATION_90, 90);
        ORIENTATIONS.append(Surface.ROTATION_180, 180);
        ORIENTATIONS.append(Surface.ROTATION_270, 270);
    }

    /**
     * Get the angle by which an image must be rotated given the device's current
     * orientation.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private int getRotationCompensation(String cameraId, Activity activity, boolean isFrontFacing)
            throws CameraAccessException {
        // Get the device's current rotation relative to its "native" orientation.
        // Then, from the ORIENTATIONS table, look up the angle the image must be
        // rotated to compensate for the device's rotation.
        int deviceRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int rotationCompensation = ORIENTATIONS.get(deviceRotation);

        // Get the device's sensor orientation.
        CameraManager cameraManager = (CameraManager) activity.getSystemService(CAMERA_SERVICE);
        int sensorOrientation = cameraManager
                .getCameraCharacteristics(cameraId)
                .get(CameraCharacteristics.SENSOR_ORIENTATION);

        if (isFrontFacing) {
            rotationCompensation = (sensorOrientation + rotationCompensation) % 360;
        } else { // back-facing
            rotationCompensation = (sensorOrientation - rotationCompensation + 360) % 360;
        }
        return rotationCompensation;
    }

    @Override
    public void OnClick(String spec) {
        Intent intent = new Intent(getActivity(), DoctorsActivity.class);
        intent.putExtra("doctors", userAccountArrayList);
        intent.putExtra("spec",spec);
        startActivity(intent);
    }
}