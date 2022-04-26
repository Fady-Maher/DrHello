package com.example.drhello.ui.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.drhello.R;
import com.example.drhello.ui.chats.StateOfUser;
import com.example.drhello.model.UserAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {
    ImageView imageView;
    ImageView image_user;
    TextView user_name, user_location, user_school, user_home, user_email, user_phone;
    ImageView back_profile;
    UserAccount userAccount;
    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        getInformationUser(mAuth,db);
        back_profile = findViewById(R.id.back_profile);
        user_name = findViewById(R.id.user_name);
        user_location = findViewById(R.id.user_location);
        user_home = findViewById(R.id.user_home);
        user_email = findViewById(R.id.user_email);
        user_phone = findViewById(R.id.user_phone);
        user_school = findViewById(R.id.user_school);
        image_user = findViewById(R.id.user_image);
        /*
        userViewModel = new UserViewModel();
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

        // all data of user

        userViewModel.getUser(mAuth, db);
        userViewModel.UserMutableLiveData.observe(this, userAccount -> {

            user_name.setText(userAccount.getName());
            user_email.setText(userAccount.getEmail());

            try {
                Glide.with(ProfileActivity.this).load(userAccount.getImg_profile()).placeholder(R.drawable.ic_chat).
                        error(R.drawable.ic_chat).into(image_user);
            } catch (Exception e) {
                image_user.setImageResource(R.drawable.ic_chat);
            }
        });

         */




        imageView = findViewById(R.id.edit_profile);
        imageView.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        back_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void getInformationUser(FirebaseAuth mAuth, FirebaseFirestore db) {
        db.collection("users").document(mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    userAccount = task.getResult().toObject(UserAccount.class);
                    user_home.setText(userAccount.getUserInformation().getAddress_home());
                    user_location.setText(userAccount.getUserInformation().getState_address());
                    user_school.setText(userAccount.getUserInformation().getName_education());
                    user_phone.setText(userAccount.getUserInformation().getPhone());
                }
            }
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