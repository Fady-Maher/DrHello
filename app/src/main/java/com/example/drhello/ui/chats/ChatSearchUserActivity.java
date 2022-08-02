package com.example.drhello.ui.chats;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.drhello.R;
import com.example.drhello.other.ShowDialogPython;
import com.example.drhello.adapter.OnFriendsClickListener;
import com.example.drhello.adapter.SearchUserAdapter;
import com.example.drhello.databinding.ActivityChatSearchUserBinding;
import com.example.drhello.firebaseinterface.MyCallBackChats;
import com.example.drhello.firebaseinterface.MyCallbackUser;
import com.example.drhello.model.AddPersonModel;
import com.example.drhello.model.ChatModel;
import com.example.drhello.model.LastChat;
import com.example.drhello.model.UserAccount;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Map;

public class ChatSearchUserActivity extends AppCompatActivity  implements OnFriendsClickListener {
    ActivityChatSearchUserBinding activityChatSearchUserBinding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private UserAccount userAccount;
    private ArrayList<LastChat> userAccountArrayList = new ArrayList<>() , accountsSearch = new ArrayList<>();
    ShowDialogPython showDialogPython;
    private  androidx.appcompat.widget.SearchView searchView;
    SearchUserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat_search_user);
        activityChatSearchUserBinding = DataBindingUtil.setContentView(ChatSearchUserActivity.this, R.layout.activity_chat_search_user);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        setSupportActionBar(activityChatSearchUserBinding.toolbarCo);
        activityChatSearchUserBinding.backAddPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        readData(new MyCallbackUser() {
            @Override
            public void onCallback(DocumentSnapshot documentSnapshot) {
                if(!documentSnapshot.exists()){
                    FirebaseAuth.getInstance().getCurrentUser().delete();
                }else{
                    userAccount = documentSnapshot.toObject(UserAccount.class);
                }
                showDialogPython.dismissDialog();
            }
        });

        setData();
    }

    private void setData(){
        readDataChatsListener(new MyCallBackChats() {
            @Override
            public void onCallBack(DocumentSnapshot value) {
                userAccountArrayList.clear();
                accountsSearch.clear();
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
                    userAccountArrayList.add(lastChat);
                }

                adapter = new SearchUserAdapter(ChatSearchUserActivity.this,
                        userAccountArrayList, ChatSearchUserActivity.this);
                activityChatSearchUserBinding.recView.setAdapter(adapter);
            }
        });
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
            showDialogPython = new ShowDialogPython(ChatSearchUserActivity.this,ChatSearchUserActivity.this.getLayoutInflater(),"load");
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
        Intent intent = new Intent(ChatSearchUserActivity.this, ChatActivity.class);
        intent.putExtra("friendAccount", lastChat.getIdFriend());
        intent.putExtra("userAccount", userAccount);
        intent.putExtra("typeactivity", "Search");

        ChatModel chatModel = (ChatModel) getIntent().getSerializableExtra("message");
        if (chatModel != null) {
            Log.e("getActivity:", chatModel.getMessage());
            intent.putExtra("message", chatModel);
        }
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        StateOfUser stateOfUser = new StateOfUser();
        stateOfUser.changeState("Online");
    }

    @Override
    public void onPause() {
        super.onPause();
        StateOfUser stateOfUser = new StateOfUser();
        stateOfUser.changeState("Offline");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_btn, menu);

        MenuItem item = menu.findItem(R.id.search_people);
        searchView = (androidx.appcompat.widget.SearchView) MenuItemCompat.getActionView(item);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("Search");


        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                setData();
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                accountsSearch.clear();
                if (newText.isEmpty()){
                    adapter.setDate(accountsSearch);
                    return false;
                }

                for(LastChat d : userAccountArrayList){
                    if(d.getNameSender() != null && d.getNameSender().toLowerCase().contains(newText.toLowerCase())){
                        Log.e("UserName : ",d.getNameSender());
                        accountsSearch.add(d);
                    }
                }

                if (accountsSearch.size()!=0){
                    adapter.setDate(accountsSearch);
                }

                return false;
            }
        });

        return true;


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}