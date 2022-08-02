package com.example.drhello.ui.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.drhello.R;
import com.example.drhello.other.ShowDialogPython;
import com.example.drhello.model.UserAccount;
import com.example.drhello.ui.chats.StateOfUser;
import com.example.drhello.databinding.ActivityEditProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity {

    ActivityEditProfileBinding activityEditProfileBinding;
    private FirebaseFirestore db;
    private final int REQUEST_CODE_OPEN_Gallary_USER = 1 , REQUEST_CODE_OPEN_Gallary_DR = 2;
    private StorageReference storageRef;
    private Bitmap bitmap;
    private UserAccount userAccount;
    private HashMap map;
    private Locale[] locales = Locale.getAvailableLocales();
    private ArrayList<String> countries = new ArrayList<String>();
    private ArrayList<String> arrayAdaptermapcity =  new ArrayList<String>();
    ShowDialogPython showDialogPython;



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

        activityEditProfileBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile);


        for (Locale locale : locales) {
            String country = locale.getDisplayCountry();
            if (country.trim().length() > 0 && !countries.contains(country)) {
                countries.add(country);
            }
        }
        Collections.sort(countries);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, countries);
        activityEditProfileBinding.spinnerCountryUser.setAdapter(adapter);
        activityEditProfileBinding.spinnerCountryDr.setAdapter(adapter);

        try {
            ArrayList<Float> res = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(Objects.requireNonNull(JsonDataFromAsset("countriesToCities.json")));
            map = new Gson().fromJson(jsonObject.toString(), HashMap.class);
          /*  Log.e("CITIES :", map.get(countries.get(activityEditProfileBinding
                    .spinnerCountryUser.getSelectedItemPosition())).toString());
        */} catch (
                JSONException e) {
            e.printStackTrace();
        }


        activityEditProfileBinding.spinnerCountryUser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("CITIES :", map.get(countries.get(activityEditProfileBinding.spinnerCountryUser.getSelectedItemPosition())).toString());
                arrayAdaptermapcity = (ArrayList<String>) map.get(countries.get(activityEditProfileBinding.
                        spinnerCountryUser.getSelectedItemPosition()));
                ArrayAdapter<String> adapterCity = new ArrayAdapter<String>(EditProfileActivity.this,
                        android.R.layout.simple_spinner_item,
                        arrayAdaptermapcity);

                String name = "flag_"+countries.get(activityEditProfileBinding.
                        spinnerCountryUser.getSelectedItemPosition()).toLowerCase();
                int id = getResources().getIdentifier(name, "drawable", getPackageName());
             //   Drawable drawable = getResources().getDrawable(id);
                activityEditProfileBinding.imgCountry.setImageResource(id);
                activityEditProfileBinding.spinnerCityUser.setAdapter(adapterCity);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        activityEditProfileBinding.spinnerCountryDr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("CITIES :", map.get(countries.get(activityEditProfileBinding.spinnerCountryDr.getSelectedItemPosition())).toString());
                arrayAdaptermapcity = (ArrayList<String>) map.get(countries.get(activityEditProfileBinding.
                        spinnerCountryDr.getSelectedItemPosition()));
                ArrayAdapter<String> adapterCity = new ArrayAdapter<String>(EditProfileActivity.this,
                        android.R.layout.simple_spinner_item,
                        arrayAdaptermapcity);

                String name = "flag_"+countries.get(activityEditProfileBinding.
                        spinnerCountryDr.getSelectedItemPosition()).toLowerCase();
                int id = getResources().getIdentifier(name, "drawable", getPackageName());
                //   Drawable drawable = getResources().getDrawable(id);
                activityEditProfileBinding.imgCountryDr.setImageResource(id);
                activityEditProfileBinding.spinnerCityDr.setAdapter(adapterCity);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        if (getIntent().getSerializableExtra("userAccount") != null){
            Log.e("getIntent","userAccount");
            showDialogPython = new ShowDialogPython(EditProfileActivity.this,EditProfileActivity.this.getLayoutInflater(),"load");
             userAccount = (UserAccount) getIntent().getSerializableExtra("userAccount");
            if(userAccount.getUserInformation().getType().equals("normal user")){
                activityEditProfileBinding.layDr.setVisibility(View.GONE);
                activityEditProfileBinding.layUr.setVisibility(View.VISIBLE);
                activityEditProfileBinding.editPhoneUser.setEnabled(false);
                activityEditProfileBinding.editEmailUser.setEnabled(false);

                activityEditProfileBinding.editBirthUser.setHint(userAccount.getUserInformation().getDate_of_birth());
                activityEditProfileBinding.editNameUser.setHint(userAccount.getName());
                activityEditProfileBinding.editAddressUser.setHint(userAccount.getUserInformation().getAddress_home());
                activityEditProfileBinding.spinnerCityUser.setSelection(arrayAdaptermapcity.indexOf(userAccount.getUserInformation().getCity()));
                activityEditProfileBinding.spinnerCountryUser.setSelection(countries.indexOf(userAccount.getUserInformation().getCountry()));
                activityEditProfileBinding.editEmailUser.setHint(userAccount.getEmail());
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
                activityEditProfileBinding.spinnerCityDr.setSelection(arrayAdaptermapcity.indexOf(userAccount.getUserInformation().getCity()));
                activityEditProfileBinding.spinnerCountryDr.setSelection(countries.indexOf(userAccount.getUserInformation().getCountry()));
                activityEditProfileBinding.editEmailDr.setHint(userAccount.getEmail());
                activityEditProfileBinding.editPhoneDr.setHint(userAccount.getUserInformation().getPhone());
                activityEditProfileBinding.editSpecDr.setHint(userAccount.getUserInformation().getSpecification());
                activityEditProfileBinding.editSpecInDr.setHint(userAccount.getUserInformation().getSpecification_in());
                activityEditProfileBinding.editNameDr.setHint(userAccount.getName());
                activityEditProfileBinding.editWorkPlaceDr.setHint(userAccount.getUserInformation().getAddress_work());

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

            showDialogPython.dismissDialog();
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



    private String JsonDataFromAsset(String name) {
        String json = null;
        try {
            InputStream inputStream = EditProfileActivity.this.getAssets().open(name);
            int sizeOfFile = inputStream.available();
            byte[] bufferData = new byte[sizeOfFile];
            inputStream.read(bufferData);
            inputStream.close();
            json = new String(bufferData, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return json;
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

        if(!birth.equals("")){
            userAccount.getUserInformation().setDate_of_birth(birth);
        }
        if(!name.equals("")){
            userAccount.setName(name);
        }
        if(!address.equals("")){
            userAccount.getUserInformation().setAddress_home(address);
        }

        userAccount.getUserInformation().setCountry(countries.get(activityEditProfileBinding.
                spinnerCountryUser.getSelectedItemPosition()));
        userAccount.getUserInformation().setCity(arrayAdaptermapcity.get(activityEditProfileBinding.
                spinnerCityUser.getSelectedItemPosition()));

    }
    private void checkStateDr(){
        String birth = activityEditProfileBinding.editBirthDr.getText().toString();
        String name = activityEditProfileBinding.editNameDr.getText().toString();
        String address = activityEditProfileBinding.editAddressDr.getText().toString();

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

        if(!spec.equals("")){
            userAccount.getUserInformation().setSpecification(spec);
        }
        if(!specin.equals("")){
            userAccount.getUserInformation().setSpecification_in(specin);
        }

        userAccount.getUserInformation().setCountry(countries.get(activityEditProfileBinding.
                spinnerCountryDr.getSelectedItemPosition()));
        userAccount.getUserInformation().setCity(arrayAdaptermapcity.get(activityEditProfileBinding.
                spinnerCityDr.getSelectedItemPosition()));
    }

    private void updateData(String type){
        showDialogPython = new ShowDialogPython(EditProfileActivity.this,EditProfileActivity.this.getLayoutInflater(),"load");

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
                    showDialogPython.dismissDialog();
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





