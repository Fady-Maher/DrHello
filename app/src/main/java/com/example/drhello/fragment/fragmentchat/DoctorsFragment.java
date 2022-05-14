package com.example.drhello.fragment.fragmentchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.drhello.R;
import com.example.drhello.adapter.FriendsAdapter;
import com.example.drhello.adapter.OnFriendsClickListener;
import com.example.drhello.adapter.UserStateAdapter;
import com.example.drhello.firebaseinterface.MyCallBackChats;
import com.example.drhello.firebaseinterface.MyCallBackListenerComments;
import com.example.drhello.firebaseinterface.MyCallbackUser;
import com.example.drhello.fragment.ChatFragment;
import com.example.drhello.model.AddPersonModel;
import com.example.drhello.model.ChatModel;
import com.example.drhello.model.LastChat;
import com.example.drhello.model.UserAccount;
import com.example.drhello.model.UserState;
import com.example.drhello.ui.chats.ChatActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class DoctorsFragment extends Fragment implements OnFriendsClickListener {
    private RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ArrayList<LastChat> userAccountArrayList = new ArrayList<>();
    public static ProgressDialog mProgress;
    private UserAccount userAccount;
    public DoctorsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("onCreate:","DoctorsFragment");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_doctors, container, false);
        recyclerView = view.findViewById(R.id.rec_view);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mProgress = new ProgressDialog(getActivity());

        readData(new MyCallbackUser() {
            @Override
            public void onCallback(DocumentSnapshot documentSnapshot) {
                if(!documentSnapshot.exists()){
                    FirebaseAuth.getInstance().getCurrentUser().delete();
                }else{
                    userAccount = documentSnapshot.toObject(UserAccount.class);
                }
                mProgress.dismiss();
            }
        });

        readDataChatsListener(new MyCallBackChats() {
            @Override
            public void onCallBack(DocumentSnapshot value) {
                userAccount = value.toObject(UserAccount.class);
                Log.e("userAccount1 : ", userAccount.getName());
                for (Map.Entry<String, AddPersonModel> entry : userAccount.getFriendsmap().entrySet()) {
                    LastChat lastChat = new LastChat();
                    lastChat.setImage_person(entry.getValue().getImage_person());
                    lastChat.setNameSender(entry.getValue().getName_person());
                    lastChat.setIdFriend(entry.getValue().getId());
                    if(userAccount.getMap().containsKey(entry.getKey())){
                        lastChat.setMessage(userAccount.getMap().get(entry.getKey()).getMessage());
                        lastChat.setDate(userAccount.getMap().get(entry.getKey()).getDate());
                        Log.e("getMessage : ", userAccount.getMap().get(entry.getKey()).getMessage());

                    }else{
                        lastChat.setMessage("");
                        lastChat.setDate("");
                        Log.e("getMessage : ","getMessage()");

                    }
                    if(entry.getValue().getType().equals("Doctor")){
                        userAccountArrayList.add(lastChat);
                    }
                }

                FriendsAdapter adapter = new FriendsAdapter(getActivity(),
                        userAccountArrayList, DoctorsFragment.this, userAccount);
                recyclerView.setAdapter(adapter);
            }
        });

        return view;
    }

    public void readDataChatsListener(MyCallBackChats myCallback) {
        db.collection("users").document(mAuth.getCurrentUser().getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        Log.e("task : ", " tast");
                        if (mAuth.getCurrentUser() != null) {
                            userAccountArrayList.clear();
                            myCallback.onCallBack(value);
                        }
                    }
                });
    }

    public void readData(MyCallbackUser myCallback) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            mProgress.setMessage("Loading..");
            mProgress.setCancelable(false);
            mProgress.show();
            FirebaseFirestore.getInstance().collection("users")
                    .document(currentUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    myCallback.onCallback(documentSnapshot);
                }
            });
        }
    }

    @Override
    public void onClick(LastChat lastChat) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("friendAccount", lastChat.getIdFriend());
        intent.putExtra("userAccount", userAccount);
        ChatModel chatModel = (ChatModel) getActivity().getIntent().getSerializableExtra("message");
        if (chatModel != null) {
            Log.e("getActivity:", chatModel.getMessage());
            intent.putExtra("message", chatModel);
        }
        getActivity().startActivity(intent);
    }
}