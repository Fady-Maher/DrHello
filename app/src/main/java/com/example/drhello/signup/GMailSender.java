package com.example.drhello.signup;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.drhello.model.UserAccount;
import com.example.drhello.ui.login.VerifyActivity;
import com.example.drhello.ui.login.VerifyForgetPasswordActivity;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class GMailSender extends AsyncTask<Void,Void,Void> {

    @SuppressLint("StaticFieldLeak")
    private final Context context;

    //Information to send email
    private final String email;
    private final String subject;
    private final String message;
    private UserAccount userAccount;
    private final String verify_num;
    private Boolean flag_activity = false, flag_activity_forget = false;
    private final String EMAIL;

    {
        EMAIL = "EMAIL";
    }

    //Progressdialog to show while sending email
    private ProgressDialog progressDialog;

    //Class Constructor
    public GMailSender(Context context, String email, String subject, String message, String verify_num , Boolean flag_activity_forget){
        //Initializing variables
        this.context = context;
        this.email = email;
        this.subject = subject;
        this.message = message;
        this.verify_num = verify_num;
        this.flag_activity_forget = flag_activity_forget;
    }

    //Class Constructor
    public GMailSender(Context context, String email, String subject, String message , UserAccount userAccount , String verify_num , Boolean flag_activity){
        //Initializing variables
        this.context = context;
        this.email = email;
        this.subject = subject;
        this.message = message;

        this.userAccount = userAccount;
        this.verify_num = verify_num;
        this.flag_activity = flag_activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Showing progress dialog while sending email
        progressDialog = ProgressDialog.show(context,"Sending Verification Code","Please wait...",false,false);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //Dismissing the progress dialog
        progressDialog.dismiss();
        //Showing a success message

        //AFTER SEND GMAIL MESSAGE WILL TAKE TO VerifyActivity

        if(flag_activity){
            Toast.makeText(context,"Verification Code Sent",Toast.LENGTH_LONG).show();
            Intent intent=new Intent(context, VerifyActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("userAccount",userAccount);
            intent.putExtra("verify_num", verify_num);
            intent.putExtra("method",EMAIL);
            context.startActivity(intent);
        }

        //TO FOREGET PASSWORD AND WILL TAKE TO NEWPASSWORD ACTIVITY
        if(flag_activity_forget){
            Toast.makeText(context,"Password Reset Code Sent",Toast.LENGTH_LONG).show();
            Intent intent=new Intent(context, VerifyForgetPasswordActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("email",email);
            intent.putExtra("verify_num", verify_num);
            intent.putExtra("method",EMAIL);
            context.startActivity(intent);
        }

        Log.e("log", "Message Sent");
    }

    @Override
    protected Void doInBackground(Void... params) {
        //Creating properties
        Properties props = new Properties();

        //Configuring properties for gmail
        //If you are not using gmail you may need to change the values
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        //Creating a new session
        //Authenticating the password
        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    //Authenticating the password
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(Config.EMAIL, Config.PASSWORD);
                    }
                });

        try {
            //Creating MimeMessage object
            MimeMessage mm = new MimeMessage(session);

            //Setting sender address
            mm.setFrom(new InternetAddress(Config.EMAIL));
            //Adding receiver
            mm.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            //Adding subject
            mm.setSubject(subject);
            //Adding message
            mm.setText(message);

            //Sending email
            Transport.send(mm);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }



    public static class Config {
        public static final String EMAIL ="careeasy6@gmail.com"; //your-gmail-username
        public static final String PASSWORD ="careeasy125@_"; //your-gmail-password

    }
}