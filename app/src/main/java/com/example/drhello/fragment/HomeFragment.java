package com.example.drhello.fragment;

import static android.content.Context.CAMERA_SERVICE;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.drhello.ui.profile.DoctorsActivity;
import com.example.drhello.other.ShowDialogPython;
import com.example.drhello.adapter.OnClickDoctorInterface;
import com.example.drhello.R;
import com.example.drhello.adapter.SpecialistAdapter;
import com.example.drhello.firebaseinterface.MyCallbackAllUser;
import com.example.drhello.medical.BrainActivity;
import com.example.drhello.medical.ChestActivity;
import com.example.drhello.medical.HeartActivity;
import com.example.drhello.medical.OpticalActivity;
import com.example.drhello.medical.SkinActivity;
import com.example.drhello.model.SliderItem;
import com.example.drhello.model.UserAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements OnClickDoctorInterface {
    private CardView chest,brain,skin,cancer;
    private ArrayList<SliderItem> sliderItems = new ArrayList<>();
    private ArrayList<SliderItem> Items = new ArrayList<>();

    private String[] stringsChest = {"Covid_19", "Lung_Opacity", "Normal", "Pneumonia"};
    private RecyclerView recyclerView,rec_diseases;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<UserAccount> userAccountArrayList = new ArrayList<>();
    SliderItem sliderItem;
    ShowDialogPython showDialogPython;
    ArrayList<String> spec = new ArrayList<>();

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
        recyclerView = view.findViewById(R.id.viewPagerImageSlider);
        rec_diseases = view.findViewById(R.id.rec_diseases);
        Items.add(new SliderItem(R.drawable.xray, "Chest X-Rays"));
        Items.add(new SliderItem(R.drawable.brain, "Brain Tumor"));
        Items.add(new SliderItem(R.drawable.skin, "Skin Cancer"));
        Items.add(new SliderItem(R.drawable.heart_model, "Heart"));
        Items.add(new SliderItem(R.drawable.normal_eye, "Optical"));
        SpecialistAdapter specialistAdapter = new SpecialistAdapter(getActivity(),Items,
                HomeFragment.this);
        rec_diseases.setAdapter(specialistAdapter);

        img = view.findViewById(R.id.img);



        readData(new MyCallbackAllUser() {
            @Override
            public void onCallback(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (int i = 0; i < task.getResult().size(); i++) {
                        UserAccount userAccount = task.getResult().getDocuments().get(i).toObject(UserAccount.class);
                        if (userAccount.getUserInformation().getType().equals("Doctor")){
                            userAccountArrayList.add(userAccount);
                            if(!spec.contains(userAccount.getUserInformation().getSpecification())){
                                spec.add(userAccount.getUserInformation().getSpecification());

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
                                }else{
                                    sliderItem = new SliderItem(R.drawable.app_mark, userAccount.getUserInformation().getSpecification());
                                }

                                if(!sliderItems.contains(sliderItem))
                                    sliderItems.add(sliderItem);
                            }
                        }
                    }

                    if(sliderItems.size() > 0 ){
                        Log.e("sliderItems: ",sliderItems.size()+"");
                        SpecialistAdapter specialistAdapter = new SpecialistAdapter(getActivity(),
                                sliderItems,HomeFragment.this);
                        recyclerView.setAdapter(specialistAdapter);
                        specialistAdapter.notifyDataSetChanged();
                    }
                }

                showDialogPython.dismissDialog();

            }
        });

        return view;
    }


    public void readData(MyCallbackAllUser myCallback) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            showDialogPython = new ShowDialogPython(getActivity(),getActivity().getLayoutInflater(),"load");
            db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    myCallback.onCallback(task);
                }
            });
        }
    }

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 0);
        ORIENTATIONS.append(Surface.ROTATION_90, 90);
        ORIENTATIONS.append(Surface.ROTATION_180, 180);
        ORIENTATIONS.append(Surface.ROTATION_270, 270);
    }

    @Override
    public void OnClick(String spec) {
        if(spec.equals("Chest X-Rays")){
            Intent intent = new Intent(getActivity(), ChestActivity.class);
            startActivity(intent);
        }else if(spec.equals("Brain Tumor")){
            Intent intent = new Intent(getActivity(), BrainActivity.class);
            startActivity(intent);
        }else if(spec.equals("Skin Cancer")){
            Intent intent = new Intent(getActivity(), SkinActivity.class);
            startActivity(intent);
        }else if(spec.equals("Heart")){
            Intent intent = new Intent(getActivity(), HeartActivity.class);
            startActivity(intent);
        }else if(spec.equals("Optical")){
            Intent intent = new Intent(getActivity(), OpticalActivity.class);
            startActivity(intent);
        }else{
            Intent intent = new Intent(getActivity(), DoctorsActivity.class);
            intent.putExtra("doctors", userAccountArrayList);
            intent.putExtra("spec",spec);
            startActivity(intent);
        }
    }
}