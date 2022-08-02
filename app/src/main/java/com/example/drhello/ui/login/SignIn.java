package com.example.drhello.ui.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.example.drhello.other.ShowDialogPython;
import com.example.drhello.connectionnewtwork.CheckNetwork;
import com.example.drhello.databinding.ActivitySignInBinding;
import com.example.drhello.firebaseinterface.MyCallbackSignIn;
import com.example.drhello.model.UserAccount;
import com.example.drhello.ui.main.MainActivity;
import com.example.drhello.R;
import com.example.drhello.signup.SignUpMethods;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;

public class SignIn extends AppCompatActivity implements View.OnClickListener {
    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 123;
    private SignUpMethods signUpMethods;
    private CallbackManager callbackManager;
    ShowDialogPython showDialogPython;

    private FirebaseFirestore db;

    Boolean flag_check_firebase = false;
    //reverse activity name then binding
   @SuppressLint("StaticFieldLeak")
   public static ActivitySignInBinding signInBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            getWindow().setStatusBarColor(Color.WHITE);
        }


        //to connect layout with java code
        signInBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in);
        db = FirebaseFirestore.getInstance();


        //createRequest for google sign in
        createRequestGoogle();

        // all views id will be in signInBinding
        signInBinding.shimmerLayout.startShimmerAnimation();


        signInBinding.btnSignin.setOnClickListener(this);
        signInBinding.btnGoogleSignin.setOnClickListener(this);
        signInBinding.btnFacebookSignin.setOnClickListener(this);
        signInBinding.txtForgot.setOnClickListener(this);
        signInBinding.swipeBtn.setOnStateChangeListener(active -> {
            Intent intent = new Intent(SignIn.this, SignUp.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_signin:
                if(CheckNetwork.getConnectivityStatusString(SignIn.this) == 1) {
                    String email_phone = Objects.requireNonNull(Objects.requireNonNull(signInBinding.editEmailSignin).getEditText()).getText().toString().trim();
                    String password = Objects.requireNonNull(Objects.requireNonNull(signInBinding.editPassSignin).getEditText()).getText().toString().trim();
                    if (!email_phone.equals("") && !password.equals("")) {
                        if(email_phone.matches("[0-9]+")){
                            if(isValidPhoneNumber(email_phone)){
                                db.collection("users").whereEqualTo("phone",email_phone).
                                        get().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        int count = task.getResult().size();
                                        if(count == 0){
                                            Log.e("signin :  ", "This mobile number does not have an account");
                                            Toast.makeText(SignIn.this, "This mobile number does not have an account, and please enter the correct number!!", Toast.LENGTH_SHORT).show();
                                        }else{
                                            for (QueryDocumentSnapshot document : task.getResult()) {

                                                Log.e("signin :  ", "task.isSuccessful");
                                                flag_check_firebase = true;
                                                UserAccount userAccount = document.toObject(UserAccount.class);
                                                signUpMethods = new SignUpMethods(SignIn.this, 1);
                                                signUpMethods.signInEmailAndPass(userAccount.getEmail(), password,userAccount);
                                            }
                                        }
                                    }
                                });


                                Log.e("signin :  ", "with phone and pass");
                            }else{
                                Toast.makeText(SignIn.this, "invalid phone , please try again!!", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Log.e("signin :  ", "with email and pass");
                            signUpMethods = new SignUpMethods(SignIn.this,1);
                            signUpMethods.signInEmailAndPass(email_phone, password,new UserAccount());
                        }
                    }
                }else{
                    Toast.makeText(SignIn.this, "Please, Check Internet", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.txt_forgot:
                if(CheckNetwork.getConnectivityStatusString(SignIn.this) == 1) {
                    Intent intent = new Intent(SignIn.this, ForgotPassword.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(SignIn.this, "Please, Check Internet", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_facebook_signin:
                if(CheckNetwork.getConnectivityStatusString(SignIn.this) == 1) {
                    signInBinding.btnFacebookSignin.startAnimation();
                    createRequestFaceBook();
                }else{
                    Toast.makeText(SignIn.this, "Please, Check Internet", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_google_signin:
                if(CheckNetwork.getConnectivityStatusString(SignIn.this) == 1) {
                    signInBinding.btnGoogleSignin.startAnimation();
                    signInWithGoogle();
                }else{
                    Toast.makeText(SignIn.this, "Please, Check Internet", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    private boolean isValidPhoneNumber(String phone) {
        if (!phone.trim().equals("") && (phone.length() > 6 && phone.length() <= 13)
                && !Pattern.matches("[a-zA-Z]+", phone) && phone.matches("[0-9]+")) {
            return android.util.Patterns.PHONE.matcher(phone).matches();
        }
        return false;
    }

    public void createRequestFaceBook() {
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile", "user_friends"));
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        Log.e("Facebook : ", "onSuccess");
                        signUpMethods = new SignUpMethods(SignIn.this,1);
                        signUpMethods.handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        signInBinding.btnFacebookSignin.revertAnimation();
                        // App code
                        Log.e("Facebook : ", "onCancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        signInBinding.btnFacebookSignin.revertAnimation();
                        // App code
                        Log.e("Facebook : onError() ", exception.toString());
                    }
                });
    }






    private void createRequestGoogle() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void  signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.e(" Google", "firebaseAuthWithGoogle:" + account.getId());
                signUpMethods = new SignUpMethods(SignIn.this,1);
                signUpMethods.firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                signInBinding.btnGoogleSignin.revertAnimation();
                // Google Sign In failed, update UI appropriately
                Log.e("Google sign in failed", e.getMessage());
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

        readData(new MyCallbackSignIn() {
            @Override
            public void onCallback(DocumentSnapshot documentSnapshot) {
                if(!documentSnapshot.exists()){
                    FirebaseAuth.getInstance().getCurrentUser().delete();
                }else{
                    if(documentSnapshot.toObject(UserAccount.class).getUserInformation() != null)
                        signIn();
                }
                showDialogPython.dismissDialog();
            }
        });
    }

    public void readData(MyCallbackSignIn myCallback) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            showDialogPython = new ShowDialogPython(SignIn.this,SignIn.this.getLayoutInflater(),"load");
            FirebaseFirestore.getInstance().collection("users")
                    .document(currentUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    myCallback.onCallback(documentSnapshot);
                }
            });
        }
    }


    private void signIn() {
        Intent intent = new Intent(SignIn.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


}