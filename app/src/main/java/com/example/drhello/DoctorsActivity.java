package com.example.drhello;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.example.drhello.adapter.AddPersonAdapter;
import com.example.drhello.fragment.fragmentfriends.AddFriendFragment;
import com.example.drhello.model.UserAccount;

import java.util.ArrayList;

public class DoctorsActivity extends AppCompatActivity {
    private DoctorAdapter doctorAdapter;
    private RecyclerView rec_view;
    private ArrayList<UserAccount> doctorArrayList = new ArrayList<>();
    private ArrayList<UserAccount> doctorArrayListAdapter = new ArrayList<>();
    private String spec;
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

            doctorAdapter = new DoctorAdapter(DoctorsActivity.this, doctorArrayListAdapter);
            rec_view.setAdapter(doctorAdapter);
        }
    }

}