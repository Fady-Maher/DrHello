package com.example.drhello.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.drhello.adapter.TapChatAdapter;
import com.example.drhello.adapter.TapFriendAdapter;
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
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;


public class ChatFragment extends Fragment {

    private RecyclerView  recyclerView_state;
    private ArrayList<UserState> userStates = new ArrayList<>();
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ArrayList<LastChat> userAccountArrayList = new ArrayList<>();
    private FloatingActionButton add_user;
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





        add_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddPersonActivity.class);
                startActivity(intent);
            }
        });
        /*************************************************/





        TabLayout tabLayout = view.findViewById(R.id.Tab);
        tabLayout.addTab(tabLayout.newTab().setText("Doctors").setIcon(R.drawable.doctor_tab2),0);
        tabLayout.addTab(tabLayout.newTab().setText("Users").setIcon(R.drawable.ic_user),1);
        TapChatAdapter adapter = new TapChatAdapter( getFragmentManager(),
                tabLayout.getTabCount()
        );
        ViewPager view_pager = view.findViewById(R.id.view_pager);
        view_pager.setAdapter(adapter);
        view_pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.e("online:",tab.getPosition()+"");
                view_pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



        return view;
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

}