package com.example.drhello.ui.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.drhello.R;
import com.example.drhello.adapter.ImagePostsAdapter;
import com.example.drhello.adapter.UserStateAdapter;
import com.example.drhello.databinding.ActivityProfileBinding;
import com.example.drhello.firebaseinterface.MyCallBackListenerComments;
import com.example.drhello.firebaseinterface.MyCallbackUser;
import com.example.drhello.model.UserState;
import com.example.drhello.ui.chats.StateOfUser;
import com.example.drhello.model.UserAccount;
import com.example.drhello.ui.writepost.WritePostsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    ActivityProfileBinding activityProfileBinding;
    public static ProgressDialog mProgress;
    private UserAccount userAccount;
    private boolean flag_follow = false;
    private FirebaseFirestore db;
    int followers = 0 ;
    String follow = "";
    private static final int REQUEST_CODE = 1;

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
        db = FirebaseFirestore.getInstance();

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
                    followers = userAccount.getFollowers();
                    follow = userAccount.getFollows();
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
                        /*
                        activityProfileBinding.txtStarUser.setText(userAccount.getFollows());
                        if(userAccount.getFollows().equals("follow")){
                            activityProfileBinding.imgbtnUser.setImageResource(R.drawable.select_star1);
                        }else{
                            activityProfileBinding.imgbtnUser.setImageResource(R.drawable.star1);
                        }
                         */

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
                        activityProfileBinding.txtStartDr.setText(userAccount.getFollowers()+"");
                        /*
                        if(userAccount.getFollows().equals("follow")){
                            activityProfileBinding.imgbtnUser.setImageResource(R.drawable.select_star1);
                        }else{
                            activityProfileBinding.imgbtnUser.setImageResource(R.drawable.star1);
                        }

                         */
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


        activityProfileBinding.lnFollowDr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!flag_follow){
                    flag_follow = true;
                    activityProfileBinding.imgbtnDr.setImageResource(R.drawable.select_star1);
                    followers = followers + 1;
                    follow = "follow";
                }else{
                    flag_follow = false;
                    activityProfileBinding.imgbtnDr.setImageResource(R.drawable.star1);
                    followers = followers - 1;
                    follow = "unfollow";
                }
                updataInformation(follow,followers,userAccount);
            }
        });


        activityProfileBinding.lnFollowUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!flag_follow){
                    flag_follow = true;
                    activityProfileBinding.imgbtnUser.setImageResource(R.drawable.select_star1);
                    activityProfileBinding.txtStarUser.setText("follow");
                    followers = followers + 1;
                    follow = "follow";
                }else{
                    flag_follow = false;
                    activityProfileBinding.imgbtnUser.setImageResource(R.drawable.star1);
                    activityProfileBinding.txtStarUser.setText("unfollow");
                    followers = followers - 1;
                    follow = "unfollow";
                }
                updataInformation(follow,followers,userAccount);
            }
        });

        activityProfileBinding.lnCallUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ProfileActivity.this,
                            new String[]{Manifest.permission.CALL_PHONE},
                            REQUEST_CODE);
                    Log.e("PERS","PERS");
                } else {
                    // else block means user has already accepted.And make your phone call here.
                    String uri = "tel:" + userAccount.getUserInformation().getPhone();
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse(uri));
                    startActivity(intent);
                    Log.e("PERS","intent");

                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE && resultCode==RESULT_OK){
            String uri = "tel:" + userAccount.getUserInformation().getPhone();
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse(uri));
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            // for each permission check if the user granted/denied them
            // you may want to group the rationale in a single dialog,
            // this is just an example
            for (int i = 0, len = permissions.length; i < len; i++) {
                String permission = permissions[i];
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    // user rejected the permission
                    boolean showRationale = false;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        showRationale = shouldShowRequestPermissionRationale(permission);
                    }
                    if (!showRationale) {
                        // user also CHECKED "never ask again"
                        // you can either enable some fall back,
                        // disable features of your app
                        // or open another dialog explaining
                        // again the permission and directing to
                        // the app setting
                        Log.e("PERS","setting");
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_CODE);
                    } else if (Manifest.permission.WRITE_CONTACTS.equals(permission)) {
                        //showRationale(permission, R.string.permission_denied_contacts);
                        // user did NOT check "never ask again"
                        // this is a good place to explain the user
                        // why you need the permission and ask if he wants
                        // to accept it (the rationale)
                        Log.e("PERS","rationale");

                    }
                }
            }
        }
    }

    private void updataInformation(String follow,int followers,UserAccount userAccount) {
        mProgress.setMessage("Loading..");
        mProgress.setCancelable(false);
        mProgress.show();
        userAccount.setFollowers(followers);
        userAccount.setFollows(follow);
        db.collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .set(userAccount)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if(userAccount.getUserInformation().getType().equals("normal user")){

                        }else{
                            readDateInfo();
                        }
                        Toast.makeText(getApplicationContext(), "Successful Follow Doctor.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed Follow Doctor.", Toast.LENGTH_SHORT).show();
                    }
                    mProgress.dismiss();
                });
    }

    private void readDateInfo(){
        readData(new MyCallbackUser() {
            @Override
            public void onCallback(DocumentSnapshot documentSnapshot) {
                if(!documentSnapshot.exists()){
                    FirebaseAuth.getInstance().getCurrentUser().delete();
                }else{
                    userAccount = documentSnapshot.toObject(UserAccount.class);
                    followers = userAccount.getFollowers();
                    follow = userAccount.getFollows();
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
                        activityProfileBinding.txtStarUser.setText(userAccount.getFollows());
                        if(userAccount.getFollows().equals("follow")){
                            activityProfileBinding.imgbtnUser.setImageResource(R.drawable.select_star1);
                        }else{
                            activityProfileBinding.imgbtnUser.setImageResource(R.drawable.star1);
                        }

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
                        activityProfileBinding.txtStartDr.setText(userAccount.getFollowers()+"");
                        if(userAccount.getFollows().equals("follow")){
                            activityProfileBinding.imgbtnUser.setImageResource(R.drawable.select_star1);
                        }else{
                            activityProfileBinding.imgbtnUser.setImageResource(R.drawable.star1);
                        }
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