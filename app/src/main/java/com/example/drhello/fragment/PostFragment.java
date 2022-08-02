package com.example.drhello.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.drhello.other.ShowDialogPython;
import com.example.drhello.firebaseinterface.MyCallbackDeleteItem;
import com.example.drhello.firebaseinterface.MyCallbackDeletePost;
import com.example.drhello.firebaseinterface.MyCallBackListenerComments;
import com.example.drhello.firebaseinterface.MyCallBackReaction;
import com.example.drhello.firebaseinterface.MyCallbackUser;
import com.example.drhello.ui.profile.ProfileActivity;
import com.example.drhello.ui.writepost.NumReactionActivity;
import com.example.drhello.model.ReactionType;
import com.example.drhello.model.Posts;
import com.example.drhello.R;
import com.example.drhello.ui.writepost.ShowImageActivity;
import com.example.drhello.ui.writecomment.WriteCommentActivity;
import com.example.drhello.ui.writepost.WritePostsActivity;
import com.example.drhello.adapter.OnPostClickListener;
import com.example.drhello.adapter.PostsAdapter;
import com.example.drhello.model.UserAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class PostFragment extends Fragment implements OnPostClickListener {

    private Button btn_write_post;
    ArrayList<Posts> postsArrayList = new ArrayList<>() , postsArrayListSeacrch = new ArrayList<>() ;
    private TextView textView;
    private RecyclerView recycler_posts;
    private PostsAdapter postsAdapter;
    private ArrayList<String> strings = new ArrayList<>();
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    ImageView image_user;
    private UserAccount userAccount;
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.US);
    private  androidx.appcompat.widget.SearchView searchView;

    private int numImages = 0;

    ShowDialogPython showDialogPython;

    public PostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        textView = view.findViewById(R.id.txt_post);
        btn_write_post = view.findViewById(R.id.btn_write_post);
        recycler_posts = view.findViewById(R.id.recycle_posts);
        image_user = view.findViewById(R.id.user_image);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();



        readData(new MyCallbackUser() {
            @Override
            public void onCallback(DocumentSnapshot documentSnapshot) {
                if (!documentSnapshot.exists()) {
                    FirebaseAuth.getInstance().getCurrentUser().delete();
                } else {
                    userAccount = documentSnapshot.toObject(UserAccount.class);
                    try {
                        Glide.with(getActivity()).load(userAccount.getImg_profile()).placeholder(R.drawable.user).
                                error(R.drawable.user).into(image_user);
                    } catch (Exception e) {
                        image_user.setImageResource(R.drawable.user);
                    }
                }
                showDialogPython.dismissDialog();

            }
        });


        btn_write_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), WritePostsActivity.class));
            }
        });

        postsAdapter = new PostsAdapter(getActivity(), postsArrayList,
                PostFragment.this, getActivity().getSupportFragmentManager(),"PostFragment");
        recycler_posts.setAdapter(postsAdapter);

        setData();

        return view;
    }

    private void setData(){
        readDataPostsListener(new MyCallBackListenerComments() {
            @Override
            public void onCallBack(QuerySnapshot value) {
                Log.e("lostart2 : ", postsArrayList.size() + "");
                postsAdapter.notifyDataSetChanged();
                for (DocumentSnapshot document : value.getDocuments()) {
                    Posts singele_posts = document.toObject(Posts.class);
                    postsArrayList.add(singele_posts);
                }

                Collections.sort(postsArrayList, new Comparator<Posts>() {
                    @Override
                    public int compare(Posts lhs, Posts rhs) {
                        try {
                            return dateFormat.parse(rhs.getDate())
                                    .compareTo(dateFormat.parse(lhs.getDate()));
                        } catch (ParseException e) {
                            throw new IllegalArgumentException(e);
                        }
                    }
                });

                postsAdapter.FunPostsAdapter(postsArrayList);
                postsAdapter.notifyDataSetChanged();

            }
        });
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
                    postsArrayListSeacrch.clear();
                    postsAdapter.FunPostsAdapter(postsArrayListSeacrch);
                    postsAdapter.notifyDataSetChanged();
                    return false;
                }
                postsArrayListSeacrch.clear();
                postsAdapter.notifyDataSetChanged();
                for(Posts d : postsArrayList){
                    if(d.getWritePost() != null && d.getWritePost().toLowerCase()
                            .contains(newText.toLowerCase())){
                        Log.e("UserName : ",d.getWritePost());
                        postsArrayListSeacrch.add(d);
                    }
                }

                if (postsArrayListSeacrch.size()!=0){
                    postsAdapter.FunPostsAdapter(postsArrayListSeacrch);
                    postsAdapter.notifyDataSetChanged();
                }

                return false;
            }
        });

    }

    public void readDataPostsListener(MyCallBackListenerComments myCallback) {
        db.collection("posts")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        postsArrayList.clear();
                        if (mAuth.getCurrentUser() != null) {
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
    public void onClickImage(String uri) {
        Intent intent = new Intent(getActivity(), ShowImageActivity.class);
        intent.putExtra("uri_image", uri);
        startActivity(intent);
    }

    @Override
    public void onClickNumReaction(Posts posts) {
        Intent intent = new Intent(getActivity(), NumReactionActivity.class);
        intent.putExtra("post", posts);
        startActivity(intent);
    }

    @Override
    public void onClickComment(Posts posts) {
        Intent intent = new Intent(getActivity(), WriteCommentActivity.class);
        intent.putExtra("post", posts);
        startActivity(intent);
    }

    @Override
    public void onClickShare(Posts posts) {
        String img = "";
        for(int i = 0 ; i < posts.getImgUri().size();i++){
            img += "\n\n" + posts.getImgUri() ;
        }
        String string = "Text : " + posts.getWritePost()  +"\n"+ img;
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT,string);
        startActivity(Intent.createChooser(sendIntent, null));

    }


    @Override
    public void selectedReaction(String reaction, Posts posts) {

        ReactionType reactionType = new ReactionType(reaction, mAuth.getCurrentUser().getUid());
        Log.e("reactionType", reactionType.getReactionType());  // new
        Map<String, String> arrayList = posts.getReactions();
        if (reactionType.getReactionType().equals(posts.getReactions().get(mAuth.getCurrentUser().getUid()))) {
            arrayList.remove(mAuth.getCurrentUser().getUid());
        } else {
            arrayList.put(mAuth.getCurrentUser().getUid(), reactionType.getReactionType());
        }
        posts.setReactions(arrayList);

        readDataReadction(new MyCallBackReaction() {
            @Override
            public void onCallBack(Task<Void> task) {
                if (task.isSuccessful())
                    showDialogPython.dismissDialog();

            }
        }, posts);

    }

    @Override
    public void onClickProfile(int position, String id) {
        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        intent.putExtra("userId",id);
        getActivity().startActivity(intent);
    }

    @Override
    public void onClickOption(int position, Posts posts, MenuItem menuItem ) {
        switch (menuItem.getItemId()){
            case R.id.btn_delete:
                deletePost(posts);
                break;
            case R.id.btn_modify:
                Intent intent = new Intent(getActivity(),WritePostsActivity.class);
                intent.putExtra("post",posts);
                getActivity().startActivity(intent);
                break;
            case R.id.btn_save:
                savePost(posts);
                break;
        }
    }

    public void readDataReadction(MyCallBackReaction myCallback, Posts posts) {
        showDialogPython = new ShowDialogPython(getActivity(),getActivity().getLayoutInflater(),"load");

        db.collection("posts").document(posts.getPostId()).set(posts)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        myCallback.onCallBack(task);
                    }
                });
    }


    private void deletePost(Posts posts){
        showDialogPython = new ShowDialogPython(getActivity(),getActivity().getLayoutInflater(),"load");

        if(posts.getImgUri().size() > 0){
            numImages = posts.getImgUri().size();
            Log.e("numImages : ", numImages + "");
            myCallBackListItem(new MyCallbackDeletePost() {
                @Override
                public void myCallBack(ListResult listResult) {
                      for (StorageReference item : listResult.getItems()) {
                            // All the items under listRef.
                            numImages = numImages - 1 ;
                            StorageReference storageReference = firebaseStorage.getReference(item.getPath());
                            myCallBackDeleteItem(new MyCallbackDeleteItem() {
                                @Override
                                public void myCallBackItem(Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Log.e("task : ", numImages + "");
                                    }
                                }
                            },storageReference);
                            if(numImages == 0){
                                myCallBackDeletePost(new MyCallbackDeleteItem() {
                                    @Override
                                    public void myCallBackItem(Task<Void> task) {
                                       if(task.isSuccessful()){
                                           Log.e("successfully", "posts  deleted!");
                                       } else{
                                           Log.e("Failed", " posts deleted!");
                                       }
                                        showDialogPython.dismissDialog();
                                    }
                                },posts);
                            }
                        }
                }
            },posts);

        }else{
            myCallBackDeletePost(new MyCallbackDeleteItem() {
                @Override
                public void myCallBackItem(Task<Void> task) {
                    if(task.isSuccessful()){
                        Log.e("successfully", "posts  deleted!");
                    } else{
                        Log.e("Failed", " posts deleted!");
                    }
                    showDialogPython.dismissDialog();
                }
            },posts);
        }
    }

    private void savePost(Posts posts){
        showDialogPython = new ShowDialogPython(getActivity(),getActivity().getLayoutInflater(),"load");

        ArrayList<String> postArray = userAccount.getPostArray();
        if(postArray.contains(posts.getPostId())){
            Toast.makeText(getActivity(),"this post already in saved posts!",Toast.LENGTH_SHORT).show();
            Log.e("Failed", "this post already in saved posts!!");
            showDialogPython.dismissDialog();
        }else{
            postArray.add(posts.getPostId());
            userAccount.setPostArray(postArray);

            myCallBackSavePost(new MyCallbackDeleteItem() {
                @Override
                public void myCallBackItem(Task<Void> task) {
                    if(task.isSuccessful()){
                        Log.e("successfully", "posts saved!");
                    }else{
                        Log.e("Failed", " posts saved!");
                    }
                    showDialogPython.dismissDialog();
                }
            });
        }
    }

    private boolean isNetworkAvailable() {
        @SuppressLint("UseRequireInsteadOfGet") ConnectivityManager connectivityManager
                = (ConnectivityManager) Objects.requireNonNull(getActivity()).getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void myCallBackListItem(MyCallbackDeletePost myCallbackDeletePost, Posts posts){
        StorageReference storageReference = firebaseStorage.getReference(posts.getUserId());
        String path = "/"+posts.getPostId()+"/images";
        StorageReference listRef = storageReference.child(path);
        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        myCallbackDeletePost.myCallBack(listResult);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Uh-oh, an error occurred!
                    }
                });
    }

    private void myCallBackDeleteItem(MyCallbackDeleteItem myCallbackDeleteItem,
                                      StorageReference storageReference){
        storageReference.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                myCallbackDeleteItem.myCallBackItem(task);
            }
        });
    }

    private void myCallBackDeletePost(MyCallbackDeleteItem myCallbackDeleteItem,Posts posts){
        db.collection("posts").document(posts.getPostId())
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        myCallbackDeleteItem.myCallBackItem(task);
                    }
                });
    }

    private void myCallBackSavePost(MyCallbackDeleteItem myCallbackDeleteItem){
        db.collection("users").document(userAccount.getId())
                .set(userAccount).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
               myCallbackDeleteItem.myCallBackItem(task);
            }
        });
    }
}