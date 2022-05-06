package com.example.drhello.ui.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.drhello.R;
import com.example.drhello.model.UserAccount;
import com.example.drhello.ui.chats.StateOfUser;
import com.example.drhello.databinding.ActivityEditProfileBinding;
import com.example.drhello.signup.SignUpMethods;
import com.example.drhello.ui.login.CompleteInfoActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class EditProfileActivity extends AppCompatActivity {

    ActivityEditProfileBinding activityEditProfileBinding;
    private FirebaseFirestore db;
    private ProgressDialog mProgress;
    private final int REQUEST_CODE_OPEN_Gallary_USER = 1 , REQUEST_CODE_OPEN_Gallary_DR = 2;
    private StorageReference storageRef;
    private Bitmap bitmap;
    private UserAccount userAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            getWindow().setStatusBarColor(Color.WHITE);
        }

        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference().child("images/profiles/" + FirebaseAuth.getInstance().getCurrentUser().getUid());

        mProgress = new ProgressDialog(EditProfileActivity.this);

        activityEditProfileBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile);



        if (getIntent().getSerializableExtra("userAccount") != null){
            Log.e("getIntent","userAccount");
            mProgress.setMessage("Loading..");
            mProgress.setCancelable(false);
            mProgress.show();
             userAccount = (UserAccount) getIntent().getSerializableExtra("userAccount");
            if(userAccount.getUserInformation().getType().equals("normal user")){
                activityEditProfileBinding.layDr.setVisibility(View.GONE);
                activityEditProfileBinding.layUr.setVisibility(View.VISIBLE);
                activityEditProfileBinding.editPhoneUser.setEnabled(false);
                activityEditProfileBinding.editEmailUser.setEnabled(false);

                activityEditProfileBinding.editBirthUser.setHint(userAccount.getUserInformation().getDate_of_birth());
                activityEditProfileBinding.editNameUser.setHint(userAccount.getName());
                activityEditProfileBinding.editAddressUser.setHint(userAccount.getUserInformation().getAddress_home());
                activityEditProfileBinding.editCityUser.setHint(userAccount.getUserInformation().getCity());
                activityEditProfileBinding.editEmailUser.setHint(userAccount.getEmail());
                activityEditProfileBinding.editCountryUser.setHint(userAccount.getUserInformation().getCountry());
                activityEditProfileBinding.editPhoneUser.setHint(userAccount.getUserInformation().getPhone());

                try{
                    Glide.with(EditProfileActivity.this).load(userAccount.getImg_profile()).placeholder(R.drawable.user).
                            error(R.drawable.user).into(activityEditProfileBinding.imgCurUser);
                }catch (Exception e){
                    activityEditProfileBinding.imgCurUser.setImageResource(R.drawable.user);
                }

                activityEditProfileBinding.editBirthUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Calendar calendar = Calendar.getInstance();
                        final int year = calendar.get(Calendar.YEAR);
                        final int month = calendar.get(Calendar.MONTH);
                        final int day = calendar.get(Calendar.DAY_OF_MONTH);
                        DatePickerDialog datePickerDialog = new DatePickerDialog(
                                EditProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int day) {
                                month = month + 1;
                                String date = day + ":" + month + ":" + year;
                                activityEditProfileBinding.editBirthUser.setText(date);
                            }
                        }, year, month, day);
                        datePickerDialog.show();
                    }
                });


                activityEditProfileBinding.imgFinishUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });

                activityEditProfileBinding.imgEditUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateData("");
                    }
                });

            }else{

                activityEditProfileBinding.layDr.setVisibility(View.VISIBLE);
                activityEditProfileBinding.layUr.setVisibility(View.GONE);
                activityEditProfileBinding.editPhoneDr.setEnabled(false);
                activityEditProfileBinding.editEmailDr.setEnabled(false);

                activityEditProfileBinding.editAddressDr.setHint(userAccount.getUserInformation().getAddress_home());
                activityEditProfileBinding.editBirthDr.setHint(userAccount.getUserInformation().getDate_of_birth());
                activityEditProfileBinding.editCityDr.setHint(userAccount.getUserInformation().getCity());
                activityEditProfileBinding.editEmailDr.setHint(userAccount.getEmail());
                activityEditProfileBinding.editPhoneDr.setHint(userAccount.getUserInformation().getPhone());
                activityEditProfileBinding.editSpecDr.setHint(userAccount.getUserInformation().getSpecification());
                activityEditProfileBinding.editSpecInDr.setHint(userAccount.getUserInformation().getSpecification_in());
                activityEditProfileBinding.editNameDr.setHint(userAccount.getName());
                activityEditProfileBinding.editWorkPlaceDr.setHint(userAccount.getUserInformation().getAddress_work());
                activityEditProfileBinding.editCountryDr.setHint(userAccount.getUserInformation().getCountry());

                try{
                    Glide.with(EditProfileActivity.this).load(userAccount.getImg_profile()).placeholder(R.drawable.user).
                            error(R.drawable.user).into(activityEditProfileBinding.imgCurDr);
                }catch (Exception e){
                    activityEditProfileBinding.imgCurDr.setImageResource(R.drawable.user);
                }

                activityEditProfileBinding.editBirthDr.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Calendar calendar = Calendar.getInstance();
                        final int year = calendar.get(Calendar.YEAR);
                        final int month = calendar.get(Calendar.MONTH);
                        final int day = calendar.get(Calendar.DAY_OF_MONTH);
                        DatePickerDialog datePickerDialog = new DatePickerDialog(
                                EditProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int day) {
                                month = month + 1;
                                String date = day + ":" + month + ":" + year;
                                activityEditProfileBinding.editBirthDr.setText(date);
                            }
                        }, year, month, day);
                        datePickerDialog.show();
                    }
                });

                activityEditProfileBinding.imgFinishDr.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });

                activityEditProfileBinding.imgEditDr.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateData("Doctor");
                    }
                });
            }

            mProgress.dismiss();
        }

        activityEditProfileBinding.imgCameraUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                String[] mimetypes = {"image/*", "video/*"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                startActivityForResult(intent, REQUEST_CODE_OPEN_Gallary_USER);
            }
        });

        activityEditProfileBinding.imgCameraDr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                String[] mimetypes = {"image/*", "video/*"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                startActivityForResult(intent, REQUEST_CODE_OPEN_Gallary_DR);
            }
        });

    }




    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_OPEN_Gallary_USER && resultCode == Activity.RESULT_OK && data != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getBaseContext().getContentResolver(), data.getData());
                activityEditProfileBinding.imgCurUser.setImageBitmap(bitmap);
                Toast.makeText(getBaseContext(), "Successful", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if (requestCode == REQUEST_CODE_OPEN_Gallary_DR && resultCode == Activity.RESULT_OK && data != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getBaseContext().getContentResolver(), data.getData());
                activityEditProfileBinding.imgCurDr.setImageBitmap(bitmap);
                Toast.makeText(getBaseContext(), "Successful", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(getBaseContext(), "Canceled", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkStateUser(){
        String birth = activityEditProfileBinding.editBirthUser.getText().toString();
        String name = activityEditProfileBinding.editNameUser.getText().toString();
        String address = activityEditProfileBinding.editAddressUser.getText().toString();
        String city = activityEditProfileBinding.editCityUser.getText().toString();
        String country = activityEditProfileBinding.editCountryUser.getText().toString();

        if(!birth.equals("")){
            userAccount.getUserInformation().setDate_of_birth(birth);
        }
        if(!name.equals("")){
            userAccount.setName(name);
        }
        if(!address.equals("")){
            userAccount.getUserInformation().setAddress_home(address);
        }
        if(!city.equals("")){
            userAccount.getUserInformation().setCity(city);
        }
        if(!country.equals("")){
            userAccount.getUserInformation().setCountry(country);
        }
    }
    private void checkStateDr(){
        String birth = activityEditProfileBinding.editBirthDr.getText().toString();
        String name = activityEditProfileBinding.editNameDr.getText().toString();
        String address = activityEditProfileBinding.editAddressDr.getText().toString();
        String city = activityEditProfileBinding.editCityDr.getText().toString();
        String country = activityEditProfileBinding.editCountryDr.getText().toString();
        String workplace = activityEditProfileBinding.editCountryDr.getText().toString();
        String spec = activityEditProfileBinding.editSpecDr.getText().toString();
        String specin = activityEditProfileBinding.editSpecInDr.getText().toString();

        if(!birth.equals("")){
            userAccount.getUserInformation().setDate_of_birth(birth);
        }
        if(!name.equals("")){
            userAccount.setName(name);
        }
        if(!address.equals("")){
            userAccount.getUserInformation().setAddress_home(address);
        }
        if(!city.equals("")){
            userAccount.getUserInformation().setCity(city);
        }
        if(!country.equals("")){
            userAccount.getUserInformation().setCountry(country);
        }
        if(!workplace.equals("")){
            userAccount.getUserInformation().setAddress_work(workplace);
        }
        if(!spec.equals("")){
            userAccount.getUserInformation().setSpecification(spec);
        }
        if(!specin.equals("")){
            userAccount.getUserInformation().setSpecification_in(specin);
        }
    }

    private void updateData(String type){
        mProgress.setTitle("Updateing Information");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.show();

        if(type.equals("Doctor")){
            checkStateDr();
        }else{
            checkStateUser();
        }

        if(bitmap == null ){
            updataInformation("",userAccount);
        }else {
            uploadImage(bitmap,userAccount);
        }
    }
    private void uploadImage(Bitmap bitmap,UserAccount userAccount) {
        ByteArrayOutputStream output_image = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 25, output_image);
        byte[] data_image = output_image.toByteArray();
        storageRef.putBytes(data_image).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                getDownloadUrl(userAccount);
                Toast.makeText(getApplicationContext(), "Successful Upload ", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "UnSuccessful Upload ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getDownloadUrl(UserAccount userAccount) {
        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Log.e("onSuccess : ", uri.toString());
            Toast.makeText(getBaseContext(), " Successful during get url of image ", Toast.LENGTH_SHORT).show();
            updataInformation(uri.toString(),userAccount);
        }).addOnFailureListener(e -> Toast.makeText(getBaseContext(), " unSuccessful during get url of image ", Toast.LENGTH_SHORT).show());
    }


    private void updataInformation(String url , UserAccount userAccount) {
        //users//id//userinformation//id --> to seperate data
        if(!url.equals("")){
            userAccount.setImg_profile(url);

        }
        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .set(userAccount)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.e("updata pass : ", "finish");
                        Toast.makeText(getApplicationContext(), "Successful update user info.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "failed to update user info.", Toast.LENGTH_SHORT).show();
                    }
                    mProgress.dismiss();
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





