package com.example.drhello.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import com.bumptech.glide.Glide;
import com.example.drhello.ui.chats.ChatSearchUserActivity;
import com.example.drhello.other.ShowDialogPython;
import com.example.drhello.adapter.OnClickFriendStateLinstener;
import com.example.drhello.fragment.fragmentchat.DoctorsFragment;
import com.example.drhello.fragment.fragmentchat.NormalUsersFragment;
import com.example.drhello.ui.chats.AddPersonActivity;
import com.example.drhello.firebaseinterface.MyCallBackListenerComments;
import com.example.drhello.firebaseinterface.MyCallbackUser;
import com.example.drhello.ui.chats.ChatActivity;
import com.example.drhello.R;
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


public class ChatFragment extends Fragment implements OnClickFriendStateLinstener {

    private RecyclerView  recyclerView_state;
    private ArrayList<UserState> userStates = new ArrayList<>();
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FloatingActionButton add_user;
    private CircleImageView img_cur_user;
    private UserAccount userAccount;
    ShowDialogPython showDialogPython;

    ViewPager view_pager;
    View view;



    private Button btn_search;
    TabLayout tabLayout;
    FrameLayout frameLayout;
    Fragment fragment = null;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    public ChatFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("onCreate: " ,"ChatFragment");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chat, container, false);
        String type = getArguments().getString("type");
        btn_search = view.findViewById(R.id.btn_search);
        Log.e("onCreateView: " ,"FIRST");
        recyclerView_state = view.findViewById(R.id.recycle_users);
        add_user = view.findViewById(R.id.add_user);
        img_cur_user = view.findViewById(R.id.img_cur_user);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

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
                //                Log.e("online:","statues");
                                if(userAccount.getFriendsmap().containsKey(friendAccount.getId())){
                   //                 Log.e("online:","mapFriend");
                                    UserState userState = new UserState(friendAccount.getImg_profile(),
                                            friendAccount.getState(),friendAccount.getName(),friendAccount.getId());
                                    userStates.add(userState);
                                }
                            }
                            UserStateAdapter userStateAdapter = new UserStateAdapter(getActivity(), userStates,
                                    ChatFragment.this);
                            recyclerView_state.setAdapter(userStateAdapter);
                        }
                    });
                }
                showDialogPython.dismissDialog();
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
        frameLayout = view.findViewById(R.id.frameLayout);
        if(type.equals("Normal")){
            Log.e("INTENTChat: " ,type);
            fragment = new NormalUsersFragment();
            TabLayout.Tab tab = tabLayout.getTabAt(1);
            tab.select();
        }else{
            Log.e("INTENTChat: " ,type);
            fragment = new DoctorsFragment();
            TabLayout.Tab tab = tabLayout.getTabAt(0);
            tab.select();
        }

        fragmentManager = getActivity().getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Fragment fragment = null;
                switch (tab.getPosition()) {
                    case 0:
                        fragment = new DoctorsFragment();
                        break;
                    case 1:
                        fragment = new NormalUsersFragment();
                        break;
                }
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.frameLayout, fragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ChatSearchUserActivity.class);
                startActivity(intent);
            }
        });







        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    public void readDataUsersListener(MyCallBackListenerComments myCallback) {
       // showDialogPython = new ShowDialogPython(getActivity(),getActivity().getLayoutInflater(),"load");

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
            showDialogPython = new ShowDialogPython(getActivity(),getActivity().getLayoutInflater(),"load");
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
    public void onClickState(String id) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("friendAccount", id);
        intent.putExtra("userAccount", userAccount);
        ChatModel chatModel = (ChatModel) getActivity().getIntent().getSerializableExtra("message");
        if (chatModel != null) {
            Log.e("getActivity:", chatModel.getMessage());
            intent.putExtra("message", chatModel);
        }
        getActivity().startActivity(intent);
    }
}