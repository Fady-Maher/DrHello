package com.example.drhello.ui.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import com.example.drhello.R;
import com.example.drhello.databinding.ActivityVerifyForgetPasswordBinding;
import com.example.drhello.signup.GMailSender;
import java.math.BigDecimal;
import java.util.Random;

public class VerifyForgetPasswordActivity extends AppCompatActivity {
    ActivityVerifyForgetPasswordBinding verifyBinding;

    String email = "" , verify_num = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_forget_password);

        verifyBinding = DataBindingUtil.setContentView(this, R.layout.activity_verify_forget_password);

        if(getIntent().getStringExtra("email") != null){
            email  = getIntent().getStringExtra("email");
            verify_num = getIntent().getStringExtra("verify_num");
            verifyBinding.txtEmailVerifyforget.setText(getIntent().getStringExtra("email"));
        }


        verifyBinding.verifyNumVerifyforget.setOnAllFilledListener(text -> {
            if(text.equals(verify_num)){
                Intent intent=new Intent(VerifyForgetPasswordActivity.this, NewPassword.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("email",email);
                startActivity(intent);
            }
        });



        verifyBinding.txtVerifyforgetNum.setOnClickListener(view -> {
            //RESEND
            if(!email.equals("")){
                resendEmail(email);
            }
        });

    }

    private void resendEmail(String email_or_phone) {
        //  String email =  userAccount.getEmail();
        Random random = new Random();
        @SuppressLint("DefaultLocale") String verify_num_new = String.format("%06d", random.nextInt(1000000));
        String englishNumerals = new BigDecimal(verify_num_new).toString();
        verify_num = englishNumerals;

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

        //Creating SendMail object
        GMailSender sm = new GMailSender(this, email_or_phone, subject, message, englishNumerals, false);
        //Executing sendmail to send email
        sm.execute();

    }


}