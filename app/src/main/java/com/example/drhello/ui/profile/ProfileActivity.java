package com.example.drhello.ui.profile;

import static com.facebook.FacebookSdk.getApplicationContext;

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
import com.example.drhello.FollowersModel;
import com.example.drhello.PostsUsersActivity;
import com.example.drhello.R;
import com.example.drhello.adapter.ImagePostsAdapter;
import com.example.drhello.adapter.UserStateAdapter;
import com.example.drhello.databinding.ActivityProfileBinding;
import com.example.drhello.firebaseinterface.MyCallBackAddFriend;
import com.example.drhello.firebaseinterface.MyCallBackListenerComments;
import com.example.drhello.firebaseinterface.MyCallbackUser;
import com.example.drhello.firebaseservice.FcmNotificationsSender;
import com.example.drhello.model.AddPersonModel;
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
import java.util.Map;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    ActivityProfileBinding activityProfileBinding;
    public static ProgressDialog mProgress;
    private UserAccount userAccount;
    private boolean flag_follow = false;
    private FirebaseFirestore db;
    private static final int REQUEST_CODE = 1;
    private String userId;

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

        if(getIntent().getStringExtra("userId") !=null){
            userId = getIntent().getStringExtra("userId");
        }else{
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            activityProfileBinding.lnDr.setVisibility(View.GONE);
            activityProfileBinding.lnUser.setVisibility(View.GONE);
        }

        if(userId != null){
            readDateInfo();
        }

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


        activityProfileBinding.lnFollowDr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickFollowers(userAccount);
            }
        });


        activityProfileBinding.lnFollowUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickFollowers(userAccount);
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


        activityProfileBinding.lnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String location = userAccount.getUserInformation().getAddress_work();
                String lat = location.substring(10).split(",")[0];
                String lon = location.substring(10).split(",")[1].replace(")","");
                String geoUri = "http://www.google.com/maps/place/" + lat + "," + lon + "";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                startActivity(intent);
            }
        });

        activityProfileBinding.lnPostUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, PostsUsersActivity.class);
                intent.putExtra("userAccount",userAccount);
                startActivity(intent);
            }
        });

        activityProfileBinding.lnPostsDr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, PostsUsersActivity.class);
                intent.putExtra("userAccount",userAccount);
                startActivity(intent);
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

    private void updataInformation(UserAccount userAccount) {
        mProgress.setMessage("Loading..");
        mProgress.setCancelable(false);
        mProgress.show();

        db.collection("users")
                .document(userAccount.getId())
                .set(userAccount)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        readDateInfo();
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
                        if(userId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            activityProfileBinding.txtStarUser.setText(userAccount.getFollowersModelMap().size()+"");
                        }else{
                            if(userAccount.getFollowersModelMap().containsKey(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                activityProfileBinding.txtStarUser.setText("Follow");
                                activityProfileBinding.imgbtnUser.setImageResource(R.drawable.select_star1);
                            }else{
                                activityProfileBinding.txtStarUser.setText("unFollow");
                                activityProfileBinding.imgbtnUser.setImageResource(R.drawable.star1);
                            }
                        }

                        String name = "flag_"+userAccount.getUserInformation().getCountry().toLowerCase();
                        int id = getResources().getIdentifier(name, "drawable", getPackageName());
                        activityProfileBinding.imgCountryUser.setImageResource(id);

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
                        activityProfileBinding.txtStartDr.setText(userAccount.getFollowersModelMap().size()+"");
                        if(userAccount.getFollowersModelMap().containsKey(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            activityProfileBinding.imgbtnDr.setImageResource(R.drawable.select_star1);
                        }else{
                            activityProfileBinding.imgbtnDr.setImageResource(R.drawable.star1);
                        }

                        String name = "flag_"+userAccount.getUserInformation().getCountry().toLowerCase();
                        int id = getResources().getIdentifier(name, "drawable", getPackageName());
                        activityProfileBinding.imgCountryDr.setImageResource(id);

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
        if (userId != null) {
            mProgress.setMessage("Loading..");
            mProgress.setCancelable(false);
            mProgress.show();
            FirebaseFirestore.getInstance().collection("users")
                    .document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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


    public void onClickFollowers(UserAccount userAccountfriend) {
        mProgress.setMessage("Uploading..");
        mProgress.setCancelable(false);
        mProgress.show();
        String userIdMe = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Map<String, FollowersModel> followersModelMap = userAccountfriend.getFollowersModelMap();

        if (!followersModelMap.containsKey(userIdMe)){ //follow
            followersModelMap.put(userIdMe, new FollowersModel(userIdMe));
            userAccountfriend.setFollowersModelMap(followersModelMap);
        }else{
            followersModelMap.remove(userIdMe);
            userAccountfriend.setFollowersModelMap(followersModelMap);
        }
        updataInformation(userAccountfriend);
    }


}