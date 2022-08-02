package com.example.drhello.ui.profile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.example.drhello.R;
import com.example.drhello.adapter.DoctorAdapter;
import com.example.drhello.adapter.OnDoctorsClickLinstener;
import com.example.drhello.model.UserAccount;
import com.example.drhello.ui.chats.ChatActivity;

import java.util.ArrayList;

public class DoctorsActivity extends AppCompatActivity implements OnDoctorsClickLinstener {
    private DoctorAdapter doctorAdapter;
    private RecyclerView rec_view;
    private ArrayList<UserAccount> doctorArrayList = new ArrayList<>();
    private ArrayList<UserAccount> doctorArrayListAdapter = new ArrayList<>();
    private String spec;
    private static final int REQUEST_CODE = 1;
    int position_click = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            getWindow().setStatusBarColor(Color.WHITE);
        }

        rec_view = findViewById(R.id.rec_view);
        if(getIntent().getStringExtra("spec") != null){
            spec = getIntent().getStringExtra("spec");
        }

        if(getIntent().getSerializableExtra("doctors") != null){
            doctorArrayList = (ArrayList<UserAccount>) getIntent().getSerializableExtra("doctors");
            for(int i = 0 ; i < doctorArrayList.size() ; i++){
                if(doctorArrayList.get(i).getUserInformation().getSpecification().equals(spec)){
                    doctorArrayListAdapter.add(doctorArrayList.get(i));
                }
            }

            doctorAdapter = new DoctorAdapter(DoctorsActivity.this, doctorArrayListAdapter, DoctorsActivity.this);
            rec_view.setAdapter(doctorAdapter);
        }
    }

    @Override
    public void OnClickCall(int position,String phone) {
        position_click = position;
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DoctorsActivity.this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    REQUEST_CODE);
            Log.e("PERS","PERS");
        } else {
            // else block means user has already accepted.And make your phone call here.
            String uri = "tel:" + doctorArrayListAdapter.get(position).getUserInformation().getPhone();
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse(uri));
            startActivity(intent);
            Log.e("PERS","intent");
        }
    }

    @Override
    public void OnClickChat(int position,UserAccount userAccount) {
        Intent intent = new Intent(DoctorsActivity.this, ChatActivity.class);
        intent.putExtra("friendAccount", userAccount.getId());
        //intent.putExtra("userAccount", userAccount);
        startActivity(intent);
    }

    @Override
    public void OnClickPlace(int position,String location) {
        String lat = location.substring(10).split(",")[0];
        String lon = location.substring(10).split(",")[1].replace(")","");
        String geoUri = "http://www.google.com/maps/place/" + lat + "," + lon + "";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE && resultCode==RESULT_OK){
            String uri = "tel:" + doctorArrayListAdapter.get(position_click).getUserInformation().getPhone();
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse(uri));
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            // for each permission check if the user granted/denied them
            // you may want to group the rationale in a single dialog,
            // this is just an example
            for (int i = 0, len = permissions.length; i < len; i++) {
                String permission = permissions[i];
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    // user rejected the permission
                    boolean showRationale = false;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        showRationale = shouldShowRequestPermissionRationale(permission);
                    }
                    if (!showRationale) {
                        // user also CHECKED "never ask again"
                        // you can either enable some fall back,
                        // disable features of your app
                        // or open another dialog explaining
                        // again the permission and directing to
                        // the app setting
                        Log.e("PERS","setting");
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_CODE);
                    } else if (Manifest.permission.WRITE_CONTACTS.equals(permission)) {
                        //showRationale(permission, R.string.permission_denied_contacts);
                        // user did NOT check "never ask again"
                        // this is a good place to explain the user
                        // why you need the permission and ask if he wants
                        // to accept it (the rationale)
                        Log.e("PERS","rationale");

                    }
                }
            }
        }
    }

}