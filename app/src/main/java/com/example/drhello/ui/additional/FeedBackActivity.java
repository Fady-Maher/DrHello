package com.example.drhello.ui.additional;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.drhello.R;
import com.example.drhello.other.ShowDialogPython;
import com.example.drhello.databinding.ActivityFeedBackBinding;
import com.example.drhello.firebaseinterface.MyCallbackUser;
import com.example.drhello.model.UserAccount;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hsalf.smileyrating.SmileyRating;

public class FeedBackActivity extends AppCompatActivity {
    ActivityFeedBackBinding activityFeedBackBinding;
    ShowDialogPython showDialogPython;
    private UserAccount userAccount;
    private SmileyRating smileyRating;
    private String tpyesmile="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            getWindow().setStatusBarColor(Color.WHITE);
        }
        activityFeedBackBinding = DataBindingUtil.setContentView(FeedBackActivity.this, R.layout.activity_feed_back);
        activityFeedBackBinding.imgBackPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        activityFeedBackBinding.smileRating.setSmileySelectedListener(new SmileyRating.OnSmileySelectedListener() {
            @Override
            public void onSmileySelected(SmileyRating.Type type) {
                Log.e("onSmileySelected:  ",type+"");
                if (SmileyRating.Type.TERRIBLE == type) {
                    tpyesmile = "TERRIBLE";
                } else if (SmileyRating.Type.GOOD == type) {
                    tpyesmile = "GOOD";
                } else if (SmileyRating.Type.OKAY == type) {
                    tpyesmile = "OKAY";
                } else if (SmileyRating.Type.BAD == type) {
                    tpyesmile = "BAD";
                } else if (SmileyRating.Type.GREAT == type) {
                    tpyesmile = "GREAT";
                }
                // You can get the user rating too
                // rating will between 1 to 5
                int rating = type.getRating();

            }
        });

        activityFeedBackBinding.imgPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tpyesmile.equals("")) {
                    Toast.makeText(FeedBackActivity.this, "Please , Select Rate", Toast.LENGTH_SHORT).show();
                } else {
                    String message = "User Id: " + userAccount.getId() + "\n \n" + "User Name: " + userAccount.getName() + "\n \n" + "feedback rating = " + tpyesmile + "\n\n\n" + "user feedback : " +
                            "\n" + activityFeedBackBinding.editText.getText().toString();
                    Intent email = new Intent(Intent.ACTION_SEND);
                    email.putExtra(Intent.EXTRA_EMAIL, new String[]{ "careeasy6@gmail.com"});
                    email.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
                    email.putExtra(Intent.EXTRA_TEXT, message);
                    email.setType("message/rfc822");
                    startActivity(Intent.createChooser(email, "Choose an Email client :"));
                }
            }
        });


        readData(new MyCallbackUser() {
            @Override
            public void onCallback(DocumentSnapshot documentSnapshot) {
                if (!documentSnapshot.exists()) {
                    FirebaseAuth.getInstance().getCurrentUser().delete();
                } else {
                    userAccount = documentSnapshot.toObject(UserAccount.class);
                }
                showDialogPython.dismissDialog();
            }
        });

    }

    public void readData(MyCallbackUser myCallback) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            showDialogPython = new ShowDialogPython(FeedBackActivity.this, FeedBackActivity.this.getLayoutInflater(), "load");

            FirebaseFirestore.getInstance().collection("users")
                    .document(currentUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    myCallback.onCallback(documentSnapshot);
                }
            });
        }
    }

}