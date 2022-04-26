package com.example.drhello.ui.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.drhello.R;
import com.example.drhello.ui.chats.StateOfUser;
import com.example.drhello.databinding.ActivityEditProfileBinding;
import com.example.drhello.signup.SignUpMethods;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.regex.Pattern;

public class EditProfileActivity extends AppCompatActivity {
    Toolbar toolbar;
    ImageView imageView;
    ActivityEditProfileBinding activityEditProfileBinding;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private ProgressDialog mProgress;
    private final int REQUEST_CODE_OPEN_Gallary = 1;
    private StorageReference storageRef;
    private Bitmap bitmap;
    private String phone = "";
    private  UserInformation userInformation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            getWindow().setStatusBarColor(Color.WHITE);
        }


        activityEditProfileBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile);
        mProgress = new ProgressDialog(this);
        db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference().child("images/profiles/" + user.getUid());

        if (getIntent().getStringExtra("method") != null && getIntent().getSerializableExtra("userInformation") != null) {
            setDate();
            updataInformation("",(UserInformation) getIntent().getSerializableExtra("userInformation"));
        }

        toolbar = findViewById(R.id.toolbar_edit_editprofile);
        imageView = findViewById(R.id.img_back_edit_editprofile);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");

        activityEditProfileBinding.imgBackEditEditprofile.setOnClickListener(v -> finish());



        activityEditProfileBinding.editProImgEditprofile.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            String[] mimetypes = {"image/*", "video/*"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
            startActivityForResult(intent, REQUEST_CODE_OPEN_Gallary);
        });


        activityEditProfileBinding.btnUpdateEditprofile.setOnClickListener(view -> {
/*
            userInformation = new UserInformation(Objects.requireNonNull(activityEditProfileBinding.editLocationEditprofile.getEditText()).getText().toString(),
                        Objects.requireNonNull(activityEditProfileBinding.editUsernameEditprofile.getEditText()).getText().toString(),
                        Objects.requireNonNull(activityEditProfileBinding.editHomeEditprofile.getEditText()).getText().toString(),
                        Objects.requireNonNull(activityEditProfileBinding.editPhoneEditprofile.getEditText()).getText().toString(),
                        Objects.requireNonNull(activityEditProfileBinding.editSchoolEditprofile.getEditText()).getText().toString());
*/
                phone = activityEditProfileBinding.editPhoneEditprofile.getEditText().getText().toString().trim();
                mProgress.setTitle("Updateing Information");
                mProgress.setMessage("Please wait...");
                mProgress.setCancelable(false);
                mProgress.show();

                if (bitmap == null && activityEditProfileBinding.editPhoneEditprofile.getEditText().getText().toString().equals("")) {
                    mProgress.dismiss();
                    updataInformation("",userInformation);
                }
                else if(bitmap == null ){
                    checkphonenumbercorrecy(phone,"");
                }else {
                    uploadImage(bitmap);
                }

            });
    }

    private void setDate() {
        Objects.requireNonNull(activityEditProfileBinding.editLocationEditprofile.getEditText()).setText(activityEditProfileBinding.editLocationEditprofile.getEditText().getText().toString());
        Objects.requireNonNull(activityEditProfileBinding.editUsernameEditprofile.getEditText()).setText(activityEditProfileBinding.editUsernameEditprofile.getEditText().getText().toString());
        Objects.requireNonNull(activityEditProfileBinding.editHomeEditprofile.getEditText()).setText(activityEditProfileBinding.editHomeEditprofile.getEditText().getText().toString());
        Objects.requireNonNull(activityEditProfileBinding.editPhoneEditprofile.getEditText()).setText(activityEditProfileBinding.editPhoneEditprofile.getEditText().getText().toString());
        Objects.requireNonNull(activityEditProfileBinding.editSchoolEditprofile.getEditText()).setText(activityEditProfileBinding.editSchoolEditprofile.getEditText().getText().toString());
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_OPEN_Gallary && resultCode == Activity.RESULT_OK && data != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getBaseContext().getContentResolver(), data.getData());

                activityEditProfileBinding.editProImgEditprofile.setImageBitmap(bitmap);

                Toast.makeText(getBaseContext(), "Successful", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(getBaseContext(), "Canceled", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkphonenumbercorrecy(String phone , String url) {
        //check if phone
        if (phone.matches("[0-9]+") && isValidPhoneNumber(phone)) {
            //write code of phone here ya fady
            Log.e("PHONE : ", phone);
            String phonenum = "+" + "20" + phone;

     //       userInformation.setImg_profile(url);
            mProgress.dismiss();
            SignUpMethods signUpMethods = new SignUpMethods(EditProfileActivity.this, userInformation);
            signUpMethods.sendVerificationCode(phonenum);
            //         sendVerificationCode(phone);

        } else {
            Toast.makeText(getApplicationContext(), "invalid phone number , please try again!!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidPhoneNumber(String phone) {

        if (!phone.trim().equals("") && (phone.length() > 6 && phone.length() <= 13)
                && !Pattern.matches("[a-zA-Z]+", phone) && phone.matches("[0-9]+")) {
            return android.util.Patterns.PHONE.matcher(phone).matches();
        }

        return false;
    }

    private void uploadImage(Bitmap bitmap) {
        ByteArrayOutputStream output_image = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 25, output_image);
        byte[] data_image = output_image.toByteArray();

        storageRef.putBytes(data_image).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                //     progressBar_signup.setVisibility(View.GONE);
                getDownloadUrl();
                Toast.makeText(getApplicationContext(), "Successful Upload ", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "UnSuccessful Upload ", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(snapshot -> {
            //      progressBar_signup.setVisibility(View.VISIBLE);
        });
    }

    private void getDownloadUrl() {
        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Log.e("onSuccess : ", uri.toString());
            Toast.makeText(getBaseContext(), " Successful during get url of image ", Toast.LENGTH_SHORT).show();
            if(!phone.equals("")){
                Log.e("phone : ", "phone --> success");
                checkphonenumbercorrecy(phone,uri.toString());
            }else{
                mProgress.dismiss();
   //             userInformation.setImg_profile(uri.toString());
                updataInformation(uri.toString(),userInformation);
            }
        }).addOnFailureListener(e -> Toast.makeText(getBaseContext(), " unSuccessful during get url of image ", Toast.LENGTH_SHORT).show());
    }


    private void updataInformation(String url , UserInformation userInformation) {
        //users//id//userinformation//id --> to seperate data
        db.collection("users").document(user.getUid())
                .collection("userinfomation").document(user.getUid())
                .set(userInformation)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.e("updata pass : ", "finish");
                        Toast.makeText(getApplicationContext(), "Successful update user info.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "failed to update user info.", Toast.LENGTH_SHORT).show();
                    }
                    Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                });
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





