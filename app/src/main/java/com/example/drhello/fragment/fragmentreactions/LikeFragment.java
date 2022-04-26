package com.example.drhello.fragment.fragmentreactions;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.drhello.R;
import com.example.drhello.adapter.NumReactionAdapter;
import com.example.drhello.model.ReactionModel;
import com.example.drhello.model.UserAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;


public class LikeFragment extends Fragment {


    private RecyclerView recyclerView;
    private Map<String,String> map;
    private ArrayList<UserAccount> userAccountArrayList = new ArrayList<>();
    private FirebaseFirestore db= FirebaseFirestore.getInstance();
    private ArrayList<ReactionModel> reactionModelArrayList = new ArrayList<>() ;
    private NumReactionAdapter numReactionAdapter;
    private ArrayList<UserAccount> userAccountArrayListAdapter= new ArrayList<>();
    private UserAccount userAccountme;
    private FirebaseAuth mAuth;
    public LikeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            map = (Map<String, String>) getArguments().getSerializable("post");
            userAccountArrayList = (ArrayList<UserAccount>) getArguments().getSerializable("userAccountArrayList");
            Log.e("logcata:",userAccountArrayList.size()+"");           }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_like, container, false);
        recyclerView=view.findViewById(R.id.rec_view);
        mAuth = FirebaseAuth.getInstance();
        reactionModelArrayList.clear();
        userAccountArrayListAdapter.clear();
        for(int i= 0 ;i<userAccountArrayList.size();i++){
            if(userAccountArrayList.get(i).getId().equals(mAuth.getCurrentUser().getUid())){
                userAccountme = userAccountArrayList.get(i);
            }
            if(map.containsKey(userAccountArrayList.get(i).getId())){
                if (map.get(userAccountArrayList.get(i).getId()).equals("Like")){
                    ReactionModel reactionModel = new ReactionModel();
                    reactionModel.setReaction(map.get(userAccountArrayList.get(i).getId()));
                    reactionModel.setImg_user(userAccountArrayList.get(i).getImg_profile());
                    reactionModel.setName_user(userAccountArrayList.get(i).getName());
                    reactionModelArrayList.add(reactionModel);
                    userAccountArrayListAdapter.add(userAccountArrayList.get(i));

                }

            }
        }
        numReactionAdapter = new NumReactionAdapter(getActivity(), reactionModelArrayList);
        recyclerView.setAdapter(numReactionAdapter);
        return view;
    }

}