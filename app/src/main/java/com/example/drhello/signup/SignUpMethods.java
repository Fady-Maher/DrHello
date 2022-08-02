package com.example.drhello.signup;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.drhello.ui.login.CompleteInfoActivity;
import com.example.drhello.R;
import com.example.drhello.model.UserAccount;
import com.example.drhello.model.UserInformation;
import com.example.drhello.ui.login.SignIn;
import com.example.drhello.ui.login.SignUp;
import com.example.drhello.ui.login.VerifyActivity;
import com.example.drhello.ui.main.MainActivity;
import com.facebook.AccessToken;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SignUpMethods {

    private final Context context;
    private UserAccount userAccount, userAccountme;
    private UserInformation userInformation;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private final FirebaseFirestore db;
    private final ProgressDialog mProgress;
    String phone;

    //for validation email or phone
    private final String PHONE;

    {
        PHONE = "PHONE";
    }

    //phone
    private String mVerificationId;

    {
        mVerificationId = "";
    }

    Drawable d;
    int x;

    public SignUpMethods(Context context, UserInformation userInformation) {
        this.context = context;
        this.userInformation = userInformation;
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mProgress = new ProgressDialog(context);
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    public SignUpMethods(UserAccount userAccount, Context context, UserInformation userInformation) {
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mProgress = new ProgressDialog(context);
        d = context.getResources().getDrawable(R.drawable.ic_baseline_done_24);
        this.userInformation = userInformation;
        this.userAccountme = userAccount;

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public SignUpMethods(Context context, int x) {
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mProgress = new ProgressDialog(context);
        d = context.getResources().getDrawable(R.drawable.ic_baseline_done_24);
        this.x = x;

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public SignUpMethods(Context context, UserAccount userAccount, int x) {
        this.context = context;
        this.userAccount = userAccount;
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mProgress = new ProgressDialog(context);
        d = context.getResources().getDrawable(R.drawable.ic_baseline_done_24);
        this.x = x;
    }


    public void createAccountEmailAndPass(String email, String pass) {

        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                user = mAuth.getCurrentUser();
                assert user != null;
                userAccount.setId(user.getUid());
                userAccount.setDate(getDateTime());
                //Add information of user to firebasefirestore
                addInformation(user, userAccount);
                Toast.makeText(context, "Welcome To Easy Care App", Toast.LENGTH_SHORT).show();
            } else {
                String errorCode = ((FirebaseAuthException) Objects.requireNonNull(task.getException())).getErrorCode();
                switch (errorCode) {
                    case "ERROR_INVALID_CUSTOM_TOKEN":
                        Toast.makeText(context, "The custom token format is incorrect. Please check the documentation.", Toast.LENGTH_LONG).show();
                        break;

                    case "ERROR_CUSTOM_TOKEN_MISMATCH":
                        Toast.makeText(context, "The custom token corresponds to a different audience.", Toast.LENGTH_LONG).show();
                        break;

                    case "ERROR_INVALID_CREDENTIAL":
                        Toast.makeText(context, "The supplied auth credential is malformed or has expired.", Toast.LENGTH_LONG).show();
                        break;

                    case "ERROR_INVALID_EMAIL":
                        Toast.makeText(context, "The email address is badly formatted.", Toast.LENGTH_LONG).show();
                        break;

                    case "ERROR_WRONG_PASSWORD":
                        Toast.makeText(context, "The password is invalid or the user does not have a password.", Toast.LENGTH_LONG).show();
                        break;

                    case "ERROR_USER_MISMATCH":
                        Toast.makeText(context, "The supplied credentials do not correspond to the previously signed in user.", Toast.LENGTH_LONG).show();
                        break;

                    case "ERROR_REQUIRES_RECENT_LOGIN":
                        Toast.makeText(context, "This operation is sensitive and requires recent authentication. Log in again before retrying this request.", Toast.LENGTH_LONG).show();
                        break;

                    case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                        Toast.makeText(context, "An account already exists with the same email address but different sign-in credentials. Sign in using a provider associated with this email address.", Toast.LENGTH_LONG).show();
                        break;

                    case "ERROR_EMAIL_ALREADY_IN_USE":
                        Log.e("signIn:failure", task.getException().getMessage());
                        Toast.makeText(context, "An account already exists with the same email address but different sign-in Methods. ", Toast.LENGTH_SHORT).show();
                        break;

                    case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                        Toast.makeText(context, "This credential is already associated with a different user account.", Toast.LENGTH_LONG).show();
                        break;

                    case "ERROR_USER_DISABLED":
                        Toast.makeText(context, "The user account has been disabled by an administrator.", Toast.LENGTH_LONG).show();
                        break;

                    case "ERROR_USER_TOKEN_EXPIRED":
                        Toast.makeText(context, "The user\\'s credential is no longer valid. The user must sign in again.", Toast.LENGTH_LONG).show();
                        break;

                    case "ERROR_USER_NOT_FOUND":
                        Toast.makeText(context, "There is no user record corresponding to this identifier. The user may have been deleted.", Toast.LENGTH_LONG).show();
                        break;

                    case "ERROR_INVALID_USER_TOKEN":
                        Toast.makeText(context, "The user\\'s credential is no longer valid. The user must sign in again.", Toast.LENGTH_LONG).show();
                        break;

                    case "ERROR_OPERATION_NOT_ALLOWED":
                        Toast.makeText(context, "This operation is not allowed. You must enable this service in the console.", Toast.LENGTH_LONG).show();
                        break;

                    case "ERROR_WEAK_PASSWORD":
                        Toast.makeText(context, "The given password is invalid.", Toast.LENGTH_LONG).show();
                        // etPassword.setError("The password is invalid it must 6 characters at least");
                        // etPassword.requestFocus();
                        break;
                    default:
                        Log.e("signIn:failure", task.getException().getMessage());
                        Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }


    private void addInformation(FirebaseUser firebaseUser, UserAccount userAccount) {
        Log.e("task : ", " isSuccessful");

        db.collection("users").document(mAuth.getCurrentUser().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            UserAccount userAccount1 = documentSnapshot.toObject(UserAccount.class);
                            if (userAccount1 != null) {
                                userAccount.setMap(userAccount1.getMap());
                                userAccount.setFriendsmap(userAccount1.getFriendsmap());
                                userAccount.setRequests(userAccount1.getRequests());
                                userAccount.setRequestSsent(userAccount1.getRequestSsent());
                                if (userAccount1.getUserInformation() == null) {
                                    Intent intent = new Intent(context, CompleteInfoActivity.class);
                                    intent.putExtra("userAccount", userAccount);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    context.startActivity(intent);

                                } else {
                                    signIn();
                                }
                            }
                        } else {
                            Intent intent = new Intent(context, CompleteInfoActivity.class);
                            intent.putExtra("userAccount", userAccount);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(intent);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("task : ", " onFailure");
                        Intent intent = new Intent(context, CompleteInfoActivity.class);
                        intent.putExtra("userAccount", userAccount);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);
                    }
                });





        /*
        db.collection("users").whereEqualTo("id",mAuth.getUid())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    Log.e("task : ", " isSuccessful");
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        UserAccount userAccount1 = document.toObject(UserAccount.class);
                        userAccount.setMap(userAccount1.getMap());
                        userAccount.setFriendsmap(userAccount1.getFriendsmap());
                        userAccount.setRequests(userAccount1.getRequests());
                        userAccount.setRequestSsent(userAccount1.getRequestSsent());
                        if(userAccount.getUserInformation() == null){
                            Intent intent = new Intent(context, CompleteInfoActivity.class);
                            intent.putExtra("userAccount",userAccount);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(intent);

                        }else{
                            signIn();
                        }
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("task : ", " onFailure");

                Intent intent = new Intent(context, CompleteInfoActivity.class);
                    intent.putExtra("userAccount",userAccount);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);

            }
        });

         */


    }


    public void signInEmailAndPass(String email, String pass, UserAccount userAccount) {
        mProgress.setMessage("Signing In");
        mProgress.show();
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mProgress.dismiss();
                Toast.makeText(context, "Successful Sign In ", Toast.LENGTH_SHORT).show();
                user = mAuth.getCurrentUser();
                Log.e("user : ", user.getUid());

                userAccount.setId(user.getUid());
                userAccount.setDate(getDateTime());
                addInformation(user, userAccount);
            } else {
                mProgress.dismiss();
                Log.e("except : ", Objects.requireNonNull(task.getException()).toString());
                Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //for google sign in with firebase
    public void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) context, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.e(" Google", "signInWithCredential:success:");
                        user = mAuth.getCurrentUser();
                        assert user != null;
                        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                userAccount = new UserAccount(Objects.requireNonNull(user.getPhotoUrl()).toString(),
                                        user.getDisplayName()
                                        , user.getEmail(), "pass", getDateTime(), user.getUid(), s, "google");
                                userAccount.setId(user.getUid());
                                userAccount.setDate(getDateTime());
                                //Add information of user to firebasefirestore
                                addInformation(user, userAccount);
                            }
                        });

                        if (x == 1)
                            SignIn.signInBinding.btnGoogleSignin.doneLoadingAnimation(Color.parseColor("#0F2DCA"), drawableToBitmap(d));
                        else
                            SignUp.signUpBinding.btnGoogleSignup.doneLoadingAnimation(Color.parseColor("#0F2DCA"), drawableToBitmap(d));
                    } else {
                        if (x == 1)
                            SignIn.signInBinding.btnGoogleSignin.revertAnimation();
                        else
                            SignUp.signUpBinding.btnGoogleSignup.revertAnimation();
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                    }
                });
    }


    public void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) context, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.e("signInWithCredential:", "success");
                        user = mAuth.getCurrentUser();
                        assert user != null;
                        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                userAccount = new UserAccount(Objects.requireNonNull(user.getPhotoUrl()).toString(), user.getDisplayName()
                                        , user.getEmail(), "pass", getDateTime(), user.getUid(), s, "facebook");
                                userAccount.setId(user.getUid());
                                userAccount.setDate(getDateTime());
                                //Add information of user to firebasefirestore
                                addInformation(user, userAccount);
                            }
                        });

                        if (x == 1)
                            SignIn.signInBinding.btnFacebookSignin.doneLoadingAnimation(Color.parseColor("#F44336"), drawableToBitmap(d));
                        else
                            SignUp.signUpBinding.btnFacebookSignup.doneLoadingAnimation(Color.parseColor("#F44336"), drawableToBitmap(d));
                    } else if (!task.isSuccessful() &&
                            task.getException() instanceof FirebaseAuthUserCollisionException) {
                        if (x == 1)
                            SignIn.signInBinding.btnFacebookSignin.revertAnimation();
                        else
                            SignUp.signUpBinding.btnFacebookSignup.revertAnimation();
                        FirebaseAuthUserCollisionException exception =
                                (FirebaseAuthUserCollisionException) task.getException();
                        if (exception.getErrorCode().equals("ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL")) {

                            // Lookup existing account’s provider ID.
                            Toast.makeText(context, "An account already exists with the same email address but different sign-in Methods. ", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (x == 1)
                            SignIn.signInBinding.btnFacebookSignin.revertAnimation();
                        else
                            SignUp.signUpBinding.btnFacebookSignup.revertAnimation();
                        Log.e("signInfacebook:failure", Objects.requireNonNull(task.getException()).getMessage());
                        Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void signIn() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }


    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss:SS a", Locale.US);
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }


    //sign up with phone and password
    public void sendVerificationCode(String phone) {
        mProgress.setTitle("Sending Verification Code");
        mProgress.setMessage("Please wait...");
        mProgress.show();
        mAuth = FirebaseAuth.getInstance();
        this.phone = phone;
        Log.e("sendVerification : ", phone);
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phone)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity((Activity) context)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            //Getting the code sent by SMS

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            Log.e("VerCompleted:", phoneAuthCredential + "");
            mProgress.dismiss();
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

            Toast.makeText(context, "An error occurred during authentication, Try again later (5 Hour) And if this is repeated, Please contact our support team.", Toast.LENGTH_SHORT).show();
            mProgress.dismiss();
        }

        @Override
        public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(verificationId, forceResendingToken);

            mProgress.dismiss();
            mVerificationId = verificationId;
            if (userInformation.getPhone() != null) {
                Intent intent = new Intent(context, VerifyActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("userInformation", userInformation);
                intent.putExtra("verify_num", mVerificationId);
                intent.putExtra("method", PHONE);
                intent.putExtra("userAccountme", userAccountme);
                intent.putExtra("phone", phone);
                context.startActivity(intent);
            }
        }
    };
}
