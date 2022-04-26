package com.example.drhello.ui.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.drhello.R;
import com.example.drhello.ui.chats.StateOfUser;
import com.example.drhello.databinding.ActivityCompleteInfoBinding;
import com.example.drhello.model.UserAccount;
import com.example.drhello.ui.main.MainActivity;
import com.example.drhello.ui.profile.UserInformation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
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
import java.util.regex.Pattern;

public class CompleteInfoActivity extends AppCompatActivity {
    private ActivityCompleteInfoBinding activityCompleteInfoBinding;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String gender;
    private Locale[] locales = Locale.getAvailableLocales();
    private ArrayList<String> countries = new ArrayList<String>();
    private static final int Gallary_REQUEST_CODE = 1;
    private UserAccount userAccount;
    private Bitmap bitmap;
    private HashMap map;
    private ArrayList<String> arrayAdaptermap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_info);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            getWindow().setStatusBarColor(Color.WHITE);
        }

        activityCompleteInfoBinding = DataBindingUtil.setContentView(CompleteInfoActivity.this, R.layout.activity_complete_info);
        activityCompleteInfoBinding.shimmer.startShimmerAnimation();

        if (getIntent().getSerializableExtra("userAccount") != null) {
            userAccount = (UserAccount) getIntent().getSerializableExtra("userAccount");
        }


        //To get user’s birthday

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        activityCompleteInfoBinding.txtBirthdayUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        CompleteInfoActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month + 1;
                        String date = day + ":" + month + ":" + year;
                        activityCompleteInfoBinding.txtBirthdayUser.setText(date);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        activityCompleteInfoBinding.txtBirthdayDr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        CompleteInfoActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month + 1;
                        String date = day + ":" + month + ":" + year;
                        activityCompleteInfoBinding.txtBirthdayDr.setText(date);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });


        activityCompleteInfoBinding.imgbtnMaleDr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityCompleteInfoBinding.imgbtnMaleDr.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.btn_selected_info));
                activityCompleteInfoBinding.imgbtnFemaleDr.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.btn_unselected_info));
                activityCompleteInfoBinding.imgbtnMaleDr.setImageResource(R.drawable.ic_male_selected);
                activityCompleteInfoBinding.imgbtnFemaleDr.setImageResource(R.drawable.ic_female_unselected);
                gender = "Male";
            }
        });

        activityCompleteInfoBinding.imgbtnFemaleDr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityCompleteInfoBinding.imgbtnFemaleDr.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.btn_selected_info));
                activityCompleteInfoBinding.imgbtnMaleDr.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.btn_unselected_info));
                activityCompleteInfoBinding.imgbtnMaleDr.setImageResource(R.drawable.ic_male_unselected);
                activityCompleteInfoBinding.imgbtnFemaleDr.setImageResource(R.drawable.ic_female_selected);
                gender = "Female";
            }
        });

        activityCompleteInfoBinding.imgbtnMaleUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityCompleteInfoBinding.imgbtnMaleUser.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.btn_selected_info));
                activityCompleteInfoBinding.imgbtnFemaleUser.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.btn_unselected_info));
                activityCompleteInfoBinding.imgbtnMaleUser.setImageResource(R.drawable.ic_male_selected);
                activityCompleteInfoBinding.imgbtnFemaleUser.setImageResource(R.drawable.ic_female_unselected);
                gender = "Male";
            }
        });

        activityCompleteInfoBinding.imgbtnFemaleUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityCompleteInfoBinding.imgbtnFemaleUser.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.btn_selected_info));
                activityCompleteInfoBinding.imgbtnMaleUser.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.btn_unselected_info));
                activityCompleteInfoBinding.imgbtnMaleUser.setImageResource(R.drawable.ic_male_unselected);
                activityCompleteInfoBinding.imgbtnFemaleUser.setImageResource(R.drawable.ic_female_selected);
                gender = "Female";
            }
        });

        //To switch between doctor and user layout
        activityCompleteInfoBinding.switchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activityCompleteInfoBinding.switchLayout.isChecked()) {
                    activityCompleteInfoBinding.layDoctor.setVisibility(View.VISIBLE);
                    activityCompleteInfoBinding.layUser.setVisibility(View.GONE);
                } else {
                    activityCompleteInfoBinding.layDoctor.setVisibility(View.GONE);
                    activityCompleteInfoBinding.layUser.setVisibility(View.VISIBLE);
                }
            }
        });


        for (Locale locale : locales) {
            String country = locale.getDisplayCountry();

            if (country.trim().length() > 0 && !countries.contains(country)) {
                countries.add(country);
            }
        }

        Collections.sort(countries);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, countries);
        activityCompleteInfoBinding.spinnerCountryUser.setAdapter(adapter);
        activityCompleteInfoBinding.spinnerCountryDr.setAdapter(adapter);

        try {
            ArrayList<Float> res = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(Objects.requireNonNull(JsonDataFromAsset("countriesToCities.json")));
            map = new Gson().fromJson(jsonObject.toString(), HashMap.class);
            Log.e("CITIES :", map.get(countries.get(activityCompleteInfoBinding.spinnerCountryUser.getSelectedItemPosition())).toString());
        } catch (
                JSONException e) {
            e.printStackTrace();
        }

        activityCompleteInfoBinding.spinnerCountryUser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("CITIES :", map.get(countries.get(activityCompleteInfoBinding.spinnerCountryUser.getSelectedItemPosition())).toString());
                arrayAdaptermap = (ArrayList<String>) map.get(countries.get(activityCompleteInfoBinding.
                        spinnerCountryUser.getSelectedItemPosition()));
                ArrayAdapter<String> adapterCity = new ArrayAdapter<String>(CompleteInfoActivity.this,
                        android.R.layout.simple_spinner_item,
                        arrayAdaptermap);
                activityCompleteInfoBinding.spinnerCityUser.setAdapter(adapterCity);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        activityCompleteInfoBinding.spinnerCountryDr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("CITIES :", map.get(countries.get(activityCompleteInfoBinding.spinnerCountryDr.getSelectedItemPosition())).toString());
                arrayAdaptermap = (ArrayList<String>) map.get(countries.get(activityCompleteInfoBinding.
                        spinnerCountryDr.getSelectedItemPosition()));
                ArrayAdapter<String> adapterCity = new ArrayAdapter<String>(CompleteInfoActivity.this,
                        android.R.layout.simple_spinner_item,
                        arrayAdaptermap);
                activityCompleteInfoBinding.spinnerCityDr.setAdapter(adapterCity);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        activityCompleteInfoBinding.btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activityCompleteInfoBinding.switchLayout.isChecked()) {  //doctor
                    checkValidation();
                } else { // normal user
                    String fullNumber = activityCompleteInfoBinding.ccp.getFullNumber();
                    UserInformation userInformation = new UserInformation(
                            countries.get(activityCompleteInfoBinding.spinnerCountryUser.getSelectedItemPosition()),
                            activityCompleteInfoBinding.editAddressUser.getText().toString(),
                            arrayAdaptermap.get(activityCompleteInfoBinding.spinnerCityUser.getSelectedItemPosition()),
                            activityCompleteInfoBinding.editStateUser.getText().toString(),
                            "",
                            "",
                            "",
                            activityCompleteInfoBinding.txtBirthdayUser.getText().toString(),
                            gender, "normal user","");
                    userInformation.setPhone(activityCompleteInfoBinding.editPhoneUser.getText().toString());
                    checkphonenumbercorrecy(userAccount, activityCompleteInfoBinding.editPhoneUser.getText().toString(), fullNumber + activityCompleteInfoBinding.editPhoneUser.getText().toString(), userInformation);
                }
            }
        });

        activityCompleteInfoBinding.btnImageDr.setOnClickListener(new View.OnClickListener() {
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

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Gallary_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getBaseContext().getContentResolver(), data.getData());
                //     Toast.makeText(getBaseContext(), "Successful", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Log.e("gallary exception: ", e.getMessage());
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // Toast.makeText(getBaseContext(), "Canceled", Toast.LENGTH_SHORT).show();
        }

    }


    private void checkphonenumbercorrecy(UserAccount userAccount, String pphone, String phone, UserInformation userInformation) {
        //check if phone

        if (pphone.matches("[0-9]+") && isValidPhoneNumber(pphone)) {
            //write code of phone here ya fady
            Log.e("PHONE : ", phone);
            String phonenum = "+" + phone;
       /*     SignUpMethods signUpMethods = new SignUpMethods(userAccount, CompleteInfoActivity.this, userInformation);
            signUpMethods.sendVerificationCode(phonenum);
*/
            updataInformation(userInformation);
        } else {
            Toast.makeText(getApplicationContext(), "invalid phone number , please try again!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updataInformation(UserInformation userInformation) {
        //users//id//userinformation//id --> to seperate data
        userAccount.setUserInformation(userInformation);
        db.collection("users").document(mAuth.getCurrentUser().getUid())
                .set(userAccount)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.e("updata pass : ", "finish");
                        Intent intent = new Intent(CompleteInfoActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("userInformation", getIntent().getSerializableExtra("userInformation"));
                        intent.putExtra("method", "PHONE");
                        startActivity(intent);
                        //        Toast.makeText(getApplicationContext(), "Successful update user info.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "failed to update user info.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean isValidPhoneNumber(String phone) {

        if (!phone.trim().equals("") && (phone.length() > 6 && phone.length() <= 13)
                && !Pattern.matches("[a-zA-Z]+", phone) && phone.matches("[0-9]+")) {
            return android.util.Patterns.PHONE.matcher(phone).matches();
        }

        return false;
    }

    private void checkValidation() {

        String editClinicDr = Objects.requireNonNull(activityCompleteInfoBinding.editClinicDr).getText().toString().trim();
        String editSpecificStateDr = Objects.requireNonNull(activityCompleteInfoBinding.editSpecificStateDr).getText().toString().trim();
        String editSpecificDr = Objects.requireNonNull(activityCompleteInfoBinding.editSpecificDr).getText().toString().trim();
        String editAddressDr = Objects.requireNonNull(activityCompleteInfoBinding.editAddressDr).getText().toString().trim();
        String editStateDr = Objects.requireNonNull(activityCompleteInfoBinding.editStateDr).getText().toString().trim();
        String editEducationDr = Objects.requireNonNull(activityCompleteInfoBinding.editEducationDr).getText().toString().trim();
        String editAddressEducationDr = Objects.requireNonNull(activityCompleteInfoBinding.editAddressEducationDr).getText().toString().trim();
        String editPhoneDr = Objects.requireNonNull(activityCompleteInfoBinding.editPhoneDr).getText().toString().trim();


        if (editClinicDr.isEmpty()) {
            activityCompleteInfoBinding.editClinicDr.setError("Name Clinic is needed");
            activityCompleteInfoBinding.editClinicDr.requestFocus();
            return;
        }
        if (editAddressDr.isEmpty()) {
            activityCompleteInfoBinding.editAddressDr.setError("Address is needed");
            activityCompleteInfoBinding.editAddressDr.requestFocus();
            return;
        }
        if (editStateDr.isEmpty()) {
            activityCompleteInfoBinding.editStateDr.setError("Address State is needed");
            activityCompleteInfoBinding.editStateDr.requestFocus();
            return;
        }
        if (editEducationDr.isEmpty()) {
            activityCompleteInfoBinding.editEducationDr.setError("Education State is needed");
            activityCompleteInfoBinding.editEducationDr.requestFocus();
            return;
        }
        if (editAddressEducationDr.isEmpty()) {
            activityCompleteInfoBinding.editAddressEducationDr.setError("Address Education State is needed");
            activityCompleteInfoBinding.editAddressEducationDr.requestFocus();
            return;
        }
        if (editPhoneDr.isEmpty()) {
            activityCompleteInfoBinding.editPhoneDr.setError("Phone State is needed");
            activityCompleteInfoBinding.editPhoneDr.requestFocus();
            return;
        }
        if (editSpecificDr.isEmpty()) {
            activityCompleteInfoBinding.editSpecificDr.setError("Specification is needed");
            activityCompleteInfoBinding.editSpecificDr.requestFocus();
            return;
        }
        if (editSpecificStateDr.isEmpty()) {
            activityCompleteInfoBinding.editSpecificStateDr.setError("Specification State is needed");
            activityCompleteInfoBinding.editSpecificStateDr.requestFocus();
            return;
        }

        if (editSpecificStateDr.isEmpty()) {
            activityCompleteInfoBinding.editSpecificStateDr.setError("Specification State is needed");
            activityCompleteInfoBinding.editSpecificStateDr.requestFocus();
            return;
        }
        if (bitmap == null) {
            Toast.makeText(getApplicationContext(), "Please, Check your Certificate", Toast.LENGTH_SHORT).show();
            return;
        } else {
            uploadImage(bitmap);
        }
    }

    private void uploadImage(Bitmap bitmap) {
        ByteArrayOutputStream output_image = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output_image);
        byte[] data_image = output_image.toByteArray();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/imagesChat/" + userAccount.getId());
        storageReference.putBytes(data_image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.e("uri.toString() : ", uri.toString());
                        String fullNumber = activityCompleteInfoBinding.ccpDr.getFullNumber();
                        UserInformation userInformation = new UserInformation("Doctor",
                                countries.get(activityCompleteInfoBinding.spinnerCountryDr.getSelectedItemPosition()),
                                activityCompleteInfoBinding.editAddressDr.getText().toString(),
                                arrayAdaptermap.get(activityCompleteInfoBinding.spinnerCityDr.getSelectedItemPosition()),
                                activityCompleteInfoBinding.editStateDr.getText().toString(),
                                fullNumber + activityCompleteInfoBinding.editPhoneDr.getText().toString(),
                                activityCompleteInfoBinding.editAddressEducationDr.getText().toString(),
                                activityCompleteInfoBinding.editEducationDr.getText().toString(),
                                activityCompleteInfoBinding.txtBirthdayUser.getText().toString(),
                                gender,
                                activityCompleteInfoBinding.editSpecificDr.getText().toString(),
                                activityCompleteInfoBinding.editSpecificStateDr.getText().toString(),
                                uri.toString(),
                                activityCompleteInfoBinding.editClinicDr.getText().toString());

                        userInformation.setPhone(activityCompleteInfoBinding.editPhoneDr.getText().toString());
                        checkphonenumbercorrecy(userAccount, activityCompleteInfoBinding.editPhoneDr.getText().toString(), fullNumber + activityCompleteInfoBinding.editPhoneDr.getText().toString(), userInformation);

                        // Toast.makeText(getBaseContext(), "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("uri ", "Successful Upload");
                //Toast.makeText(getApplicationContext(),"Successful Upload ",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(getApplicationContext(),"UnSuccessful Upload ",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String JsonDataFromAsset(String name) {
        String json = null;
        try {
            InputStream inputStream = CompleteInfoActivity.this.getAssets().open(name);
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