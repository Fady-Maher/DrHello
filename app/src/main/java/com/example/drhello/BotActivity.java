package com.example.drhello;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.drhello.databinding.ActivityBotBinding;
import com.example.drhello.databinding.ActivityChatBotBinding;
import com.example.drhello.medical.ChestActivity;
import com.example.drhello.ui.login.CompleteInfoActivity;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class BotActivity extends AppCompatActivity implements  OnTranslateClickListener{
    RecyclerView recyclerView ;
    ChatBotAdapter chatBotAdapter ;
    ArrayList<ChatBotModel> arrayList = new ArrayList<>();
    PyObject main_program;
    public static ProgressDialog mProgress;
    private String message = "Welcome to DrCare ChatBot, feel free to ask any Medical Questions!";
    private ActivityBotBinding activityBotBinding ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bot);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            getWindow().setStatusBarColor(Color.WHITE);
        }
        activityBotBinding = DataBindingUtil.setContentView(this, R.layout.activity_bot);

        mProgress = new ProgressDialog(BotActivity.this);
        AsyncTaskD asyncTaskD = new AsyncTaskD(null,0,"first");
        asyncTaskD.execute();

        arrayList.add(new ChatBotModel(message,getDateTime(),1,message));

        chatBotAdapter = new ChatBotAdapter(BotActivity.this,arrayList,BotActivity.this);
        activityBotBinding.rvChat.setAdapter(chatBotAdapter);

        activityBotBinding.imageviewSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text  = activityBotBinding.editMessage.getText().toString();
                ChatBotModel chatBotModel = new ChatBotModel(text,getDateTime(),1,text);
                AsyncTaskD asyncTaskD = new AsyncTaskD(chatBotModel,0,"chatbot");
                asyncTaskD.execute();
            }
        });
    }

    @Override
    public void onClick(ChatBotModel chatBotModel, int position) {
        mProgress.setMessage("Translating...");
        mProgress.setCancelable(false);
        mProgress.show();
        AsyncTaskD asyncTaskD = new AsyncTaskD(chatBotModel,position,"translate");
        asyncTaskD.execute();
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss:SS a", Locale.US);
        Date date = new Date();
        return dateFormat.format(date);
    }

    public class AsyncTaskD extends AsyncTask<String, String, String> {
        ChatBotModel chatBotModel;
        int position;
        String action;

        public AsyncTaskD(ChatBotModel chatBotModel,int position,String action){
            this.chatBotModel = chatBotModel;
            this.position = position;
            this.action = action;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... f_url) {
            if(action.equals("first")){
                if (! Python.isStarted()) {
                    Python.start(new AndroidPlatform(BotActivity.this));//error is here!
                }
                final Python py = Python.getInstance();
                main_program = py.getModule("prolog");
            }else if(action.equals("translate")){
                if(chatBotModel.getText().equals(chatBotModel.getTemp())){
                    String str = main_program.callAttr("method_translate",chatBotModel.getText()).toString();
                    chatBotModel.setText(str);
                }else{
                    chatBotModel.setText(chatBotModel.getTemp());
                }
                arrayList.remove(position);
                arrayList.add(position,chatBotModel);
            }else if(action.equals("chatbot")){
                String str = main_program.callAttr("model_classifer",chatBotModel.getText()).toString();
                Log.e("CHATbot: ",str);
                chatBotModel.setText(str);
                activityBotBinding.editMessage.setText("");
            }

            mProgress.dismiss();
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            if(action.equals("translate")){
                chatBotAdapter.notifyItemChanged(position);
            }else if(action.equals("chatbot")) {
                chatBotAdapter.notifyItemInserted(position);
            }else if(action.equals("checker")){
                chatBotAdapter.notifyItemInserted(position);
            }else if(action.equals("chatbot_sysmptoms")){
                chatBotAdapter.notifyItemInserted(position);
            }
        }
    }


}