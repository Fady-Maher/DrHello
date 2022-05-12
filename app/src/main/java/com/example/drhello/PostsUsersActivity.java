package com.example.drhello;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.drhello.adapter.OnPostClickListener;
import com.example.drhello.adapter.PostsAdapter;
import com.example.drhello.databinding.ActivityPostsUsersBinding;
import com.example.drhello.firebaseinterface.MyCallBackListenerComments;
import com.example.drhello.firebaseinterface.MyCallBackReaction;
import com.example.drhello.fragment.PostFragment;
import com.example.drhello.model.Posts;
import com.example.drhello.model.ReactionType;
import com.example.drhello.model.UserAccount;
import com.example.drhello.ui.profile.ProfileActivity;
import com.example.drhello.ui.writecomment.WriteCommentActivity;
import com.example.drhello.ui.writepost.NumReactionActivity;
import com.example.drhello.ui.writepost.ShowImageActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class PostsUsersActivity extends AppCompatActivity implements OnClickPostProfileListener, OnPostClickListener {
    ArrayList<Posts> postsArrayList = new ArrayList<>();
    private PostsAdapter postsAdapter;
    private FirebaseFirestore db;
    public static ProgressDialog mProgress;
    private ActivityPostsUsersBinding activityPostsUsersBinding;
    private UserAccount userAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts_users);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            getWindow().setStatusBarColor(Color.WHITE);
        }
        activityPostsUsersBinding = DataBindingUtil.setContentView(this, R.layout.activity_posts_users);

        db = FirebaseFirestore.getInstance();
        mProgress = new ProgressDialog(PostsUsersActivity.this);

        if (getIntent().getSerializableExtra("userAccount") != null) {
            userAccount = (UserAccount) getIntent().getSerializableExtra("userAccount");
            readDataPostsListener(new MyCallBackListenerComments() {
                @Override
                public void onCallBack(QuerySnapshot value) {
                    Log.e("lostart2 : ", postsArrayList.size() + "");
                    for (DocumentSnapshot document : value.getDocuments()) {
                        Posts post = document.toObject(Posts.class);
                        if (userAccount.getId().equals(post.getUserId())) {
                            postsArrayList.add(post);
                            postsAdapter.FunPostsAdapter(postsArrayList);
                            postsAdapter.notifyDataSetChanged();
                        }

                    }
                }
            });
        }

        postsAdapter = new PostsAdapter(PostsUsersActivity.this, postsArrayList,
                PostsUsersActivity.this,
                getSupportFragmentManager(), PostsUsersActivity.this);
        activityPostsUsersBinding.recyclePosts.setAdapter(postsAdapter);


    }

    public void readDataPostsListener(MyCallBackListenerComments myCallback) {
        db.collection("posts").orderBy("date", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        postsArrayList.clear();
                        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                            myCallback.onCallBack(value);
                        }
                    }
                });
    }

    @Override
    public void onClickImage(String uri) {
        Intent intent = new Intent(PostsUsersActivity.this, ShowImageActivity.class);
        intent.putExtra("uri_image", uri);
        startActivity(intent);
    }

    @Override
    public void onClickNumReaction(Posts posts) {
        Intent intent = new Intent(PostsUsersActivity.this, NumReactionActivity.class);
        intent.putExtra("post", posts);
        startActivity(intent);
    }

    @Override
    public void onClickComment(Posts posts) {
        Intent intent = new Intent(PostsUsersActivity.this, WriteCommentActivity.class);
        intent.putExtra("post", posts);
        startActivity(intent);
    }

    @Override
    public void selectedReaction(String reaction, Posts posts) {
        ReactionType reactionType = new ReactionType(reaction, FirebaseAuth.getInstance().getCurrentUser().getUid());
        Log.e("reactionType", reactionType.getReactionType());  // new
        Map<String, String> arrayList = posts.getReactions();
        if (reactionType.getReactionType().equals(posts.getReactions().get(FirebaseAuth.getInstance().getCurrentUser().getUid()))) {
            arrayList.remove(FirebaseAuth.getInstance().getCurrentUser().getUid());
        } else {
            arrayList.put(FirebaseAuth.getInstance().getCurrentUser().getUid(), reactionType.getReactionType());
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

    @Override
    public void onClick(int position, String id) {
        Intent intent = new Intent(PostsUsersActivity.this, ProfileActivity.class);
        intent.putExtra("userId", id);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}