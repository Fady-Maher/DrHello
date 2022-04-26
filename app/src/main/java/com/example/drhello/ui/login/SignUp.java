package com.example.drhello.ui.login;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;
import com.example.drhello.R;
import com.example.drhello.connectionnewtwork.CheckNetwork;
import com.example.drhello.databinding.ActivitySignUpBinding;
import com.example.drhello.signup.GMailSender;
import com.example.drhello.signup.PasswordStrength;
import com.example.drhello.signup.SignUpMethods;
import com.example.drhello.model.UserAccount;
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
import com.google.firebase.messaging.FirebaseMessaging;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;


import static android.content.ContentValues.TAG;


public class SignUp extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    @SuppressLint("StaticFieldLeak")
    public static ActivitySignUpBinding signUpBinding;
    private ProgressDialog mProgress;
    private CallbackManager callbackManager;
    private SignUpMethods signUpMethods;
    private final static int RC_SIGN_IN = 123;
    private GoogleSignInClient mGoogleSignInClient;
    private String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            getWindow().setStatusBarColor(Color.BLUE);
        }
        //createRequest for google sign in
        createRequestGoogle();
        mProgress = new ProgressDialog(this);

        //to connect layout with java code
        signUpBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);

        signUpBinding.shimmerLayoutUp.startShimmerAnimation();
        signUpBinding.swipeBtnUp.setOnStateChangeListener(active -> {
            Intent intent = new Intent(SignUp.this, SignIn.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        signUpBinding.btnSignup.setOnClickListener(this);
        signUpBinding.btnFacebookSignup.setOnClickListener(this);
        signUpBinding.btnGoogleSignup.setOnClickListener(this);

        Objects.requireNonNull(signUpBinding.editPassSignup.getEditText()).addTextChangedListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_signup:
                if(CheckNetwork.getConnectivityStatusString(SignUp.this) == 1) {
                    mProgress.setMessage("Signing Up");
                    mProgress.show();
                    createUser();
                }else{
                    Toast.makeText(SignUp.this, "Please, Check Internet", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_facebook_signup:
                if(CheckNetwork.getConnectivityStatusString(SignUp.this) == 1) {
                    signUpBinding.btnFacebookSignup.startAnimation();
                    createRequestFaceBook();
                }else{
                    Toast.makeText(SignUp.this, "Please, Check Internet", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_google_signup:
                if(CheckNetwork.getConnectivityStatusString(SignUp.this) == 1) {
                    signUpBinding.btnGoogleSignup.startAnimation();
                    signInWithGoogle();
                }else{
                    Toast.makeText(SignUp.this, "Please, Check Internet", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void createRequestGoogle() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void createUser() {

        String name = Objects.requireNonNull(signUpBinding.editUsernameSignup.getEditText()).getText().toString().trim();
        String email = Objects.requireNonNull(signUpBinding.editEmailSignup.getEditText()).getText().toString().trim();
        String pass = Objects.requireNonNull(signUpBinding.editPassSignup.getEditText()).getText().toString().trim();
        String confirmPass = Objects.requireNonNull(signUpBinding.editConfirmpassSignup.getEditText()).getText().toString().trim();
        String message = "Password should contain min of 10 characters and at least 1 lowercase, 1 uppercase , 1 special characters  and 1 numeric value";
        if (name.isEmpty()) {
            signUpBinding.editUsernameSignup.getEditText().setError("Name is needed");
            signUpBinding.editUsernameSignup.getEditText().requestFocus();
            mProgress.dismiss();
            return;
        }
        if (email.isEmpty()) {
            signUpBinding.editEmailSignup.getEditText().setError("Email is needed");
            signUpBinding.editEmailSignup.getEditText().requestFocus();
            mProgress.dismiss();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() && !email.matches("[0-9]+")) {
            signUpBinding.editEmailSignup.getEditText().setError("Please enter a valid email.");
            signUpBinding.editEmailSignup.getEditText().requestFocus();
            mProgress.dismiss();
            return;
        }

        if (pass.isEmpty()) {
            signUpBinding.editPassSignup.getEditText().setError("password is needed");
            signUpBinding.editPassSignup.getEditText().requestFocus();
            signUpBinding.editPassSignup.setPasswordVisibilityToggleEnabled(false);
            mProgress.dismiss();
            return;
        }
        if (!pass.equals(confirmPass)) {
            signUpBinding.editPassSignup.getEditText().setError("password and confirm should match");
            signUpBinding.editPassSignup.getEditText().requestFocus();
            signUpBinding.editPassSignup.setPasswordVisibilityToggleEnabled(false);
            mProgress.dismiss();
            return;
        }
        if (!(PasswordStrength.calculateStrength(pass).getValue() > PasswordStrength.STRONG.getValue())) {
            signUpBinding.editPassSignup.getEditText().setError("weak password");
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            signUpBinding.editPassSignup.getEditText().requestFocus();
            signUpBinding.editPassSignup.setPasswordVisibilityToggleEnabled(false);
            mProgress.dismiss();
            return;
        }

        createNewUser(name, email, pass);

    }

    private void createNewUser(String name, String email, String pass) {

        //check if email
        if (email.matches(".*[a-zA-Z]+.*") && isEmailValid(email)) {
            Log.e("email : ", email);
            FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
                @Override
                public void onSuccess(String s) {
                    token = s;
                    UserAccount userAccount = new UserAccount("img_profile", name, email, pass, token,
                            "email and pass");
                    sendEmail(userAccount);
                }
            });

        } else {
            Toast.makeText(getApplicationContext(), "invalid email , please try again!!", Toast.LENGTH_SHORT).show();
            mProgress.dismiss();
        }

    }


    private void sendEmail(UserAccount userAccount) {
        //Getting content for email
        //  String email =  userAccount.getEmail();
        Random random = new Random();
        @SuppressLint("DefaultLocale") String verify_num = String.format("%06d", random.nextInt(1000000));
        String englishNumerals = new BigDecimal(verify_num).toString();

        // String email = "jojo09477@gmail.com";
        String email = userAccount.getEmail();

        String subject = "Easy Care Verification Code";
        String message = "E- " + englishNumerals + "  is your Easy Care Verification Code\n" +
                "Enter this code to activate this account " + email + ".\n" +
                "\n" +
                "If you don't recognize " + email + ", it is possible that someone has given your mail address by mistake. You can safely ignore this email.\n" +
                "\n" +
                "Yours sincerely,\n" +
                "The Easy Care Team.";
        mProgress.dismiss();
        //Creating SendMail object
        GMailSender sm = new GMailSender(this, email, subject, message, userAccount, englishNumerals, true);
        //Executing sendmail to send email
        sm.execute();

    }




    private boolean isEmailValid(String email) {
        if (email.length() > 10 && email.contains("@") && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            //String local_part = email.split("@")[1];  // user
            String domain_part = email.split("@")[1]; // @domain.com
            String domain = "@" + domain_part.substring(0, domain_part.length() - 4); // @domain
            String lastfourchars = email.substring(email.length() - 4); // .com
            if (lastfourchars.equals(".com")) {
                Log.e("email domain : ", domain);
                return domain.equals("@gmail") || domain.equals("@yahoo")
                        || domain.equals("@hotmail") || domain.equals("@outlook");
            }
        }
        return false;
    }


    public void createRequestFaceBook() {
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        Log.e("Facebook : ", "onSuccess");
                        signUpMethods = new SignUpMethods(SignUp.this, 2);
                        signUpMethods.handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        signUpBinding.btnFacebookSignup.revertAnimation();
                        // App code
                        Log.e("Facebook : ", "onCancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        signUpBinding.btnFacebookSignup.revertAnimation();
                        // App code
                        Log.e("Facebook : onError() ", exception.toString());
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SignUp.this, SignIn.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                signUpMethods = new SignUpMethods(SignUp.this, 2);
                signUpMethods.firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                signUpBinding.btnGoogleSignup.revertAnimation();
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        signUpBinding.editPassSignup.setPasswordVisibilityToggleEnabled(true);

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

}