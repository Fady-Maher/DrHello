package com.example.drhello.connectionnewtwork;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class NetworkChangeListener extends BroadcastReceiver {
    boolean flag = false, flag2 = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        int status = CheckNetwork.getConnectivityStatusString(context);


        Log.e(" network reciever", "network reciever  " + status);
        if (status == CheckNetwork.NETWORK_STATUS_NOT_CONNECTED && !flag) {
            flag = true;
            flag2 = true;
            Log.e("network : ", "false");
            Toast.makeText(context, "Please, Check Your Network", Toast.LENGTH_SHORT).show();
        } else if (status == CheckNetwork.NETWORK_STATUS_WIFI && flag2) {
            flag = false;
            flag2 = false;
            Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show();
            return;
        }
    }


}


