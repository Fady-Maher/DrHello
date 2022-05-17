package com.example.drhello;

import androidx.appcompat.app.AppCompatActivity;
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
    public ImageView imageview_send;
    private EditText edit_message;
    private String message = "Welcome to DrCare ChatBot, feel free to ask any Medical Questions!";
    private String correct_message ="";
    private HashMap map_causes,map_diagnosis,map_risk,map_see,map_symptoms,map_treatment;
    private String[] allSysmptoms ;
    private String[] sysmptomsyes;
    private String sysmptomsyes_string = "";
    private int x = 0;
    private String new_sysmptom = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bot);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            getWindow().setStatusBarColor(Color.WHITE);
        }
        mProgress = new ProgressDialog(BotActivity.this);

        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(BotActivity.this));//error is here!
        }

        json();

        final Python py = Python.getInstance();
        main_program = py.getModule("prolog");
        recyclerView = findViewById(R.id.rv_chat);
        imageview_send = findViewById(R.id.imageview_send);
        edit_message = findViewById(R.id.edit_message);

        arrayList.add(new ChatBotModel(message,getDateTime(),1,message));
        if(getIntent().getStringExtra("disease")!=null){
            String disease = getIntent().getStringExtra("disease");
            arrayList.add(0,new ChatBotModel("causes",getDateTime(),1,"causes"));
            arrayList.add(0,new ChatBotModel(map_causes.get(disease).toString(),getDateTime(),1,map_causes.get(disease).toString()));
            arrayList.add(0,new ChatBotModel("risk",getDateTime(),1,"risk"));
            arrayList.add(0,new ChatBotModel(map_risk.get(disease).toString(),getDateTime(),1,map_risk.get(disease).toString()));
            arrayList.add(0,new ChatBotModel("diagnosis",getDateTime(),1,"diagnosis"));
            arrayList.add(0,new ChatBotModel(map_diagnosis.get(disease).toString(),getDateTime(),1,map_diagnosis.get(disease).toString()));
            arrayList.add(0,new ChatBotModel("see doctor",getDateTime(),1,"see doctor"));
            arrayList.add(0,new ChatBotModel(map_see.get(disease).toString(),getDateTime(),1,map_see.get(disease).toString()));
            arrayList.add(0,new ChatBotModel("symptoms",getDateTime(),1,"symptoms"));
            arrayList.add(0,new ChatBotModel(map_symptoms.get(disease).toString(),getDateTime(),1,map_symptoms.get(disease).toString()));
            arrayList.add(0,new ChatBotModel("treatment",getDateTime(),1,"treatment"));
            arrayList.add(0,new ChatBotModel(map_treatment.get(disease).toString(),getDateTime(),1,map_treatment.get(disease).toString()));
        }

/*
        imageview_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(edit_message.getText().toString().toLowerCase().equals("yes") && !correct_message.equals("")){
                    //send to api
                    ChatBotModel chatBotModel = new ChatBotModel(edit_message.getText().toString(),getDateTime(),0,edit_message.getText().toString());
                    arrayList.add(0,chatBotModel);
                    AsyncTaskD asyncTaskD = new AsyncTaskD(chatBotModel,0,"chatbot");
                    asyncTaskD.execute();
                }else if(edit_message.getText().toString().toLowerCase().equals("no") && !correct_message.equals("")) {
                    message = "I can't understand you, please write sentence correctly!!";
                    arrayList.add(0,new ChatBotModel(message,getDateTime(),1,message));
                }else if(edit_message.getText().toString().toLowerCase().equals("yes") && correct_message.equals("")){

                    Log.e("YES: ", sysmptomsyes_string + "##");
                    if(sysmptomsyes_string.split("@").length >= 2){
                        sysmptomsyes_string +=  "@" + new_sysmptom ;
                    }
                    ChatBotModel chatBotModel = new ChatBotModel(edit_message.getText().toString(),getDateTime(),0,edit_message.getText().toString());
                    arrayList.add(0,chatBotModel);
                    if(sysmptomsyes_string.contains("@")){
                        AsyncTaskD asyncTaskD = new AsyncTaskD(chatBotModel,0,"chatbot_sysmptoms");
                        asyncTaskD.execute();
                    }else{
                        message ="Please enter another symptom!!";
                        arrayList.add(0,new ChatBotModel(message,getDateTime(),1,message));
                        chatBotAdapter.notifyItemInserted(0);
                    }
                }else if(edit_message.getText().toString().toLowerCase().equals("no") && correct_message.equals("")){

                    method();
                } else{ // correct message
                    ChatBotModel chatBotModel = new ChatBotModel(edit_message.getText().toString(),getDateTime(),0,edit_message.getText().toString());
                    arrayList.add(0,chatBotModel);
                    AsyncTaskD asyncTaskD = new AsyncTaskD(chatBotModel,0,"checker");
                    asyncTaskD.execute();
                }

                chatBotAdapter.notifyItemInserted(0);
            }
        });
*/


        chatBotAdapter = new ChatBotAdapter(BotActivity.this,arrayList,BotActivity.this);
        recyclerView.setAdapter(chatBotAdapter);

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
            if(action.equals("translate")){
                if(chatBotModel.getText().equals(chatBotModel.getTemp())){
                    String str = main_program.callAttr("method_translate",chatBotModel.getText()).toString();
                    chatBotModel.setText(str);
                }else{
                    chatBotModel.setText(chatBotModel.getTemp());
                }
                arrayList.remove(position);
                arrayList.add(position,chatBotModel);
            }
            /*
            else if(action.equals("chatbot")){
                String str = main_program.callAttr("modelText",chatBotModel.getText()).toString();
                Log.e("strain_",str);
                ChatBotModel chatBotModel = new ChatBotModel(str,getDateTime(),1,str);
                arrayList.add(0,chatBotModel);

            }else if(action.equals("checker")){
                String str = main_program.callAttr("method_spellchecker",chatBotModel.getText()).toString();
                if(!str.equals(chatBotModel.getText())){
                    message = "Do you mean " + str + "?"+", Please answer with (yes or no)!";
                    arrayList.add(0, new ChatBotModel(message,getDateTime(),1,message));
                    correct_message = str;
                    Log.e("strain_",str + " --- " + chatBotModel.getText());
                }else{
                    Log.e("else : ",str + " --- " + chatBotModel.getText());

                    String result = main_program.callAttr("modelText",chatBotModel.getText()).toString();

                    Log.e("strain_",result);
                    if(result.charAt(result.length()-1) == '3'){ // disease
                        String disease = result.substring(0,result.length()-1);
                        Log.e("res",disease);
                        arrayList.add(0,new ChatBotModel(disease,getDateTime(),1,disease));
                        arrayList.add(0,new ChatBotModel("causes",getDateTime(),1,"causes"));
                        arrayList.add(0,new ChatBotModel(map_causes.get(disease).toString(),getDateTime(),1,map_causes.get(disease).toString()));
                        arrayList.add(0,new ChatBotModel("risk",getDateTime(),1,"risk"));
                        arrayList.add(0,new ChatBotModel(map_risk.get(disease).toString(),getDateTime(),1,map_risk.get(disease).toString()));
                        arrayList.add(0,new ChatBotModel("diagnosis",getDateTime(),1,"diagnosis"));
                        arrayList.add(0,new ChatBotModel(map_diagnosis.get(disease).toString(),getDateTime(),1,map_diagnosis.get(disease).toString()));
                        arrayList.add(0,new ChatBotModel("see doctor",getDateTime(),1,"see doctor"));
                        arrayList.add(0,new ChatBotModel(map_see.get(disease).toString(),getDateTime(),1,map_see.get(disease).toString()));
                        arrayList.add(0,new ChatBotModel("symptoms",getDateTime(),1,"symptoms"));
                        arrayList.add(0,new ChatBotModel(map_symptoms.get(disease).toString(),getDateTime(),1,map_symptoms.get(disease).toString()));
                        arrayList.add(0,new ChatBotModel("treatment",getDateTime(),1,"treatment"));
                        arrayList.add(0,new ChatBotModel(map_treatment.get(disease).toString(),getDateTime(),1,map_treatment.get(disease).toString()));
                    }else if(result.charAt(result.length()-1) == '4'){ // sysmptoms
                        String sysmptom = result.substring(0,result.length()-1);
                        if(sysmptomsyes_string.contains("@")){
                            String[] list = {result.substring(0,result.length()-1),"2"};
                            Log.e("list",list.toString());
                            result = main_program.callAttr("modelText",result.substring(0,result.length()-1)).toString();
                            result = result.substring(0,result.length()-1);
                            Log.e("result20",result);
                            arrayList.add(0,new ChatBotModel(result,getDateTime(),1,result));
                            ArrayList<String> arrayList = (ArrayList<String>) map_symptoms.get(result);
                            allSysmptoms = arrayList.toArray(new String[0]);
                        }

                        if(sysmptomsyes_string.equals("")){
                            sysmptomsyes_string +=  sysmptom ;
                            method();
                        }else{
                            sysmptomsyes_string +=  "@" + sysmptom ;
                            sysmptomsyes_string +=  "@" + "2";
                            //String[] list = {sysmptomsyes,"2"};
                            Log.e("list",sysmptomsyes_string);
                            result = main_program.callAttr("model_classifer",sysmptomsyes_string).toString();
                            result = result.substring(0,result.length()-1);
                            if(result.contains("@")){
                                Log.e("@#$ : ",result);
                            }else{
                                method();
                            }
                            Log.e("resmethod",result);
                            ArrayList<String> arrayList = (ArrayList<String>) map_symptoms.get(result);
                            Log.e("arrayList",arrayList.toString());
                            allSysmptoms = arrayList.toArray(new String[0]);
                            sysmptomsyes_string = sysmptomsyes_string.substring(0,sysmptomsyes_string.length()-1);
                            method();
                        }
                        Log.e("sys: ",sysmptomsyes_string);
                    }
                }
            }else if(action.equals("chatbot_sysmptoms")){
                sysmptomsyes_string +=  "@" + "2";
                //String[] list = {sysmptomsyes,"2"};
                Log.e("list",sysmptomsyes_string);
                String result = main_program.callAttr("model_classifer",sysmptomsyes_string).toString();
                result = result.substring(0,result.length()-1);
                if(result.contains("@")){
                    Log.e("@#$ : ",result);
                }else{
                    method();
                }
                Log.e("resmethod",result);
                ArrayList<String> arrayList = (ArrayList<String>) map_symptoms.get(result);
                Log.e("arrayList",arrayList.toString());
                allSysmptoms = arrayList.toArray(new String[0]);
                sysmptomsyes_string = sysmptomsyes_string.substring(0,sysmptomsyes_string.length()-1);
                method();
            }

             */
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

    private void json(){
        try {
            ArrayList<Float> res = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(Objects.requireNonNull(
                    JsonDataFromAsset("causesDict.json")));
            map_causes = new Gson().fromJson(jsonObject.toString(), HashMap.class);

            jsonObject = new JSONObject(Objects.requireNonNull(
                    JsonDataFromAsset("diagnosisDict.json")));
            map_diagnosis = new Gson().fromJson(jsonObject.toString(), HashMap.class);

             jsonObject = new JSONObject(Objects.requireNonNull(
                    JsonDataFromAsset("riskFactorsDict.json")));
            map_risk = new Gson().fromJson(jsonObject.toString(), HashMap.class);

             jsonObject = new JSONObject(Objects.requireNonNull(
                    JsonDataFromAsset("seeDoctorDict.json")));
            map_see = new Gson().fromJson(jsonObject.toString(), HashMap.class);

            jsonObject = new JSONObject(Objects.requireNonNull(
                    JsonDataFromAsset("symptomsDict.json")));
            map_symptoms = new Gson().fromJson(jsonObject.toString(), HashMap.class);

            jsonObject = new JSONObject(Objects.requireNonNull(
                    JsonDataFromAsset("treatmentDict.json")));
            map_treatment = new Gson().fromJson(jsonObject.toString(), HashMap.class);


        } catch (
                JSONException e) {
            e.printStackTrace();
        }
    }

    private String JsonDataFromAsset(String name) {
        String json = null;
        try {
            InputStream inputStream = BotActivity.this.getAssets().open(name);
            int sizeOfFile = inputStream.available();
            byte[] bufferData = new byte[sizeOfFile];
            inputStream.read(bufferData);
            inputStream.close();
            json = new String(bufferData, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }

    private void method(){
        if(sysmptomsyes_string.contains("@")){
            if(allSysmptoms!= null && x < allSysmptoms.length){
                new_sysmptom = allSysmptoms[x];
                String ques = "Do you suffer from " + allSysmptoms[x] + "? , Please answer with yes or no !!";
                x = x +1;
                arrayList.add(0,new ChatBotModel(ques,getDateTime(),1,ques));
            }
        }else{
            String ques = "Do you suffer from another symptoms ? , Please answer with yes or no !!";
            arrayList.add(0,new ChatBotModel(ques,getDateTime(),1,ques));
        }
    }
}