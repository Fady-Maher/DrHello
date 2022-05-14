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
import com.example.drhello.R;
import com.example.drhello.adapter.RequestsPersonAdapter;
import com.example.drhello.adapter.OnClickRequestsPersonListener;
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


public class RequestsFriendFragment extends Fragment implements OnClickRequestsPersonListener {

    private RequestsPersonAdapter requestsPersonAdapter;
    private ArrayList<UserAccount> userAccountArrayList = new ArrayList<>(), accountsSearch = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private SwipeRefreshLayout swipeRefreshLayout;
    private UserAccount userAccountme;
    private UserAccount userAccount;
    private RecyclerView rec_view;
    private androidx.appcompat.widget.SearchView searchView;
    public static ProgressDialog mProgress;

    public RequestsFriendFragment() {
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_requests_friend, container, false);


        rec_view = view.findViewById(R.id.rec_view);

        setData();
        return view;
    }


    private void setData() {
        db.collection("users").whereEqualTo("id", mAuth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                userAccountArrayList.clear();
                if (mAuth.getCurrentUser() != null) {
                    for (DocumentSnapshot document1 : value.getDocuments()) {
                        userAccountme = document1.toObject(UserAccount.class);
                        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    userAccountArrayList.clear();
                                    for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                        if (document.exists()) {
                                            UserAccount userAccount = document.toObject(UserAccount.class);
                                            //           Log.e("document :", userAccount.getName() + "");

                                            if (userAccountme.getRequests().containsKey(userAccount.getId())) {
                                                userAccountArrayList.add(userAccount);
                                                Log.e("seArrayLi :", userAccount.getName() + "");
                                            }
                                        }
                                    }
                                    requestsPersonAdapter = new RequestsPersonAdapter(getActivity(), userAccountArrayList,
                                            RequestsFriendFragment.this);
                                    rec_view.setAdapter(requestsPersonAdapter);
                                }
                            }
                        });
                    }
                }
            }
        });
    }


    @Override
    public void onClickAccept(UserAccount friend) {
        mProgress.setMessage("Uploading..");
        mProgress.setCancelable(false);
        mProgress.show();
        UserAccount friendsAccountOld = friend;
        Map<String, AddPersonModel> friends = userAccountme.getFriendsmap();
        friends.put(friend.getId(), new AddPersonModel(friend.getName(), friend.getImg_profile(), friend.getId(),friend.getUserInformation().getType()));

        Map<String, AddPersonModel> requests = userAccountme.getRequests();
        requests.remove(friend.getId());

        Map<String, AddPersonModel> requestsSent = friend.getRequestSsent();
        requestsSent.remove(userAccountme.getId());

        Map<String, AddPersonModel> mapFriends = friend.getFriendsmap();
        mapFriends.put(userAccountme.getId(), new AddPersonModel(userAccountme.getName(), userAccountme.getImg_profile(), userAccountme.getId(),userAccountme.getUserInformation().getType()));

        userAccountme.setFriendsmap(friends);
        userAccountme.setRequests(requests);
        friend.setFriendsmap(mapFriends);
        friend.setRequestSsent(requestsSent);

        readDataAddFriendListener(new MyCallBackAddFriend() {
            @Override
            public void onCallBack(Task<Void> task) {
                if(task.isSuccessful()){
                    readDataAddMeListener(new MyCallBackAddFriend() {
                        @Override
                        public void onCallBack(Task<Void> task) {
                            userAccountArrayList.remove(friendsAccountOld);
                            requestsPersonAdapter.notifyDataSetChanged();
                            FcmNotificationsSender fcmNotificationsSender = new FcmNotificationsSender(friendsAccountOld.getTokenID(),
                                    mAuth.getCurrentUser().getUid(),
                                    "Accept",
                                    userAccountme.getName() + " Accept Friend Request ",
                                    getApplicationContext(),
                                    getActivity(),
                                    userAccountme.getImg_profile());
                            fcmNotificationsSender.SendNotifications();
                            Toast.makeText(getActivity(), "Requests successful ", Toast.LENGTH_SHORT).show();
                            mProgress.dismiss();
                        }
                    });
                }
            }
        },friend);
    }

    @Override
    public void onClickDelete(UserAccount friend) {
        mProgress.setMessage("Uploading..");
        mProgress.setCancelable(false);
        mProgress.show();
        UserAccount friendsAccountOld = friend;
        Map<String, AddPersonModel> requests = userAccountme.getRequests();
        requests.remove(friend.getId());

        Map<String, AddPersonModel> requestsSent = friend.getRequestSsent();
        requestsSent.remove(userAccountme.getId());

        userAccountme.setRequests(requests);
        friend.setRequestSsent(requestsSent);

        readDataDeleteFriendListener(new MyCallBackAddFriend() {
            @Override
            public void onCallBack(Task<Void> task) {
                if(task.isSuccessful()){
                    readDataDeleteMeListener(new MyCallBackAddFriend() {
                        @Override
                        public void onCallBack(Task<Void> task) {
                            if(task.isSuccessful()){
                                userAccountArrayList.remove(friendsAccountOld);
                                requestsPersonAdapter.notifyDataSetChanged();
                                Toast.makeText(getActivity(), "Requests successful ", Toast.LENGTH_SHORT).show();
                                mProgress.dismiss();
                            }
                        }
                    });
                }
            }
        },friend,friendsAccountOld);
    }

    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(R.menu.search_btn, menu);
        MenuItem item = menu.findItem(R.id.search_people);
        searchView = (androidx.appcompat.widget.SearchView) MenuItemCompat.getActionView(item);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("Search");


        searchView.setOnCloseListener(new androidx.appcompat.widget.SearchView.OnCloseListener() {
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
                if (newText.isEmpty()) {
                    accountsSearch.clear();
                    requestsPersonAdapter = new RequestsPersonAdapter(getActivity(), accountsSearch,
                            RequestsFriendFragment.this);
                    rec_view.setAdapter(requestsPersonAdapter);
                    return false;
                }

                for (UserAccount d : userAccountArrayList) {
                    if (d.getName() != null && d.getName().toLowerCase().contains(newText.toLowerCase())) {
                        Log.e("UserName : ", d.getName());
                        accountsSearch.add(d);
                    }
                }

                if (accountsSearch.size() != 0) {
                    requestsPersonAdapter = new RequestsPersonAdapter(getActivity(), accountsSearch,
                            RequestsFriendFragment.this);
                    rec_view.setAdapter(requestsPersonAdapter);
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

    void readDataDeleteFriendListener(MyCallBackAddFriend myCallBackAddFriend, UserAccount friend,UserAccount friendsAccountOld){
        db.collection("users").
                document(friend.getId()).set(friend).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                myCallBackAddFriend.onCallBack(task);
            }
        });
    }

    void readDataDeleteMeListener(MyCallBackAddFriend myCallBackAddFriend){
        db.collection("users").
                document(userAccountme.getId()).set(userAccountme).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                myCallBackAddFriend.onCallBack(task);
            }
        });
    }

    void readDataAddFriendListener(MyCallBackAddFriend myCallBackAddFriend,UserAccount friend){
        db.collection("users").
                document(friend.getId()).set(friend).addOnCompleteListener(new OnCompleteListener<Void>() {
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