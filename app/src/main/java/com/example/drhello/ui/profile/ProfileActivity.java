package com.example.drhello.ui.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.drhello.R;
import com.example.drhello.adapter.UserStateAdapter;
import com.example.drhello.databinding.ActivityProfileBinding;
import com.example.drhello.firebaseinterface.MyCallBackListenerComments;
import com.example.drhello.firebaseinterface.MyCallbackUser;
import com.example.drhello.model.UserState;
import com.example.drhello.ui.chats.StateOfUser;
import com.example.drhello.model.UserAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class ProfileActivity extends AppCompatActivity {

    ActivityProfileBinding activityProfileBinding;
    public static ProgressDialog mProgress;
    private UserAccount userAccount;
    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            getWindow().setStatusBarColor(Color.WHITE);
        }

        mProgress = new ProgressDialog(ProfileActivity.this);

        activityProfileBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        activityProfileBinding.imgFinishDr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        activityProfileBinding.imgEditDr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this,EditProfileActivity.class);
                intent.putExtra("userAccount",userAccount);
                startActivity(intent);
            }
        });

        activityProfileBinding.imgFinishUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        activityProfileBinding.imgEditUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this,EditProfileActivity.class);
                intent.putExtra("userAccount",userAccount);
                startActivity(intent);
            }
        });

        readData(new MyCallbackUser() {
            @Override
            public void onCallback(DocumentSnapshot documentSnapshot) {
                if(!documentSnapshot.exists()){
                    FirebaseAuth.getInstance().getCurrentUser().delete();
                }else{
                    userAccount = documentSnapshot.toObject(UserAccount.class);
                    if(userAccount.getUserInformation().getType().equals("normal user")){
                        activityProfileBinding.layUr.setVisibility(View.VISIBLE);
                        activityProfileBinding.layDr.setVisibility(View.GONE);
                        activityProfileBinding.txtAddressUser.setText(userAccount.getUserInformation().getAddress_home());
                        activityProfileBinding.txtBirthUser.setText(userAccount.getUserInformation().getDate_of_birth());
                        activityProfileBinding.txtCityUser.setText(userAccount.getUserInformation().getCity());
                        activityProfileBinding.txtCountryUser.setText(userAccount.getUserInformation().getCountry());
                        activityProfileBinding.txtEmailUser.setText(userAccount.getEmail());
                        activityProfileBinding.txtNameUserUr.setText(userAccount.getName());
                        activityProfileBinding.txtGenderUser.setText(userAccount.getUserInformation().getGender());
                        activityProfileBinding.txtPhoneUser.setText(userAccount.getUserInformation().getPhone());
                        try{
                            Glide.with(ProfileActivity.this).load(userAccount.getImg_profile()).placeholder(R.drawable.user).
                                    error(R.drawable.user).into(activityProfileBinding.imgCurUserUr);
                        }catch (Exception e){
                            activityProfileBinding.imgCurUserUr.setImageResource(R.drawable.user);
                        }
                    }else{
                        activityProfileBinding.layDr.setVisibility(View.VISIBLE);
                        activityProfileBinding.layUr.setVisibility(View.GONE);
                        activityProfileBinding.txtAddressDr.setText(userAccount.getUserInformation().getAddress_home());
                        activityProfileBinding.txtBirthDr.setText(userAccount.getUserInformation().getDate_of_birth());
                        activityProfileBinding.txtAddressWorkplace.setText(userAccount.getUserInformation().getAddress_work());
                        activityProfileBinding.txtCityDr.setText(userAccount.getUserInformation().getCity());
                        activityProfileBinding.txtEmailDr.setText(userAccount.getEmail());
                        activityProfileBinding.txtCountryDr.setText(userAccount.getUserInformation().getCountry());
                        activityProfileBinding.txtPhoneDr.setText(userAccount.getUserInformation().getPhone());
                        activityProfileBinding.txtSpecDr.setText(userAccount.getUserInformation().getSpecification());
                        activityProfileBinding.txtSpecInDr.setText(userAccount.getUserInformation().getSpecification_in());
                        activityProfileBinding.txtGenderDr.setText(userAccount.getUserInformation().getGender());
                        activityProfileBinding.txtNameUserDr.setText(userAccount.getName());
                        try{
                            Glide.with(ProfileActivity.this).load(userAccount.getImg_profile()).placeholder(R.drawable.user).
                                    error(R.drawable.user).into(activityProfileBinding.imgCurUserDr);
                        }catch (Exception e){
                            activityProfileBinding.imgCurUserDr.setImageResource(R.drawable.user);
                        }
                    }
                }
                mProgress.dismiss();
            }
        });
    }

    public void readData(MyCallbackUser myCallback) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            mProgress.setMessage("Loading..");
            mProgress.setCancelable(false);
            mProgress.show();
            FirebaseFirestore.getInstance().collection("users")
                    .document(currentUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    myCallback.onCallback(documentSnapshot);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        StateOfUser stateOfUser = new StateOfUser();
        stateOfUser.changeState("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        StateOfUser stateOfUser = new StateOfUser();
        stateOfUser.changeState("Offline");
    }



}