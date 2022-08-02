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


public class AsyncTaskDiseases extends AsyncTask<String, String, String> {
    String path, type, action, error_message = "";
    String[] prop;
    ShowDialogPython showDialogPython;
    PyObject main_program;
    int prediction;
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

    public AsyncTaskDiseases(
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
            if (type.equals("skin")) {
                result = main_program.callAttr("model", path, "Skin").toString();
            } else if (type.equals("heart")) {
                result = main_program.callAttr("model", path, "Heart").toString();
            } else if (type.equals("optical")) {
                result = main_program.callAttr("model", path, "Optical").toString();
            } else if (type.equals("corona")) {
                result = main_program.callAttr("model", path, "Corona").toString();
            } else if (type.equals("brain")) {
                result = main_program.callAttr("model", path, "Brain").toString();
            }
            if (result.equals("error")) {
                error_message = result;
                Log.e("error_message", "error");
            } else {
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
        if (!action.equals("first")) {
            if (!error_message.equals("error")) {
                Log.e("error_message", "FALSE");
                String[] res = new String[prop.length];
                int k = 0;
                for(int i = 0 ; i < prop.length; i ++){
                    if(!prop[i].equals("")){
                        res[k] = prop[i];
                        k = k +1 ;
                    }

                }
                if (type.equals("skin")) {
                    activitySkinBinding.progressactinic.setAdProgress((int) (Float.parseFloat(res[0]) * 100));
                    activitySkinBinding.progressbasal.setAdProgress((int) (Float.parseFloat(res[1]) * 100));
                    activitySkinBinding.progressbenign.setAdProgress((int) (Float.parseFloat(res[2]) * 100));
                    activitySkinBinding.progressderma.setAdProgress((int) (Float.parseFloat(res[3]) * 100));
                    activitySkinBinding.progressmelan.setAdProgress((int) (Float.parseFloat(res[4]) * 100));
                    activitySkinBinding.progressmelanoma.setAdProgress((int) (Float.parseFloat(res[5]) * 100));
                    activitySkinBinding.progressvascular.setAdProgress((int) (Float.parseFloat(res[6]) * 100));
                    activitySkinBinding.txtPrediction.setText(prediction + "");

                    activitySkinBinding.txtGo.setEnabled(true);
                } else if (type.equals("heart")) {
                    activityHeartBinding.progressfusion.setAdProgress((int) (Float.parseFloat(res[0]) * 100));
                    activityHeartBinding.progressnormal.setAdProgress((int) (Float.parseFloat(res[1]) * 100));
                    activityHeartBinding.progresssup.setAdProgress((int) (Float.parseFloat(res[2]) * 100));
                    activityHeartBinding.progressun.setAdProgress((int) (Float.parseFloat(res[3]) * 100));
                    activityHeartBinding.progressvent.setAdProgress((int) (Float.parseFloat(res[4]) * 100));
                    activityHeartBinding.txtPrediction.setText(prediction + "");

                    activityHeartBinding.txtGo.setEnabled(true);
                } else if (type.equals("optical")) {
                    activityOpticalBinding.progresscnv.setAdProgress((int) (Float.parseFloat(res[0]) * 100));
                    activityOpticalBinding.progressdru.setAdProgress((int) (Float.parseFloat(res[1]) * 100));
                    activityOpticalBinding.progresssdmi.setAdProgress((int) (Float.parseFloat(res[2]) * 100));
                    activityOpticalBinding.progressnormal.setAdProgress((int) (Float.parseFloat(res[3]) * 100));
                    activityOpticalBinding.txtPrediction.setText(prediction + "");
                    activityOpticalBinding.txtGo.setEnabled(true);
                } else if (type.equals("corona")) {
                    activityChestBinding.progresscovid.setAdProgress((int) (Float.parseFloat(res[0]) * 100));
                    activityChestBinding.progresslung.setAdProgress((int) (Float.parseFloat(res[1]) * 100));
                    activityChestBinding.progressnormal.setAdProgress((int) (Float.parseFloat(res[2]) * 100));
                    activityChestBinding.progresspneu.setAdProgress((int) (Float.parseFloat(res[3]) * 100));
                    activityChestBinding.txtPrediction.setText(prediction + "");
                    activityChestBinding.txtGo.setEnabled(true);
                } else if (type.equals("brain")) {
                    activityBrainBinding.progressglioma.setAdProgress((int) (Float.parseFloat(res[0]) * 100));
                    activityBrainBinding.progressmen.setAdProgress((int) (Float.parseFloat(res[1]) * 100));
                    activityBrainBinding.progressno.setAdProgress((int) (Float.parseFloat(res[2]) * 100));
                    activityBrainBinding.progresspit.setAdProgress((int) (Float.parseFloat(res[3]) * 100));
                    activityBrainBinding.txtPrediction.setText(prediction + "");
                    activityBrainBinding.txtGo.setEnabled(true);
                }
                showDialogPython.dismissDialog();
            } else {
                Log.e("error_message", "TRUE");
                showDialogPython.dismissDialog();
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
        Log.e("showDialogError", "error");
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

    public int getprediction() {
        return prediction;
    }
}
