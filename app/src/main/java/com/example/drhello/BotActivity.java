package com.example.drhello;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.drhello.adapter.ChatBotAdapter;
import com.example.drhello.adapter.OnTranslateClickListener;
import com.example.drhello.databinding.ActivityBotBinding;
import com.example.drhello.medical.ChestActivity;
import com.example.drhello.model.ChatBotModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import pl.droidsonroids.gif.GifImageView;

public class BotActivity extends AppCompatActivity implements OnTranslateClickListener, ChatBotlistener {
    public final static String AR_STRING = "ا آ ب پ ت ث ج چ ح خ د ذ ر ز ژ س ش ص ض ط ظ ع غ ف ق ک گ ل م ن و ه ی";
    public final static String EN_STRING = "a b c d e f g h i j k l m n o p q r s t u v w x y z";

    ChatBotAdapter chatBotAdapter;
    ArrayList<ChatBotModel> arrayList = new ArrayList<>();
    PyObject main_program;
    private String message = "Welcome to DrCare ChatBot, feel free to ask any Medical Questions!";
    private ActivityBotBinding activityBotBinding;
    int user = 0, bot = 1, pos = 0;
    int index = 0;
    String[] strings;
    String  allsymptoms, accept = "", refuse = "";
    boolean flag = false;
    private ShowDialogPython showDialogPython;
    float prop;
    String action,result;

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
        AsyncTaskD asyncTaskD = new AsyncTaskD(null, 0);
        asyncTaskD.execute();

        arrayList.add(new ChatBotModel(message, getDateTime(), bot, message));

        chatBotAdapter = new ChatBotAdapter(BotActivity.this, arrayList, BotActivity.this, BotActivity.this);
        activityBotBinding.rvChat.setAdapter(chatBotAdapter);

        activityBotBinding.imageviewSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = activityBotBinding.editMessage.getText().toString();
                if (!text.equals("")) {
                    hideKeyboard(BotActivity.this);
                    if(getKeyboardLanguage(text) == "AR"){
                        activityBotBinding.editMessage.getText().equals("");
                        Log.e("Transl : ","AR");
                        ChatBotModel chatBotModel = new ChatBotModel(text, getDateTime(), user, text);
                        action = "translateAR";
                        AsyncTaskD asyncTaskD = new AsyncTaskD(chatBotModel, 0);
                        asyncTaskD.execute();
                    }else{
                        checkText(text,text);
                    }
                }
            }
        });
    }

    void checkText(String text,String temp){
        Log.e("text: " , text + " temp :" + temp);
        ChatBotModel chatBotModel;
        if (text.toLowerCase().equals("exit")){
            refuse = "";
            accept = "";
            flag = false;
            index = 0;
            if(text.equals(temp)){
                Log.e("false if: " , text + " temp :" + temp);
                chatBotModel = new ChatBotModel(text, getDateTime(), user, text);
            }else{
                Log.e("false else: " , text + " temp :" + temp);
                chatBotModel = new ChatBotModel(temp, getDateTime(), user, temp);
            }
            arrayList.add(0, chatBotModel);
            chatBotAdapter.notifyItemInserted(pos);
            chatBotModel = new ChatBotModel("Thanks", getDateTime(), bot, temp);
            arrayList.add(0, chatBotModel);
            chatBotAdapter.notifyItemInserted(pos);
            activityBotBinding.rvChat.scrollToPosition(0);

        }else if (text.toLowerCase().equals("no") && flag) {
            activityBotBinding.editMessage.getText().equals("");
            refuse = refuse + strings[index] + "&";
            index = index + 1;
            //text modify     temp original
            if(text.equals(temp)){
                chatBotModel = new ChatBotModel("no", getDateTime(), user, "no");
            }else{
                Log.e("text: " , text + " temp :" + temp);
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
            if(text.equals(temp)){
                Log.e("yes if: " , text + " temp :" + temp);
                chatBotModel = new ChatBotModel("yes", getDateTime(), user, "yes");
            }else{
                Log.e("yes else: " , text + " temp :" + temp);
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
            AsyncTaskModel asyncTaskModel = new AsyncTaskModel(chatBotModel, pos);
            asyncTaskModel.execute();

        } else if(flag){
            //text modify     temp original
            if(text.equals(temp)){
                Log.e("false if: " , text + " temp :" + temp);
                chatBotModel = new ChatBotModel(text, getDateTime(), user, text);
            }else{
                Log.e("false else: " , text + " temp :" + temp);
                chatBotModel = new ChatBotModel(temp, getDateTime(), user, temp);
            }
            arrayList.add(0, chatBotModel);
            chatBotAdapter.notifyItemInserted(pos);
            chatBotModel = new ChatBotModel("Please , answer with ( yes or no )!! or write exit to end checker", getDateTime(), bot, temp);
            arrayList.add(0, chatBotModel);
            chatBotAdapter.notifyItemInserted(pos);
            activityBotBinding.rvChat.scrollToPosition(0);
        }else {
            flag = false;
            if(text.equals(temp)){
                Log.e("false if: " , text + " temp :" + temp);
                chatBotModel = new ChatBotModel(text, getDateTime(), user, text);
            }else{
                Log.e("false else: " , text + " temp :" + temp);
                chatBotModel = new ChatBotModel(temp, getDateTime(), user, temp);
            }
            arrayList.add(pos, chatBotModel);
            chatBotAdapter.notifyItemInserted(pos);
            activityBotBinding.rvChat.scrollToPosition(0);
            activityBotBinding.editMessage.setText("");
            chatBotModel = new ChatBotModel(text, getDateTime(), user, text);
            action = "chatbot";

            AsyncTaskModel asyncTaskModel = new AsyncTaskModel(chatBotModel, pos);
            asyncTaskModel.execute();
        }
    }

    @Override
    public void onClick(ChatBotModel chatBotModel, int position) {
        showDialogPython = new ShowDialogPython(BotActivity.this, BotActivity.this.getLayoutInflater(),"translate");
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
        public AsyncTaskD(ChatBotModel chatBotModel, int position ) {
            this.chatBotModel = chatBotModel;
            this.position = position;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (action.equals("first")) {
                showDialogPython = new ShowDialogPython(BotActivity.this, BotActivity.this.getLayoutInflater(),"load");
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
                    String str = main_program.callAttr("method_translate", chatBotModel.getText()).toString();
                    chatBotModel.setText(str);
                } else {
                    chatBotModel.setText(chatBotModel.getTemp());
                }
                arrayList.remove(position);
                arrayList.add(position, chatBotModel);
            }else if (action.equals("translateAR")) {
                    String str = main_program.callAttr("method_translateAR", chatBotModel.getText()).toString();
                    chatBotModel.setText(str);
            } else if (action.equals("chatbot")) {
                if(prop >= 0.5){
                    Log.e("action: ", "result");
                    action = "failed";
                }else{
                    if (!flag) {
                        result = main_program.callAttr("chatbot", chatBotModel.getText()).toString();
                        Log.e("res false: ", result);
                        Log.e("res false: ", chatBotModel.getText());
                    } else {
                        Log.e("accept: ", accept);
                        Log.e("refuse: ", refuse);
                        result = main_program.callAttr("diseasePrediction2", accept, refuse).toString();
                        Log.e("res true: ", result);
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            if (action.equals("first")) {
                showDialogPython.dismissDialog();
            } else if (action.equals("translate")) {
                chatBotAdapter.notifyItemChanged(position);
                showDialogPython.dismissDialog();
            }else if (action.equals("translateAR")) {
                checkText(chatBotModel.getText(),chatBotModel.getTemp());
                Log.e("translateAR: " , chatBotModel.getText());
            } else if (action.equals("chatbot")) {
                if (result.charAt(result.length() - 1) == '0') {
                    if (result.contains("overview")) {
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
            } else if (action.equals("checker")) {
                chatBotAdapter.notifyItemInserted(position);
            } else if (action.equals("chatbot_sysmptoms")) {
                chatBotAdapter.notifyItemInserted(position);
            }else if(action.equals("failed")){
                arrayList.add(position, new ChatBotModel("Choose your words when to speak to me!!!", getDateTime(), bot, "Choose your words when to speak to me!!!"));
                chatBotAdapter.notifyItemInserted(position);
                showDialogPython.dismissDialog();
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


    private void modelFire(String text) {
        String resultModel = main_program.callAttr("predictComment", text,getKeyboardLanguage(text)).toString();
        resultModel = resultModel.replace("[", "").replace("]", "");
        String[] strings = resultModel.split(", ");
        Log.e("result: ", resultModel);
        float[][] input = new float[1][300];
        for (int i = 0; i < strings.length; i++) {
            input[0][i] = Float.parseFloat(strings[i]);
        }
        CustomModelDownloadConditions conditions = new CustomModelDownloadConditions.Builder()
                .requireWifi()
                .build();
        Task<CustomModel> model;
        if(getKeyboardLanguage(text).equals("EN")){
            Log.e("lang : ",   "EN");
            model = FirebaseModelDownloader.getInstance()
                    .getModel("HateAbusiveModelEN", DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND, conditions);
        }else{
            Log.e("lang : ",   "AR");
            model = FirebaseModelDownloader.getInstance()
                    .getModel("arabicHateOff", DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND, conditions);
        }

        model.addOnSuccessListener(new OnSuccessListener<CustomModel>() {
            @Override
            public void onSuccess(CustomModel model) {
                File modelFile = model.getFile();
                Log.e("modelFile : ", modelFile + "");
                if (modelFile != null) {
                    Interpreter interpreter = new Interpreter(modelFile);
                    int bufferSize = 1 * java.lang.Float.SIZE / java.lang.Byte.SIZE;
                    ByteBuffer modelOutput = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder());
                    interpreter.run(input, modelOutput);
                    modelOutput.rewind();
                    FloatBuffer probabilities = modelOutput.asFloatBuffer();
                    prop = probabilities.get(0);
                    Log.e("MAX : ", prop * 100 + "");
                }
            }
        });
    }
    public static String getKeyboardLanguage(String s) {
        for (int i = 0; i < s.length();) {
            int c = s.codePointAt(i);
            if (c >= 0x0600 && c <= 0x06E0)
                return "AR";
            i += Character.charCount(c);
        }
        return "EN";
    }


    public class AsyncTaskModel extends AsyncTask<String, String, String> {

        ChatBotModel chatBotModel;
        int pos;
        public AsyncTaskModel(ChatBotModel chatBotModel,int pos) {
           this.chatBotModel = chatBotModel;
           this.pos = pos;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (action.equals("chatbot")) {
                showDialogPython = new ShowDialogPython(BotActivity.this, BotActivity.this.getLayoutInflater(),"typing");
            }
        }

        @Override
        protected String doInBackground(String... f_url) {
            if (!chatBotModel.getText().isEmpty()) {
                modelFire(chatBotModel.getText());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            AsyncTaskD asyncTaskD = new AsyncTaskD(chatBotModel, pos);
            asyncTaskD.execute();
        }
    }

}