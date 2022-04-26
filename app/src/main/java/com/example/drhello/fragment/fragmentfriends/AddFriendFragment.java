package com.example.drhello.fragment.fragmentfriends;

import static com.facebook.FacebookSdk.getApplicationContext;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.drhello.ui.chats.StateOfUser;
import com.example.drhello.firebaseinterface.MyCallBackAddFriend;
import com.example.drhello.firebaseservice.FcmNotificationsSender;
import com.example.drhello.model.AddPersonModel;
import com.example.drhello.adapter.OnClickAddPersonListener;
import com.example.drhello.R;
import com.example.drhello.adapter.AddPersonAdapter;
import com.example.drhello.model.UserAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class AddFriendFragment extends Fragment  implements OnClickAddPersonListener {

    private AddPersonAdapter addPersonAdapter;
    private ArrayList<UserAccount> addPersonModelArrayList = new ArrayList<>(),accountsSearch=new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private RecyclerView rec_view;
    private SwipeRefreshLayout swipeRefreshLayout;
    private UserAccount userAccountme;
    private  androidx.appcompat.widget.SearchView searchView;
    public static ProgressDialog mProgress;

    public AddFriendFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mProgress = new ProgressDialog(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_add_friend, container, false);

        rec_view = view.findViewById(R.id.rec_view);

        setData();
        Log.e("onCrview :" , "onCrview");

        return view;
    }

    private void setData() {
        db.collection("users").whereEqualTo("id",mAuth.getCurrentUser().getUid()).
                addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                addPersonModelArrayList.clear();
                if(mAuth.getCurrentUser() != null){
                    for (DocumentSnapshot document1 : value.getDocuments()) {
                        userAccountme = document1.toObject(UserAccount.class);
                        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    addPersonModelArrayList.clear();
                                    for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                        if (document.exists()) {
                                            UserAccount userAccount = document.toObject(UserAccount.class);
                                            Log.e("document :" , userAccount.getName()+"");

                                            if (!userAccount.getId().equals(mAuth.getCurrentUser().getUid())
                                                    && !userAccountme.getFriendsmap().containsKey(userAccount.getId())
                                                    &&!userAccountme.getRequests().containsKey(userAccount.getId())) {
                                                addPersonModelArrayList.add(userAccount);
                                                Log.e("seArrayLi :" , userAccount.getName()+"");
                                            }
                                        }
                                    }
                                    addPersonAdapter = new AddPersonAdapter(getActivity(), addPersonModelArrayList, AddFriendFragment.this,userAccountme);
                                    addPersonAdapter.notifyDataSetChanged();
                                    rec_view.setAdapter(addPersonAdapter);
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    public void onClick(UserAccount friendsAccount,String state) {
        mProgress.setMessage("Uploading..");
        mProgress.setCancelable(false);
        mProgress.show();
        if (state.equals("add")){
            Map<String, AddPersonModel> friends = friendsAccount.getRequests();
            friends.put(userAccountme.getId(), new AddPersonModel(userAccountme.getName(), userAccountme.getImg_profile(), userAccountme.getId()));
            friendsAccount.setRequests(friends);

            Map<String, AddPersonModel> requestsSent = userAccountme.getRequestSsent();
            requestsSent.put(friendsAccount.getId(), new AddPersonModel(friendsAccount.getName(), friendsAccount.getImg_profile(), friendsAccount.getId()));
            userAccountme.setRequestSsent(requestsSent);

            readDataAddFriendListener(new MyCallBackAddFriend() {
                @Override
                public void onCallBack(Task<Void> task) {
                    if(task.isSuccessful()){
                        readDataAddMeListener(new MyCallBackAddFriend() {
                            @Override
                            public void onCallBack(Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(getActivity(), "Requests successful ", Toast.LENGTH_SHORT).show();
                                    Log.e("getTokenID() : " , friendsAccount.getTokenID());
                                    FcmNotificationsSender fcmNotificationsSender = new FcmNotificationsSender(friendsAccount.getTokenID(),
                                            mAuth.getCurrentUser().getUid(),
                                            "Request",
                                            userAccountme.getName() + " Sent Friend Request ",
                                            getApplicationContext(),
                                            getActivity(),
                                            userAccountme.getImg_profile());
                                    fcmNotificationsSender.SendNotifications();
                                    mProgress.dismiss();
                                }
                            }
                        });
                    }
                }
            },friendsAccount);

        }else{

            Map<String, AddPersonModel> requests = friendsAccount.getRequests();
            requests.remove(userAccountme.getId());

            Map<String, AddPersonModel> requestsSent = userAccountme.getRequestSsent();
            requestsSent.remove( friendsAccount.getId());

            friendsAccount.setRequests(requests);
            userAccountme.setRequestSsent(requestsSent);

            readDataCancelFriendListener(new MyCallBackAddFriend() {
                @Override
                public void onCallBack(Task<Void> task) {
                    if(task.isSuccessful()){
                        readDataCancelMeListener(new MyCallBackAddFriend() {
                            @Override
                            public void onCallBack(Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(getActivity(), "Cancel successful ", Toast.LENGTH_SHORT).show();
                                    mProgress.dismiss();
                                }
                            }
                        });
                    }
                }
            },friendsAccount);

        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(R.menu.search_btn,menu);
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
                if (newText.isEmpty()){
                    accountsSearch.clear();
                    addPersonAdapter = new AddPersonAdapter(getActivity(), accountsSearch, AddFriendFragment.this,userAccountme);
                    rec_view.setAdapter(addPersonAdapter);
                    return false;
                }

                for(UserAccount d : addPersonModelArrayList){
                    if(d.getName() != null && d.getName().toLowerCase().contains(newText.toLowerCase())){
                        Log.e("UserName : ",d.getName());
                        accountsSearch.add(d);
                    }
                }

                if (accountsSearch.size()!=0){
                    addPersonAdapter = new AddPersonAdapter(getActivity(), accountsSearch, AddFriendFragment.this,userAccountme);
                    rec_view.setAdapter(addPersonAdapter);
                }

                return false;
            }
        });

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


    void readDataCancelFriendListener(MyCallBackAddFriend myCallBackAddFriend,UserAccount friendsAccount){
        db.collection("users").
                document(friendsAccount.getId())
                .set(friendsAccount).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                    myCallBackAddFriend.onCallBack(task);
            }
        });
    }

    void readDataCancelMeListener(MyCallBackAddFriend myCallBackAddFriend){
        db.collection("users").
                document(userAccountme.getId()).set(userAccountme).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                myCallBackAddFriend.onCallBack(task);
            }
        });
    }

    void readDataAddFriendListener(MyCallBackAddFriend myCallBackAddFriend,UserAccount friendsAccount){
        db.collection("users").
                document(friendsAccount.getId()).set(friendsAccount).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                myCallBackAddFriend.onCallBack(task);
            }
        });
    }

    void readDataAddMeListener(MyCallBackAddFriend myCallBackAddFriend){
        db.collection("users").
                document(userAccountme.getId()).set(userAccountme).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                myCallBackAddFriend.onCallBack(task);
            }
        });
    }

}