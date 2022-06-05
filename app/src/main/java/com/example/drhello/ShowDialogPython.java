package com.example.drhello;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;

import pl.droidsonroids.gif.GifImageView;

public class ShowDialogPython {
    AlertDialog alertDialog;
    View dialogView;
    public ShowDialogPython(Context context,LayoutInflater inflater,String type){
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
            if(type.equals("load")){
                dialogView = inflater.inflate(R.layout.dialog_loading, null);
            }else if(type.equals("upload")){
                dialogView = inflater.inflate(R.layout.dialog_uploading, null);
            }else if(type.equals("typing")){
                dialogView = inflater.inflate(R.layout.dialog_bot, null);
                GifImageView txt = dialogView.findViewById(R.id.txt);
                txt.setImageResource(R.drawable.typing);
            }else if(type.equals("translate")){
                dialogView = inflater.inflate(R.layout.dialog_bot, null);
                GifImageView txt = dialogView.findViewById(R.id.txt);
                txt.setImageResource(R.drawable.translate);
            }
            dialogBuilder.setView(dialogView);
            alertDialog = dialogBuilder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.setCancelable(false);
            alertDialog.show();
    }

    public void dismissDialog(){
        alertDialog.dismiss();
    }

}
