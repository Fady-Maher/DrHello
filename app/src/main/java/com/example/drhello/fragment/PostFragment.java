package com.example.drhello.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.drhello.firebaseinterface.MyCallBackListenerComments;
import com.example.drhello.firebaseinterface.MyCallBackReaction;
import com.example.drhello.firebaseinterface.MyCallbackUser;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class PostFragment extends Fragment implements OnPostClickListener {

    private Button btn_write_post;
    ArrayList<Posts> postsArrayList = new ArrayList<>();
    private TextView textView;
    private RecyclerView recycler_posts;
    private PostsAdapter postsAdapter;
    private ArrayList<String> strings = new ArrayList<>();
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    public static ProgressDialog mProgress;
    ImageView image_user;

    public PostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        mProgress = new ProgressDialog(getActivity());

        readData(new MyCallbackUser() {
            @Override
            public void onCallback(DocumentSnapshot documentSnapshot) {
                if (!documentSnapshot.exists()) {
                    FirebaseAuth.getInstance().getCurrentUser().delete();
                } else {
                    UserAccount userAccount = documentSnapshot.toObject(UserAccount.class);
                    try {
                        Glide.with(getActivity()).load(userAccount.getImg_profile()).placeholder(R.drawable.user).
                                error(R.drawable.user).into(image_user);
                    } catch (Exception e) {
                        image_user.setImageResource(R.drawable.user);
                    }
                }
                mProgress.dismiss();
            }
        });


        btn_write_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), WritePostsActivity.class));
            }
        });

        postsAdapter = new PostsAdapter(getActivity(), postsArrayList,
                PostFragment.this, getActivity().getSupportFragmentManager());
        recycler_posts.setAdapter(postsAdapter);

        readDataPostsListener(new MyCallBackListenerComments() {
            @Override
            public void onCallBack(QuerySnapshot value) {
                Log.e("lostart2 : ", postsArrayList.size() + "");
                for (DocumentSnapshot document : value.getDocuments()) {
                    Posts singele_posts = document.toObject(Posts.class);
                    postsArrayList.add(singele_posts);
                    postsAdapter.FunPostsAdapter(postsArrayList);
                    postsAdapter.notifyDataSetChanged();
                }
            }
        });

        return view;
    }

    public void readDataPostsListener(MyCallBackListenerComments myCallback) {

        db.collection("posts").orderBy("date", Query.Direction.DESCENDING)
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
                    mProgress.dismiss();
            }
        }, posts);

    }

    public void readDataReadction(MyCallBackReaction myCallback, Posts posts) {
        mProgress.setMessage("Loading..");
        mProgress.setCancelable(false);
        mProgress.show();
        db.collection("posts").document(posts.getPostId()).set(posts)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        myCallback.onCallBack(task);
                    }
                });
    }

    private boolean isNetworkAvailable() {
        @SuppressLint("UseRequireInsteadOfGet") ConnectivityManager connectivityManager
                = (ConnectivityManager) Objects.requireNonNull(getActivity()).getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}