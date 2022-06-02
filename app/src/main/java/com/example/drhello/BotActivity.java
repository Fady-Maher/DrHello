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

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.drhello.adapter.ChatBotAdapter;
import com.example.drhello.adapter.OnTranslateClickListener;
import com.example.drhello.databinding.ActivityBotBinding;
import com.example.drhello.model.ChatBotModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class BotActivity extends AppCompatActivity implements OnTranslateClickListener {
    RecyclerView recyclerView ;
    ChatBotAdapter chatBotAdapter ;
    ArrayList<ChatBotModel> arrayList = new ArrayList<>();
    PyObject main_program;
    public static ProgressDialog mProgress;
    private String message = "Welcome to DrCare ChatBot, feel free to ask any Medical Questions!";
    private ActivityBotBinding activityBotBinding ;
    int user = 0, bot = 1,pos = 0;
    int index = 0;
    String[] strings;
    String disease,allsymptoms;

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
        activityBotBinding.imgBackChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mProgress = new ProgressDialog(BotActivity.this);
        AsyncTaskD asyncTaskD = new AsyncTaskD(null,0,"first");
        asyncTaskD.execute();

        arrayList.add(new ChatBotModel(message,getDateTime(),bot,message));

        chatBotAdapter = new ChatBotAdapter(BotActivity.this,arrayList,BotActivity.this);
        activityBotBinding.rvChat.setAdapter(chatBotAdapter);

        activityBotBinding.imageviewSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text  = activityBotBinding.editMessage.getText().toString();
                if(!text.equals("")){
                    if(text.toLowerCase().equals("no")){
                        index = index + 1;
                        ChatBotModel chatBotModel = new ChatBotModel("no",getDateTime(),user,"no");
                        arrayList.add(0,chatBotModel);
                        chatBotAdapter.notifyItemInserted(pos);
                        if(strings.length  > index){
                            String qes = "Do you suffer from " + strings[index] + " ? , Please answer with ( Yes or No )";
                            arrayList.add(pos,new ChatBotModel(qes,getDateTime(),bot,qes));
                            chatBotAdapter.notifyItemInserted(pos);
                        }else{
                            String qes = "I can't detect disease , go to doctor";
                            arrayList.add(pos,new ChatBotModel(qes,getDateTime(),bot,qes));
                            chatBotAdapter.notifyItemInserted(pos);
                        }
                    }else if(text.toLowerCase().equals("yes")){
                        //String qes = "Do you suffer from " + strings[index] + " ? , Please answer with ( Yes or No )";
                        //arrayList.add(0,new ChatBotModel(qes,getDateTime(),bot,qes));
                        //chatBotAdapter.notifyItemInserted(0);
                        ChatBotModel chatBotModel = new ChatBotModel("yes",getDateTime(),user,"yes");
                        arrayList.add(0,chatBotModel);
                        chatBotAdapter.notifyItemInserted(pos);
                        allsymptoms = disease + "#" + strings[index]+ "#";
                        Log.e("yes : " , disease);
                        index = 0;
                        chatBotModel = new ChatBotModel(allsymptoms,getDateTime(),user,allsymptoms);
                        AsyncTaskD asyncTaskD = new AsyncTaskD(chatBotModel,pos,"chatbot");
                        asyncTaskD.execute();
                    }else{

                        ChatBotModel chatBotModel = new ChatBotModel(text,getDateTime(),user,text);
                        arrayList.add(pos,chatBotModel);
                        chatBotAdapter.notifyItemInserted(pos);
                        activityBotBinding.editMessage.setText("");
                        AsyncTaskD asyncTaskD = new AsyncTaskD(chatBotModel,pos,"chatbot");
                        asyncTaskD.execute();
                    }
                }
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
        String result;

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
                result = main_program.callAttr("model_classifer",chatBotModel.getText()).toString();
                Log.e("CHATbot: ",result);
            }

            mProgress.dismiss();
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            if(action.equals("translate")){
                chatBotAdapter.notifyItemChanged(position);
            }else if(action.equals("chatbot")) {
                if(result.charAt(result.length()-1) == '0'){
                    result = result.substring(0,result.length() - 1);
                    arrayList.add(position,new ChatBotModel(result,getDateTime(),bot,result));
                    chatBotAdapter.notifyItemInserted(position);
                    Log.e("result 0 : ",result);
                }else if(result.charAt(result.length()-1) == '1'){
                    Log.e("result 1 : ",result);
                    result = result.substring(0,result.length() - 1);
                    chatBotModel.setText(result+"#");
                    chatBotModel.setDate(getDateTime());
                    AsyncTaskD asyncTaskD = new AsyncTaskD(chatBotModel,pos,"chatbot");
                    asyncTaskD.execute();
                }else if(result.charAt(result.length()-1) == '2'){
                    result = result.substring(0,result.length() - 1);
                    if(result.contains("@")){
                        Log.e("result @ : ",result);
                        String[] diseases = result.split("@");
                        for(int i = 0 ;i<diseases.length;i++){
                            arrayList.add(position,new ChatBotModel(diseases[i],getDateTime(),bot,diseases[i]));
                            chatBotAdapter.notifyItemInserted(position);
                        }
                    }else if(result.contains("&")){
                        Log.e("result & : ",result);
                        disease = result.split("&")[0];
                        index = 0 ;
                        String symptoms = result.split("&")[1]
                                .replace("[","").replace("]","").replace("\'","");
                        strings = symptoms.split(", ");
                        String qes = "Do you suffer from " + strings[index] + " ? , Please answer with ( Yes or No )";
                        arrayList.add(position,new ChatBotModel(qes,getDateTime(),bot,qes));
                        chatBotAdapter.notifyItemInserted(position);
                    }else{
                        Log.e("resultelse : ",result);
                        arrayList.add(position,new ChatBotModel(result,getDateTime(),bot,result));
                        chatBotAdapter.notifyItemInserted(position);
                    }
                }

            }else if(action.equals("checker")){
                chatBotAdapter.notifyItemInserted(position);
            }else if(action.equals("chatbot_sysmptoms")){
                chatBotAdapter.notifyItemInserted(position);
            }
        }
    }


}