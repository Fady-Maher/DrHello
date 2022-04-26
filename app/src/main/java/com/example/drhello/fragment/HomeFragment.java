package com.example.drhello.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.example.drhello.MyCallBack;
import com.example.drhello.R;
import com.example.drhello.adapter.SliderAdapter;
import com.example.drhello.firebaseinterface.MyCallbackAllUser;
import com.example.drhello.firebaseinterface.MyCallbackSignIn;
import com.example.drhello.medical.BrainActivity;
import com.example.drhello.medical.ChestActivity;
import com.example.drhello.model.SliderItem;
import com.example.drhello.model.UserAccount;
import com.example.drhello.ui.chats.AddPersonActivity;
import com.example.drhello.ui.chats.AsyncTaskDownloadAudio;
import com.google.android.gms.tasks.OnCompleteListener;
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

public class HomeFragment extends Fragment {
    private CardView chest,brain,skin,cancer;
    private ArrayList<SliderItem> sliderItems = new ArrayList<>();
    private String[] stringsChest = {"Covid_19", "Lung_Opacity", "Normal", "Pneumonia"};
    private AutoScrollRecyclerView autoScrollRecyclerView;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static ProgressDialog mProgress;

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
        autoScrollRecyclerView = view.findViewById(R.id.viewPagerImageSlider);



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
//                Intent intent = new Intent(getActivity(), AddPersonActivity.class);
//                startActivity(intent);
            }
        });
        cancer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), AddPersonActivity.class);
//                startActivity(intent);
            }
        });

        autoScrollRecyclerView.startAutoScroll();
        autoScrollRecyclerView.setLoopEnabled(true);
        autoScrollRecyclerView.setCanTouch(true);

        readData(new MyCallbackAllUser() {
            @Override
            public void onCallback(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (int i = 0; i < task.getResult().size(); i++) {
                        UserAccount userAccount = task.getResult().getDocuments().get(i).toObject(UserAccount.class);
                        if (userAccount.getUserInformation().getType().equals("Doctor")){
                            SliderItem sliderItem = new SliderItem(R.drawable.normal_xray,
                                    userAccount.getUserInformation().getSpecification());
                            if(!sliderItems.contains(sliderItem))
                                sliderItems.add(sliderItem);
                        }
                    }

                    if(sliderItems.size() > 0 ){
                        SliderAdapter sliderAdapter=new SliderAdapter(sliderItems,getActivity());
                        autoScrollRecyclerView.setAdapter(sliderAdapter);
                        sliderAdapter.notifyDataSetChanged();
                        autoScrollRecyclerView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        });
                    }
                }

                mProgress.dismiss();
            }
        });

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

}