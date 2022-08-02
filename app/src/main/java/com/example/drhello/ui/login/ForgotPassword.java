package com.example.drhello.ui.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.example.drhello.R;
import com.example.drhello.other.ShowDialogPython;
import com.example.drhello.databinding.ActivityForgotPasswordBinding;
import com.example.drhello.signup.GMailSender;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Random;

public class ForgotPassword extends AppCompatActivity {
    ActivityForgotPasswordBinding forgotPasswordBinding;
    ShowDialogPython showDialogPython;

    //for validation email or phone

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            getWindow().setStatusBarColor(Color.BLUE);
        }


        //to connect layout with java code
        forgotPasswordBinding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password);

        forgotPasswordBinding.back.setOnClickListener(view -> finish());


        forgotPasswordBinding.btnForgetPass.setOnClickListener(view -> {
            showDialogPython = new ShowDialogPython(ForgotPassword.this,ForgotPassword.this.getLayoutInflater(),"load");
            showDialogPython.dismissDialog();
            String email = Objects.requireNonNull(forgotPasswordBinding.editUsernameForgetpass.getEditText()).getText().toString().trim();
            if (email.isEmpty()) {
                forgotPasswordBinding.editUsernameForgetpass.getEditText().setError("Email is needed");
                forgotPasswordBinding.editUsernameForgetpass.requestFocus();
            } else {
                checkemailorphone(email);
            }
        });
    }


    private void checkemailorphone(String email_or_phone) {
        //check if email
        if (email_or_phone.matches(".*[a-zA-Z]+.*") && isEmailValid(email_or_phone)) {
            Log.e("email : ", email_or_phone);
            sendEmail(email_or_phone);
        } else {
            Toast.makeText(getApplicationContext(), "invalid email , please try again!!", Toast.LENGTH_SHORT).show();
        }
    }


    private void sendEmail(String email_or_phone) {
        //Getting content for email
        //  String email =  userAccount.getEmail();
        Random random = new Random();
        @SuppressLint("DefaultLocale") String verify_num = String.format("%06d", random.nextInt(1000000));
        String englishNumerals = new BigDecimal(verify_num).toString();

        // String email = "jojo09477@gmail.com";

        String subject = "Easy Care Verification Code";
        String message = "E- " + englishNumerals + "  is your Easy Care Verification Code\n" +
                "Enter this code to activate this account " + email_or_phone + ".\n" +
                "\n" +
                "If you don't recognize " + email_or_phone +
                ", it is possible that someone has given your mail address by mistake. You can safely ignore this email.\n" +
                "\n" +
                "Yours sincerely,\n" +
                "The Easy Care Team.";
        showDialogPython.dismissDialog();
        //Creating SendMail object
        GMailSender sm = new GMailSender(this, email_or_phone, subject, message, englishNumerals, true);
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
}