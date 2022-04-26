package com.example.drhello.fragment;

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
import com.example.drhello.ui.chats.AddPersonActivity;
import com.example.drhello.model.LastChat;
import com.example.drhello.firebaseinterface.MyCallBackChats;
import com.example.drhello.firebaseinterface.MyCallBackListenerComments;
import com.example.drhello.firebaseinterface.MyCallbackUser;
import com.example.drhello.model.AddPersonModel;
import com.example.drhello.ui.chats.ChatActivity;
import com.example.drhello.adapter.FriendsAdapter;
import com.example.drhello.R;
import com.example.drhello.adapter.OnFriendsClickListener;
import com.example.drhello.adapter.UserStateAdapter;
import com.example.drhello.model.ChatModel;
import com.example.drhello.model.UserState;
import com.example.drhello.model.UserAccount;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatFragment extends Fragment implements OnFriendsClickListener {

    private RecyclerView recyclerView, recyclerView_state;
    private ArrayList<UserState> userStates = new ArrayList<>();
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ArrayList<LastChat> userAccountArrayList = new ArrayList<>();
    private UserAccount userAccount1;
    private FloatingActionButton add_user;
    Map<String, AddPersonModel> mapFriend = new HashMap<>();
    private CircleImageView img_cur_user;
    private UserAccount userAccount;
    public static ProgressDialog mProgress;
    public ChatFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = view.findViewById(R.id.rec_view);
        recyclerView_state = view.findViewById(R.id.recycle_users);
        add_user = view.findViewById(R.id.add_user);
        img_cur_user = view.findViewById(R.id.img_cur_user);
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

                    try{
                        Glide.with(getActivity()).load(userAccount.getImg_profile()).placeholder(R.drawable.user).
                                error(R.drawable.user).into(img_cur_user);
                    }catch (Exception e){
                        img_cur_user.setImageResource(R.drawable.user);
                    }

                    readDataUsersListener(new MyCallBackListenerComments() {
                        @Override
                        public void onCallBack(QuerySnapshot value) {
                            for (DocumentSnapshot document : value.getDocuments()) {
                                UserAccount friendAccount = document.toObject(UserAccount.class);
                                Log.e("online:","statues");
                                if(userAccount.getFriendsmap().containsKey(friendAccount.getId())){
                                    Log.e("online:","mapFriend");
                                    UserState userState = new UserState(friendAccount.getImg_profile(),
                                            friendAccount.getState(),friendAccount.getName());
                                    userStates.add(userState);
                                }
                            }
                            UserStateAdapter userStateAdapter = new UserStateAdapter(getActivity(), userStates);
                            recyclerView_state.setAdapter(userStateAdapter);
                        }
                    });
                }
                mProgress.dismiss();
            }
        });

        readDataChatsListener(new MyCallBackChats() {
            @Override
            public void onCallBack(DocumentSnapshot value) {
                userAccount1 = value.toObject(UserAccount.class);
                Log.e("userAccount1 : ", userAccount1.getName());
                for (Map.Entry<String, AddPersonModel> entry : userAccount1.getFriendsmap().entrySet()) {
                    LastChat lastChat = new LastChat();
                    lastChat.setImage_person(entry.getValue().getImage_person());
                    lastChat.setNameSender(entry.getValue().getName_person());
                    lastChat.setIdFriend(entry.getValue().getId());
                    if(userAccount1.getMap().containsKey(entry.getKey())){
                        lastChat.setMessage(userAccount1.getMap().get(entry.getKey()).getMessage());
                        lastChat.setDate(userAccount1.getMap().get(entry.getKey()).getDate());
                        Log.e("getMessage : ", userAccount1.getMap().get(entry.getKey()).getMessage());

                    }else{
                        lastChat.setMessage("");
                        lastChat.setDate("");
                        Log.e("getMessage : ","getMessage()");

                    }
                    userAccountArrayList.add(lastChat);
                }

                FriendsAdapter adapter = new FriendsAdapter(getActivity(),
                        userAccountArrayList, ChatFragment.this, userAccount1);
                recyclerView.setAdapter(adapter);
            }
        });



        add_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddPersonActivity.class);
                intent.putExtra("userAccount",userAccount1);
                startActivity(intent);
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

    public void readDataUsersListener(MyCallBackListenerComments myCallback) {
        mProgress.setMessage("Loading..");
        mProgress.setCancelable(false);
        mProgress.show();

        db.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (mAuth.getCurrentUser() != null) {
                    userStates.clear();
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
            intent.putExtra("userAccount", userAccount1);
            ChatModel chatModel = (ChatModel) getActivity().getIntent().getSerializableExtra("message");
            if (chatModel != null) {
                Log.e("getActivity:", chatModel.getMessage());
                intent.putExtra("message", chatModel);
            }
            getActivity().startActivity(intent);
    }
}