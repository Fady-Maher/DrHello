package com.example.drhello.ui.botchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.drhello.adapter.ChatBotlistener;
import com.example.drhello.R;
import com.example.drhello.connectionnewtwork.CheckNetwork;
import com.example.drhello.firebaseinterface.MyCallbackUser;
import com.example.drhello.model.UrlsModel;
import com.example.drhello.other.ShowDialogPython;
import com.example.drhello.adapter.ChatBotAdapter;
import com.example.drhello.adapter.OnTranslateClickListener;
import com.example.drhello.databinding.ActivityBotBinding;
import com.example.drhello.model.ChatBotModel;
import com.example.drhello.ui.login.ForgotPassword;
import com.example.drhello.ui.login.SignIn;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ml.modeldownloader.CustomModel;
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions;
import com.google.firebase.ml.modeldownloader.DownloadType;
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader;

import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public class BotActivity extends AppCompatActivity implements OnTranslateClickListener, ChatBotlistener {
    ChatBotAdapter chatBotAdapter;
    ArrayList<ChatBotModel> arrayList = new ArrayList<>();
    PyObject main_program;
    private String message = "Welcome to DrCare ChatBot, feel free to ask any Medical Questions!";
    private ActivityBotBinding activityBotBinding;
    int user = 0, bot = 1, pos = 0;
    int index = 0;
    String[] strings;
    String  accept = "", refuse = "";
    boolean flag = false;
    private ShowDialogPython showDialogPython;
    String action, result;
    private UrlsModel urlsModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bot);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            getWindow().setStatusBarColor(Color.WHITE);
        }
        activityBotBinding = DataBindingUtil.setContentView(BotActivity.this, R.layout.activity_bot);

        activityBotBinding.imgBackChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        action = "first";

        readDataurl(new MyCallbackUser() {
            @Override
            public void onCallback(DocumentSnapshot documentSnapshot) {
                if (!documentSnapshot.exists()) {
                    FirebaseAuth.getInstance().getCurrentUser().delete();
                    Log.e("ERROR: " ,"error");
                    Toast.makeText(getApplicationContext(),"May be problem when collect data",Toast.LENGTH_SHORT).show();
                } else {
                    urlsModel = documentSnapshot.toObject(UrlsModel.class);
                    Log.e("URLMODEL: " ,urlsModel.getUrl());
                    AsyncTaskD asyncTaskD = new AsyncTaskD(null, 0);
                    asyncTaskD.execute();
                }
            }
        });


        arrayList.add(new ChatBotModel(message, getDateTime(), bot, message));

        chatBotAdapter = new ChatBotAdapter(BotActivity.this, arrayList, BotActivity.this, BotActivity.this);
        activityBotBinding.rvChat.setAdapter(chatBotAdapter);

        activityBotBinding.imageviewSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CheckNetwork.getConnectivityStatusString(BotActivity.this) == 1) {
                    String text = activityBotBinding.editMessage.getText().toString();
                    if (!text.equals("")) {
                        hideKeyboard(BotActivity.this);
                        checkText(text, text);
                    }
                }else{
                    Toast.makeText(BotActivity.this, "Please, Check Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void readDataurl(MyCallbackUser myCallback) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            FirebaseFirestore.getInstance().collection("Admins")
                    .document("RqE4viVs8SrFt2RxSKSw").collection("urls")
                    .document("ZMfzJrIIgvAW8eRMsGib").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                Log.e("URLMODEL: " ,"exists");

                            }else{
                                Log.e("URLMODEL: " ,"not ");
                            }
                            myCallback.onCallback(documentSnapshot);
                        }
                    });
        }
    }
    void checkText(String text, String temp) {
        Log.e("text: ", text + " temp :" + temp);
        ChatBotModel chatBotModel;
        if (text.toLowerCase().equals("exit")) {
            refuse = "";
            accept = "";
            flag = false;
            index = 0;
            if (text.equals(temp)) {
                Log.e("false if: ", text + " temp :" + temp);
                chatBotModel = new ChatBotModel(text, getDateTime(), user, text);
            } else {
                Log.e("false else: ", text + " temp :" + temp);
                chatBotModel = new ChatBotModel(temp, getDateTime(), user, temp);
            }
            arrayList.add(0, chatBotModel);
            chatBotAdapter.notifyItemInserted(pos);
            chatBotModel = new ChatBotModel("Thanks", getDateTime(), bot, temp);
            arrayList.add(0, chatBotModel);
            chatBotAdapter.notifyItemInserted(pos);
            activityBotBinding.rvChat.scrollToPosition(0);
        } else if (text.toLowerCase().equals("no") && flag) {
            activityBotBinding.editMessage.getText().equals("");
            refuse = refuse + strings[index] + "&";
            index = index + 1;
            //text modify     temp original
            if (text.equals(temp)) {
                chatBotModel = new ChatBotModel("no", getDateTime(), user, "no");
            } else {
                Log.e("text: ", text + " temp :" + temp);
                chatBotModel = new ChatBotModel(temp, getDateTime(), user, temp);
            }
            arrayList.add(0, chatBotModel);
            chatBotAdapter.notifyItemInserted(pos);
            activityBotBinding.rvChat.scrollToPosition(0);
            Log.e("refuse : ", refuse);
            if (strings.length > index) {
                String qes = "Do you suffer from " + strings[index] + " ? , Please answer with ( Yes or No )";
                arrayList.add(pos, new ChatBotModel(qes, getDateTime(), bot, qes));
                chatBotAdapter.notifyItemInserted(pos);
                activityBotBinding.rvChat.scrollToPosition(0);
            } else {
                refuse = "";
                accept = "";
                flag = false;
                index = 0;
                String qes = "I can't detect disease , go to doctor";
                arrayList.add(pos, new ChatBotModel(qes, getDateTime(), bot, qes));
                chatBotAdapter.notifyItemInserted(pos);
                activityBotBinding.rvChat.scrollToPosition(0);
            }
        } else if (text.toLowerCase().equals("yes") && flag) {
            activityBotBinding.editMessage.getText().equals("");
            if (text.equals(temp)) {
                Log.e("yes if: ", text + " temp :" + temp);
                chatBotModel = new ChatBotModel("yes", getDateTime(), user, "yes");
            } else {
                Log.e("yes else: ", text + " temp :" + temp);
                chatBotModel = new ChatBotModel(temp, getDateTime(), user, temp);
            }
            arrayList.add(0, chatBotModel);
            chatBotAdapter.notifyItemInserted(pos);
            activityBotBinding.rvChat.scrollToPosition(0);
            accept += strings[index] + "&";
            Log.e("accept : ", accept);
            index = 0;
            ///     chatBotModel = new ChatBotModel(allsymptoms, getDateTime(), user, allsymptoms);
            action = "chatbot";
            AsyncTaskD asyncTaskD = new AsyncTaskD(chatBotModel, pos);
            asyncTaskD.execute();
        } else if (flag) {
            //text modify     temp original
            if (text.equals(temp)) {
                Log.e("false if: ", text + " temp :" + temp);
                chatBotModel = new ChatBotModel(text, getDateTime(), user, text);
            } else {
                Log.e("false else: ", text + " temp :" + temp);
                chatBotModel = new ChatBotModel(temp, getDateTime(), user, temp);
            }
            arrayList.add(0, chatBotModel);
            chatBotAdapter.notifyItemInserted(pos);
            chatBotModel = new ChatBotModel("Please , answer with ( yes or no )!! or write exit to end checker", getDateTime(), bot, temp);
            arrayList.add(0, chatBotModel);
            chatBotAdapter.notifyItemInserted(pos);
            activityBotBinding.rvChat.scrollToPosition(0);
        } else {
            flag = false;
            if (text.equals(temp)) {
                Log.e("false if: ", text + " temp :" + temp);
                chatBotModel = new ChatBotModel(text, getDateTime(), user, text);
            } else {
                Log.e("false else: ", text + " temp :" + temp);
                chatBotModel = new ChatBotModel(temp, getDateTime(), user, temp);
            }
            arrayList.add(pos, chatBotModel);
            chatBotAdapter.notifyItemInserted(pos);
            activityBotBinding.rvChat.scrollToPosition(0);
            activityBotBinding.editMessage.setText("");
            chatBotModel = new ChatBotModel(text, getDateTime(), user, text);
            action = "chatbot";

            if (getKeyboardLanguage(text).equals("EN")) {
                AsyncTaskD asyncTaskD = new AsyncTaskD(chatBotModel, pos);
                asyncTaskD.execute();
            } else {
                AsyncTaskD asyncTaskD = new AsyncTaskD(chatBotModel, pos);
                asyncTaskD.execute();
            }

        }
    }

    @Override
    public void onClick(ChatBotModel chatBotModel, int position) {
        showDialogPython = new ShowDialogPython(BotActivity.this, BotActivity.this.getLayoutInflater(), "translate");
        action = "translate";
        AsyncTaskD asyncTaskD = new AsyncTaskD(chatBotModel, position);
        asyncTaskD.execute();
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss:SS a", Locale.US);
        Date date = new Date();
        return dateFormat.format(date);
    }

    @Override
    public void onClick(String link) {
        Log.e("onClick: ", link);
        if (link.contains("http")) {
            Log.e("http: ", link);
            Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            startActivity(browse);
        }
    }


    public class AsyncTaskD extends AsyncTask<String, String, String> {
        ChatBotModel chatBotModel;
        int position;
        String errorMessage = "Some thing Wrong when translate this message";
        String str_translate;

        public AsyncTaskD(ChatBotModel chatBotModel, int position) {
            this.chatBotModel = chatBotModel;
            this.position = position;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!action.equals("translate")) {
                showDialogPython = new ShowDialogPython(BotActivity.this, BotActivity.this.getLayoutInflater(), "load");
            }
        }

        @Override
        protected String doInBackground(String... f_url) {
            if (action.equals("first")) {
                if (!Python.isStarted()) {
                    Python.start(new AndroidPlatform(BotActivity.this));//error is here!
                }
                final Python py = Python.getInstance();
                main_program = py.getModule("prolog");
            } else if (action.equals("translate")) {
                if (chatBotModel.getText().equals(chatBotModel.getTemp())) {
                     str_translate = main_program.callAttr("method_translate", chatBotModel.getText()).toString();
                    if(!str_translate.equals("error")){
                        chatBotModel.setText(str_translate);
                        arrayList.remove(position);
                        arrayList.add(position, chatBotModel);
                    }else{
                        Log.e("translate: ", " ERROR");
                        arrayList.add(0, new ChatBotModel(errorMessage, getDateTime(), bot, errorMessage));
                    }
                } else {
                    str_translate = "str_translate";
                    chatBotModel.setText(chatBotModel.getTemp());
                    arrayList.remove(position);
                    arrayList.add(position, chatBotModel);
                }

                Log.e("translate8 : ", action);
            } else if (action.equals("chatbot")) {
                if (!flag) {
                    result = main_program.callAttr("chatbot",urlsModel.getUrl(), chatBotModel.getText()).toString();
                    Log.e("res false: ", result);
                    Log.e("res false: ", chatBotModel.getText());
                } else {
                    Log.e("accept: ", accept);
                    Log.e("refuse: ", refuse);
                    result = main_program.callAttr("diseasePrediction2",urlsModel.getUrl(), accept, refuse).toString();
                    Log.e("res true: ", result);
                }
                result = result.replace("\"", "");
            }

            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            if (action.equals("first")) {
                showDialogPython.dismissDialog();
            } else if (action.equals("translate")) {
                if(!str_translate.equals("error")) {
                    chatBotAdapter.notifyItemChanged(position);
                }else{
                    chatBotAdapter.notifyItemInserted(0);
                }
                showDialogPython.dismissDialog();
                Log.e("translate6 : ", action);
            }else if (action.equals("chatbot")) {
                if(!result.equals("error")){
                    Log.e("result: ", "NOT ERROR");
                    if (result.substring(0,2).equals("0-")) {
                        String[] information = result.split(Pattern.quote("\\n"));
                        for(int i = 0 ; i < information.length-1;i++){
                            Log.e("result : " + i,information[i]);
                            information[i] = information[i].replace("\\\\xa0","").replace("\\","");
                            arrayList.add(0, new ChatBotModel(information[i], getDateTime(), bot, information[i]));
                            chatBotAdapter.notifyItemInserted(0);
                        }
                        showDialogPython.dismissDialog();
                    } else if (result.charAt(result.length() - 1) == '0') {
                        if (result.contains("overview")) {
                            try{
                                result = result.substring(0, result.length() - 1);
                                String URL = result.split("url of disease")[1];
                                String overview = result.split("Symptoms")[0];
                                result = result.split("Symptoms")[1];
                                String Symptoms = result.split("When To See A Doctor")[0];
                                result = result.split("When To See A Doctor")[1];
                                String Doctor = result.split("Causes")[0];
                                result = result.split("Causes")[1];
                                String Causes = result.split("Risk Factors")[0];
                                result = result.split("Risk Factors")[1];
                                String Risk = result.split("Diagnosis")[0];
                                result = result.split("Diagnosis")[1];
                                String Diagnosis = result.split("Treatment")[0];
                                result = result.split("Treatment")[1];
                                String Treatment = result.split("url of disease")[0];
                                splitText(overview);
                                splitText("Symptoms: " + "\n" + Symptoms.replace(":", ""));
                                splitText("Doctor: " + "\n" + Doctor.replace(":", ""));
                                splitText("Causes: " + "\n" + Causes.replace(":", ""));
                                splitText("Risk Factors: " + "\n" + Risk.replace(":", ""));
                                splitText("Diagnosis: " + "\n" + Diagnosis.replace(":", ""));
                                splitText("Treatment: " + "\n" + Treatment.replace(":", ""));
                                activityBotBinding.rvChat.scrollToPosition(0);
                                splitText("Url: " + "\n" + URL.substring(1, URL.length() - 1).replace("\"", ""));
                            }catch (Exception e){
                                String mess = "Some thing Wrong when retrive Data";
                                arrayList.add(0, new ChatBotModel(mess, getDateTime(), bot, mess));
                                chatBotAdapter.notifyItemInserted(0);
                                Toast.makeText(BotActivity.this,"",Toast.LENGTH_LONG);
                            }

                            showDialogPython.dismissDialog();
                        } else {
                            result = result.substring(0, result.length() - 1);
                            arrayList.add(position, new ChatBotModel(result, getDateTime(), bot, result));
                            chatBotAdapter.notifyItemInserted(position);
                            activityBotBinding.rvChat.scrollToPosition(0);
                            showDialogPython.dismissDialog();
                            Log.e("0 : ", result);
                        }
                        refuse = "";
                        accept = "";
                        flag = false;
                        index = 0;
                    } else if (result.charAt(result.length() - 1) == '1') {
                        result = result.substring(0, result.length() - 1);
                        flag = true;
                        accept += result + "&";
                        index = 0;
                        refuse = "";
                        Log.e("1 : ", result);
                        showDialogPython.dismissDialog();
                        action = "chatbot";
                        AsyncTaskD asyncTaskD = new AsyncTaskD(chatBotModel, pos);
                        asyncTaskD.execute();
                    } else if (result.charAt(result.length() - 1) == '6') {
                        result = result.substring(0, result.length() - 1);
                        flag = false;
                        accept = "";
                        index = 0;
                        refuse = "";
                        Log.e("6 : ", result);
                        String[] diseases = result.split("&");
                        for (int i = 0; i < diseases.length; i++) {
                            arrayList.add(position, new ChatBotModel(diseases[i], getDateTime(), bot, diseases[i]));
                            chatBotAdapter.notifyItemInserted(position);
                        }
                        activityBotBinding.rvChat.scrollToPosition(0);
                        showDialogPython.dismissDialog();

                    } else if (result.charAt(result.length() - 1) == '7') {
                        result = result.substring(0, result.length() - 1);
                        strings = result.split("&");
                        Log.e("7 : ", result);
                        String qes = "Do you suffer from " + strings[index] + " ? , Please answer with ( Yes or No )";
                        arrayList.add(position, new ChatBotModel(qes, getDateTime(), bot, qes));
                        chatBotAdapter.notifyItemInserted(position);
                        activityBotBinding.rvChat.scrollToPosition(0);
                        showDialogPython.dismissDialog();
                        flag = true;
                    }

                } else{
                    Log.e("result: ", " ERROR");

                    String mess = "Some thing Wrong when retrive Data";
                    arrayList.add(0, new ChatBotModel(mess, getDateTime(), bot, mess));
                    chatBotAdapter.notifyItemInserted(0);
                    showDialogPython.dismissDialog();
                }

            } else if (action.equals("chatbot_sysmptoms")) {
                chatBotAdapter.notifyItemInserted(position);
            }
        }
    }

    private void splitText(String str) {
        String[] information = str.split(Pattern.quote("\\n"));
        String info = "";
        for (int i = 0; i < information.length; i++) {
            if (!information[i].equals("")) {
                info = info + information[i] + "\n";
            }
        }

        Log.e("str : ", info + "");
        info = info.replace("\\","");
        arrayList.add(0, new ChatBotModel(info, getDateTime(), bot, info));
        chatBotAdapter.notifyItemInserted(0);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public static String getKeyboardLanguage(String s) {
        for (int i = 0; i < s.length(); ) {
            int c = s.codePointAt(i);
            if (c >= 0x0600 && c <= 0x06E0)
                return "AR";
            i += Character.charCount(c);
        }
        return "EN";
    }
}