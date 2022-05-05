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

        activityProfileBinding.imgFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        activityProfileBinding.imgEdit.setOnClickListener(new View.OnClickListener() {
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
                    activityProfileBinding.txtAddress.setText(userAccount.getUserInformation().getAddress_home());
                    activityProfileBinding.txtBirth.setText(userAccount.getUserInformation().getDate_of_birth());
                    activityProfileBinding.txtAddressWorkplace.setText(userAccount.getUserInformation().getAddress_home());
                    activityProfileBinding.txtCity.setText(userAccount.getUserInformation().getCity());
                    activityProfileBinding.txtEmail.setText(userAccount.getEmail());
                    activityProfileBinding.txtCountry.setText(userAccount.getUserInformation().getCountry());
                    activityProfileBinding.txtPhone.setText(userAccount.getUserInformation().getPhone());
                    activityProfileBinding.txtSpec.setText(userAccount.getUserInformation().getSpecification());
                    activityProfileBinding.txtSpecIn.setText(userAccount.getUserInformation().getSpecification_in());
                    activityProfileBinding.txtGender.setText(userAccount.getUserInformation().getGender());

                    try{
                        Glide.with(ProfileActivity.this).load(userAccount.getImg_profile()).placeholder(R.drawable.user).
                                error(R.drawable.user).into(activityProfileBinding.imgCurUser);
                    }catch (Exception e){
                        activityProfileBinding.imgCurUser.setImageResource(R.drawable.user);
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