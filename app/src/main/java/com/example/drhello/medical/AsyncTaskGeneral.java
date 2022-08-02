package com.example.drhello.medical;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.drhello.R;
import com.example.drhello.databinding.ActivityBrainBinding;
import com.example.drhello.databinding.ActivityChestBinding;
import com.example.drhello.databinding.ActivityHeartBinding;
import com.example.drhello.databinding.ActivityOpticalBinding;
import com.example.drhello.databinding.ActivitySkinBinding;
import com.example.drhello.other.ShowDialogPython;


public class AsyncTaskGeneral extends AsyncTask<String, String, String> {
    String path, type, action,error_message="";
    String[] prop;
    int prediction;
    ShowDialogPython showDialogPython;
    PyObject main_program;
    ActivityChestBinding activityChestBinding;
    ChestActivity chestActivity;

    ActivityBrainBinding activityBrainBinding;
    BrainActivity brainActivity;

    HeartActivity heartActivity;
    ActivityHeartBinding activityHeartBinding;

    OpticalActivity opticalActivity;
    ActivityOpticalBinding activityOpticalBinding;

    SkinActivity skinActivity;
    ActivitySkinBinding activitySkinBinding;

    public AsyncTaskGeneral(
            String path, String action, String type,
            ActivityChestBinding activityChestBinding,
            ChestActivity chestActivity,

            ActivityBrainBinding activityBrainBinding,
            BrainActivity brainActivity,

            HeartActivity heartActivity,
            ActivityHeartBinding activityHeartBinding,

            OpticalActivity opticalActivity,
            ActivityOpticalBinding activityOpticalBinding,

            SkinActivity skinActivity,
            ActivitySkinBinding activitySkinBinding
    ) {
        this.path = path;
        this.action = action;
        this.type = type;
        this.activityChestBinding = activityChestBinding;
        this.chestActivity = chestActivity;
        this.activityBrainBinding = activityBrainBinding;
        this.brainActivity = brainActivity;
        this.heartActivity = heartActivity;
        this.activityHeartBinding = activityHeartBinding;
        this.opticalActivity = opticalActivity;
        this.activityOpticalBinding = activityOpticalBinding;
        this.skinActivity = skinActivity;
        this.activitySkinBinding = activitySkinBinding;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (type.equals("skin")) {
            showDialogPython = new ShowDialogPython(skinActivity, skinActivity.getLayoutInflater(), "load");
        } else if (type.equals("heart")) {
            showDialogPython = new ShowDialogPython(heartActivity, heartActivity.getLayoutInflater(), "load");
        } else if (type.equals("optical")) {
            showDialogPython = new ShowDialogPython(opticalActivity, opticalActivity.getLayoutInflater(), "load");
        } else if (type.equals("corona")) {
            showDialogPython = new ShowDialogPython(chestActivity, chestActivity.getLayoutInflater(), "load");
        } else if (type.equals("brain")) {
            showDialogPython = new ShowDialogPython(brainActivity, brainActivity.getLayoutInflater(), "load");
        }
    }

    @Override
    protected String doInBackground(String... f_url) {
        if (action.equals("first")) {
            if (type.equals("skin")) {
                if (!Python.isStarted()) {
                    Python.start(new AndroidPlatform(skinActivity));//error is here!
                }
            } else if (type.equals("heart")) {
                if (!Python.isStarted()) {
                    Python.start(new AndroidPlatform(heartActivity));//error is here!
                }
            } else if (type.equals("optical")) {
                if (!Python.isStarted()) {
                    Python.start(new AndroidPlatform(opticalActivity));//error is here!
                }
            } else if (type.equals("corona")) {
                if (!Python.isStarted()) {
                    Python.start(new AndroidPlatform(chestActivity));//error is here!
                }
            } else if (type.equals("brain")) {
                if (!Python.isStarted()) {
                    Python.start(new AndroidPlatform(brainActivity));//error is here!
                }
            }
        } else {
            String result = "";
            final Python py = Python.getInstance();
            main_program = py.getModule("prolog");
            result = main_program.callAttr("model", path, "General").toString();
            if(result.equals("error")){
                error_message = result;
                Log.e("error","error");
            }else{
                String[] listResult = result.split("@");
                prediction = Integer.parseInt(listResult[0]);
                String probStr = listResult[1].replace("[", "")
                        .replace("]", "")
                        .replace("\"", "");
                prop = probStr.split(" ");
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String file_url) {
       /* {0:'BrainTumor',
                1:'Chest',
                2:'HeartBeats',
                3:'Other',
                4:'Retinal',
                5:'SkinCancer'}
        */

        showDialogPython.dismissDialog();

        if (!action.equals("first")) {
            if(!error_message.equals("error")){
                Log.e("propBrainTumor: " , prop[0]+"");
                Log.e("propChest: " , prop[1]+"");
                Log.e("propHeartBeats: " , prop[2]+"");
                Log.e("propOther: " , prop[3]+"");
                Log.e("propRetinal: " , prop[4]+"");
                Log.e("propSkinCancer: " , prop[5]+"");

                if (prediction == 0 && type.equals("brain"))  {
                    AsyncTaskDiseases asyncTaskGeneral = new AsyncTaskDiseases(path,"brain",
                            "brain",null,null,
                            activityBrainBinding,brainActivity,null,null,
                            null,null,null,null
                    );
                    asyncTaskGeneral.execute();
                } else if (prediction == 1 && type.equals("corona")) {
                    AsyncTaskDiseases asyncTaskGeneral = new AsyncTaskDiseases(path,"corona",
                            "corona",activityChestBinding,chestActivity,
                            null,null,null,null,
                            null,null,null,null
                    );
                    asyncTaskGeneral.execute();
                } else if (prediction == 2 && type.equals("heart")) {
                    AsyncTaskDiseases asyncTaskGeneral = new AsyncTaskDiseases(path,"heart",
                            "heart",null,null,
                            null,null,heartActivity,activityHeartBinding,
                            null,null,null,null
                    );
                    asyncTaskGeneral.execute();
                } else if(prediction != 2 && type.equals("heart")){
                    activityHeartBinding.progressunknownheart.setAdProgress((int) (Float.parseFloat(prop[prediction]) * 100));

                }else if(prediction != 1 && type.equals("corona")){
                    activityChestBinding.progressunknownchest.setAdProgress((int) (Float.parseFloat(prop[prediction]) * 100));

                }else if(prediction != 0 && type.equals("brain")){
                    activityBrainBinding.progressunknownbrain.setAdProgress((int) (Float.parseFloat(prop[prediction]) * 100));

                }else if(prediction != 4 && type.equals("optical")){
                    activityOpticalBinding.progressunknownoptical.setAdProgress((int) (Float.parseFloat(prop[prediction]) * 100));

                }else if(prediction != 5 && type.equals("skin")){
                    activitySkinBinding.progressunknownskin.setAdProgress((int) (Float.parseFloat(prop[prediction]) * 100));
                }
                else if (prediction == 4 && type.equals("optical")) {
                    AsyncTaskDiseases asyncTaskGeneral = new AsyncTaskDiseases(path,"optical",
                            "optical",null,null,
                            null,null,null,null,
                            opticalActivity,activityOpticalBinding,null,null
                    );
                    asyncTaskGeneral.execute();
                } else if (prediction == 5 && type.equals("skin")) {
                    AsyncTaskDiseases asyncTaskGeneral = new AsyncTaskDiseases(path,"skin",
                            "skin",null,null,
                            null,null,null,null,
                            null,null,skinActivity,activitySkinBinding
                    );
                    asyncTaskGeneral.execute();
                }
            }else{
                showDialogError(type,chestActivity,brainActivity
                ,heartActivity,opticalActivity,skinActivity);
            }
        }
    }

    private void showDialogError(String type,
                                  ChestActivity chestActivity,
                                  BrainActivity brainActivity,
                                  HeartActivity heartActivity,
                                  OpticalActivity opticalActivity,
                                  SkinActivity skinActivity) {
        AlertDialog.Builder dialogBuilder = null;
        LayoutInflater inflater = null;
        
        if (type.equals("brain"))  {
             dialogBuilder = new AlertDialog.Builder(brainActivity);
             inflater = brainActivity.getLayoutInflater();
        } else if (type.equals("corona")) {
             dialogBuilder = new AlertDialog.Builder(chestActivity);
             inflater = chestActivity.getLayoutInflater(); 
        } else if (type.equals("heart")) {
             dialogBuilder = new AlertDialog.Builder(heartActivity);
             inflater = heartActivity.getLayoutInflater();        }
        else if (type.equals("optical")) {
             dialogBuilder = new AlertDialog.Builder(opticalActivity);
             inflater = opticalActivity.getLayoutInflater();
        } else if (type.equals("skin")) {
             dialogBuilder = new AlertDialog.Builder(skinActivity);
             inflater = skinActivity.getLayoutInflater();
        }
        
        
        View dialogView = inflater.inflate(R.layout.alertdialogerrormedical, null);
        dialogBuilder.setView(dialogView);
        Button btn_enter = dialogView.findViewById(R.id.btn_enter);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
        btn_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }
}
