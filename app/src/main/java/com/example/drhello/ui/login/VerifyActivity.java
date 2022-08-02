package com.example.drhello.ui.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.example.drhello.other.ShowDialogPython;
import com.example.drhello.ui.main.MainActivity;
import com.example.drhello.R;
import com.example.drhello.model.UserInformation;
import com.example.drhello.databinding.ActivityVerifyBinding;
import com.example.drhello.signup.GMailSender;
import com.example.drhello.signup.SignUpMethods;
import com.example.drhello.model.UserAccount;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class VerifyActivity extends AppCompatActivity {

    UserAccount userAccount, userAccountme;
    UserInformation userInformation;
    String verify_num, method;
    ActivityVerifyBinding verifyBinding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private final String PHONE = "PHONE";
    ShowDialogPython showDialogPython;
    String phone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            getWindow().setStatusBarColor(Color.WHITE);
        }

        verifyBinding = DataBindingUtil.setContentView(this, R.layout.activity_verify);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        verifyBinding.backVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        timer();

        //retrive data from intent userAccount
        if (getIntent().getSerializableExtra("userAccount") != null && getIntent().getStringExtra("verify_num") != null) {
            userAccount = (UserAccount) getIntent().getSerializableExtra("userAccount");
            verify_num = getIntent().getStringExtra("verify_num");
            method = getIntent().getStringExtra("method");
        } else {
            userInformation = (UserInformation) getIntent().getSerializableExtra("userInformation");
            verify_num = getIntent().getStringExtra("verify_num");
            method = getIntent().getStringExtra("method");
            phone = getIntent().getStringExtra("phone");
            Log.e("getIntentPHONE : ", phone);
        }

        if (getIntent().getSerializableExtra("userAccountme") != null) {
            userAccountme = (UserAccount) getIntent().getSerializableExtra("userAccountme");
        }

        verifyBinding.verifyNum.setOnAllFilledListener(text -> {
            if (method.equals(PHONE)) {
                Log.e("verify_num - >", "PHONE : " + verify_num);
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verify_num, text);
                if (text.equals(credential.getSmsCode())) {
                    signInWithPhoneAuthCredential(credential);
                } else {
                    Toast.makeText(getApplicationContext(), "wrong verify code ", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("verify_num - >", "EMAIL : " + verify_num);
                if (text.equals(verify_num)) {
                    SignUpMethods signUpMethods = new SignUpMethods(VerifyActivity.this, userAccount, 2);
                    signUpMethods.createAccountEmailAndPass(userAccount.getEmail(),
                            userAccount.getPass());
                } else {
                    Toast.makeText(getApplicationContext(), "wrong verify code ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        verifyBinding.txtVerifyNum.setOnClickListener(view -> {
            timer();
            if (method.equals(PHONE)) {
                Log.e("methodPHONE : ", phone);
                sendVerificationCode(phone);
            } else {
                Log.e("method:", " resendEmail");
                resendEmail(userAccount);
            }
        });

    }

    private void timer() {
        new CountDownTimer(80000, 1000) {
            public void onTick(long millisUntilFinished) {
                verifyBinding.txtVerifyNum.setEnabled(false);
                verifyBinding.txtVerifyNum.setText("Please wait " + millisUntilFinished / 1000 + " seconds to resend Code");
            }

            public void onFinish() {
                verifyBinding.txtVerifyNum.setEnabled(true);
                verifyBinding.txtVerifyNum.setText("resend Code");
            }
        }.start();
    }

    private void resendEmail(UserAccount userAccount) {
        //Getting content for email
        //  String email =  userAccount.getEmail();
        Random random = new Random();
        @SuppressLint("DefaultLocale") String verify_num_new = String.format("%06d", random.nextInt(10000));
        String englishNumerals = new BigDecimal(verify_num_new).toString();
        verify_num = englishNumerals;

        String email = userAccount.getEmail();
        String subject = "Easy Care Verification Code";
        String message = "E- " + englishNumerals + "  is your Easy Care Verification Code\n" +
                "Enter this code to activate this account jojo09477@gmail.com.\n" +
                "\n" +
                "If you don't recognize jojo09477@gmail.com, it is possible that someone has given your mail address by mistake. You can safely ignore this email.\n" +
                "\n" +
                "Yours sincerely,\n" +
                "The Easy Care Team.";

        //Creating SendMail object
        GMailSender sm = new GMailSender(this, email, subject, message, userAccount, englishNumerals, false);
        //Executing sendmail to send email
        sm.execute();

    }


    ////////////////////////////////////////////////////////
    //sign up with phone and password
    public void sendVerificationCode(String phone) {
        showDialogPython = new ShowDialogPython(VerifyActivity.this, VerifyActivity.this.getLayoutInflater(), "load");
        mAuth = FirebaseAuth.getInstance();
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phone)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(VerifyActivity.this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();
            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            Log.e("VerCompleted:", phoneAuthCredential + "");
            showDialogPython.dismissDialog();
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Log.e("onVerifFailed:", "onVerifFailed" + e.getMessage());

            /*
              The error "We have blocked all requests from this device due to unusual activity. Try again later."
              is usually thrown when a user is making SMS authentication requests to a certain number of times using
              the same phone number or IP address. These repeated requests are considered as a suspicious behavior
              which temporarily blocks the device or IP address.Additionally,
              there's a limit of 5 SMS per phone number per 4 hours.
              With this, you may try doing the following to resolve the issue:
            */

            Toast.makeText(getApplicationContext(), "An error occurred during authentication, Try again later (5 Hour) And if this is repeated, Please contact our support team.", Toast.LENGTH_SHORT).show();
            showDialogPython.dismissDialog();
        }

        @Override
        public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(verificationId, forceResendingToken);
            Log.e("onCodeSent:", verificationId + "");
            showDialogPython.dismissDialog();
            verify_num = verificationId;
        }
    };


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        String phone = "+" + "20" + userInformation.getPhone();
        Objects.requireNonNull(mAuth.getCurrentUser()).linkWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.e("linkWithCredential", "success");
                        FirebaseUser user = task.getResult().getUser();
                        //to add phone with useraccount information untill make sign in with phone or email
                        assert user != null;
                        updataInformation(userInformation);
                    } else {
                     /*   String errorCode = ((FirebaseAuthException) Objects.requireNonNull(task.getException())).getErrorCode();
                        if(errorCode.equals("FirebaseAuthInvalidCredentialsException")){
                            Toast.makeText(VerifyActivity.this, "Please resend the verification code sms and be sure use the verification code provided by the user.", Toast.LENGTH_LONG).show();
                        }

                      */
                        Toast.makeText(VerifyActivity.this, "Please resend the verification code sms and be sure use the verification code provided by the user.", Toast.LENGTH_LONG).show();

                        Log.e("linkWithCredential", "failure : " + task.getException());
                        Toast.makeText(VerifyActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void updataInformation(UserInformation userInformation) {
        //users//id//userinformation//id --> to seperate data
        if (!userInformation.getType().equals("normal user")) {
            SharedPreferences.Editor editor = getSharedPreferences("com.example.drhello", MODE_PRIVATE).edit();
            editor.putString("doctor", "false");
            editor.apply();
            userInformation.setType("normal user");
        }
        userAccountme.setUserInformation(userInformation);
        db.collection("users").document(mAuth.getCurrentUser().getUid())
                .set(userAccountme)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.e("updata pass : ", "finish");
                        Toast.makeText(VerifyActivity.this, "Successful add information ", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(VerifyActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("userInformation", getIntent().getSerializableExtra("userInformation"));
                        intent.putExtra("method", PHONE);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "failed to update user info.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}