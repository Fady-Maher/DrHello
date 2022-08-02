package com.example.drhello.ui.login;

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
import com.example.drhello.databinding.ActivityNewPasswordBinding;
import com.example.drhello.signup.PasswordStrength;
import com.example.drhello.model.UserAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.Objects;

public class NewPassword extends AppCompatActivity {

    ActivityNewPasswordBinding newPasswordBinding;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String email;
    private UserAccount userAccount;
    ShowDialogPython showDialogPython;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) ;
        }else{
            getWindow().setStatusBarColor(Color.WHITE);
        }

        newPasswordBinding = DataBindingUtil.setContentView(this, R.layout.activity_new_password);


        if(getIntent().getStringExtra("email") != null){
            email  = getIntent().getStringExtra("email");
        }

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        db.collection("users").whereEqualTo("email",email)
                .get().addOnCompleteListener(task -> {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        userAccount = document.toObject(UserAccount.class);

                    }
                });


        newPasswordBinding.btnUpdatePasswordSign.setOnClickListener(view -> {
            showDialogPython = new ShowDialogPython(NewPassword.this,NewPassword.this.getLayoutInflater(),"load");

            checkPasswordStrength();
        });
    }

    private void checkPasswordStrength() {
        String pass = Objects.requireNonNull(newPasswordBinding.editPassNewpassword.getEditText()).getText().toString().trim();
        String confirmPass = Objects.requireNonNull(newPasswordBinding.editConfirmpassNewpassword.getEditText()).getText().toString().trim();
        String message = "Password should contain min of 10 characters and at least 1 lowercase, 1 uppercase , 1 special characters  and 1 numeric value";

        if (pass.isEmpty()) {
            newPasswordBinding.editPassNewpassword.getEditText().setError("password is needed");
            newPasswordBinding.editPassNewpassword.getEditText().requestFocus();
            newPasswordBinding.editPassNewpassword.setPasswordVisibilityToggleEnabled(false);
            showDialogPython.dismissDialog();
            return;
        }
        if (!pass.equals(confirmPass)) {
            newPasswordBinding.editConfirmpassNewpassword.getEditText().setError("password and confirm should match");
            newPasswordBinding.editConfirmpassNewpassword.getEditText().requestFocus();
            newPasswordBinding.editConfirmpassNewpassword.setPasswordVisibilityToggleEnabled(false);
            showDialogPython.dismissDialog();
            return;
        }
        if (!(PasswordStrength.calculateStrength(pass).getValue() > PasswordStrength.STRONG.getValue())) {
            newPasswordBinding.editPassNewpassword.getEditText().setError("weak password");
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            newPasswordBinding.editPassNewpassword.getEditText().requestFocus();
            newPasswordBinding.editPassNewpassword.setPasswordVisibilityToggleEnabled(false);
            showDialogPython.dismissDialog();
            return;
        }

        if(userAccount.getEmail() != null){
            signInEmailAndPass(userAccount.getEmail(), userAccount.getPass() , pass);
        }
    }

    public void signInEmailAndPass(String email, String pass , String newPassword) {
        Log.e("old pass : ", pass);

        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
           //     Toast.makeText(getApplicationContext(), "Successful Sign In ", Toast.LENGTH_SHORT).show();
                user = mAuth.getCurrentUser();
                assert user != null;
                user.updatePassword(newPassword)
                        .addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Log.e("updata pass : ", newPassword);
                                Toast.makeText(getApplicationContext(), "User password updated.", Toast.LENGTH_SHORT).show();
                                db.collection("users").document(user.getUid()).update("pass",newPassword)
                                      .addOnCompleteListener(task11 -> {
                                          if(task11.isSuccessful()){
                                              Log.e("updata pass : ", "finish");
                                              Toast.makeText(getApplicationContext(), "User password updated.", Toast.LENGTH_SHORT).show();
                                          }else{
                                              Toast.makeText(getApplicationContext(), "failed to update password, Please try again later (5 Hour) And if this is repeated, Please contact our support team.", Toast.LENGTH_SHORT).show();
                                          }
                                          showDialogPython.dismissDialog();
                                          FirebaseAuth.getInstance().signOut();
                                          Intent intent=new Intent(NewPassword.this, SignIn.class);
                                          intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                          startActivity(intent);
                                      });
                            }else{
                                showDialogPython.dismissDialog();
                                //      Toast.makeText(getApplicationContext(), "failed to update password", Toast.LENGTH_SHORT).show();
                            }

                        });
            } else {
                showDialogPython.dismissDialog();
                Log.e("except : ", Objects.requireNonNull(task.getException()).toString());
           //     Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }





}