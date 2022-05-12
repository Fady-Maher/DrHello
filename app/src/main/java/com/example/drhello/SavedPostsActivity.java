package com.example.drhello;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.example.drhello.adapter.OnPostClickListener;
import com.example.drhello.adapter.PostsAdapter;
import com.example.drhello.databinding.ActivityPostsUsersBinding;
import com.example.drhello.firebaseinterface.MyCallBackListenerComments;
import com.example.drhello.firebaseinterface.MyCallBackReaction;
import com.example.drhello.model.Posts;
import com.example.drhello.model.ReactionType;
import com.example.drhello.model.UserAccount;
import com.example.drhello.ui.profile.ProfileActivity;
import com.example.drhello.ui.writecomment.WriteCommentActivity;
import com.example.drhello.ui.writepost.NumReactionActivity;
import com.example.drhello.ui.writepost.ShowImageActivity;
import com.example.drhello.ui.writepost.WritePostsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class SavedPostsActivity extends AppCompatActivity implements OnPostClickListener {
    ArrayList<Posts> postsArrayList = new ArrayList<>();
    private PostsAdapter postsAdapter;
    private FirebaseFirestore db;
    public static ProgressDialog mProgress;
    private ActivityPostsUsersBinding activityPostsUsersBinding;
    private UserAccount userAccount;
    private ArrayList<String>  stringArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_posts);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            getWindow().setStatusBarColor(Color.WHITE);
        }
        activityPostsUsersBinding = DataBindingUtil.setContentView(this, R.layout.activity_posts_users);

        db = FirebaseFirestore.getInstance();
        mProgress = new ProgressDialog(SavedPostsActivity.this);

        if (getIntent().getSerializableExtra("userAccount") != null) {
            userAccount = (UserAccount) getIntent().getSerializableExtra("userAccount");
            stringArrayList = userAccount.getPostArray();
            readDataPostsListener(new MyCallBackListenerComments() {
                @Override
                public void onCallBack(QuerySnapshot value) {
                    Log.e("lostart2 : ", postsArrayList.size() + "");
                    for (DocumentSnapshot document : value.getDocuments()) {
                        Posts post = document.toObject(Posts.class);
                        if(stringArrayList.contains(post.getPostId())){
                            postsArrayList.add(post);
                            postsAdapter.FunPostsAdapter(postsArrayList);
                        }
                    }
                }
            });
        }

        postsAdapter = new PostsAdapter(SavedPostsActivity.this, postsArrayList,
                SavedPostsActivity.this,
                getSupportFragmentManager());
        activityPostsUsersBinding.recyclePosts.setAdapter(postsAdapter);

    }

    public void readDataPostsListener(MyCallBackListenerComments myCallback) {
        db.collection("posts")
                .orderBy("date", Query.Direction.DESCENDING)
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
        Intent intent = new Intent(SavedPostsActivity.this, ShowImageActivity.class);
        intent.putExtra("uri_image", uri);
        startActivity(intent);
    }

    @Override
    public void onClickNumReaction(Posts posts) {
        Intent intent = new Intent(SavedPostsActivity.this, NumReactionActivity.class);
        intent.putExtra("post", posts);
        startActivity(intent);
    }

    @Override
    public void onClickComment(Posts posts) {
        Intent intent = new Intent(SavedPostsActivity.this, WriteCommentActivity.class);
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

    @Override
    public void onClickProfile(int position, String id) {
        Intent intent = new Intent(SavedPostsActivity.this, ProfileActivity.class);
        intent.putExtra("userId", id);
        startActivity(intent);
    }

    @Override
    public void onClickOption(int position, Posts posts) {
        LayoutInflater factory = LayoutInflater.from(SavedPostsActivity.this);
        final View deleteDialogView = factory.inflate(R.layout.alertdialogposts, null);
        final AlertDialog deleteDialog = new AlertDialog.Builder(SavedPostsActivity.this).create();
        deleteDialog.setView(deleteDialogView);
        Button btn_delete = deleteDialogView.findViewById(R.id.btn_delete);
        Button btn_modify = deleteDialogView.findViewById(R.id.btn_modify);
        if(posts.getUserId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            btn_delete.setVisibility(View.VISIBLE);
            btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deletePost(posts);
                    deleteDialog.dismiss();
                }
            });

            btn_modify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SavedPostsActivity.this,WritePostsActivity.class);
                    intent.putExtra("post",posts);
                    startActivity(intent);
                    deleteDialog.dismiss();
                }
            });

        }else {
            btn_delete.setVisibility(View.GONE);
            btn_modify.setVisibility(View.GONE);
        }

        deleteDialog.show();
    }

    private void deletePost(Posts posts){
        stringArrayList.remove(posts.getPostId());
        userAccount.setPostArray(stringArrayList);
        db.collection("users").document(userAccount.getId())
                .set(userAccount).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.e("successfully", "posts  deleted!");
                }else{
                    Log.e("Failed", " posts deleted!");
                }
                mProgress.dismiss();
            }
        });
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
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}