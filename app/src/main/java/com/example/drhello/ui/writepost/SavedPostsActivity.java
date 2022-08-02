package com.example.drhello.ui.writepost;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import com.example.drhello.R;
import com.example.drhello.other.ShowDialogPython;
import com.example.drhello.adapter.OnPostClickListener;
import com.example.drhello.adapter.PostsAdapter;
import com.example.drhello.databinding.ActivityPostsUsersBinding;
import com.example.drhello.firebaseinterface.MyCallBackListenerComments;
import com.example.drhello.firebaseinterface.MyCallBackReaction;
import com.example.drhello.firebaseinterface.MyCallbackUser;
import com.example.drhello.model.Posts;
import com.example.drhello.model.ReactionType;
import com.example.drhello.model.UserAccount;
import com.example.drhello.ui.profile.ProfileActivity;
import com.example.drhello.ui.writecomment.WriteCommentActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;

public class SavedPostsActivity extends AppCompatActivity implements OnPostClickListener {
    ArrayList<Posts> postsArrayList = new ArrayList<>();
    private PostsAdapter postsAdapter;
    private FirebaseFirestore db;
    private ActivityPostsUsersBinding activityPostsUsersBinding;
    private UserAccount userAccount;
    private ArrayList<String>  stringArrayList = new ArrayList<>();
    private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.US);
    ShowDialogPython showDialogPython;

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
        activityPostsUsersBinding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        db = FirebaseFirestore.getInstance();

        readData(new MyCallbackUser() {
            @Override
            public void onCallback(DocumentSnapshot documentSnapshot) {
                if(!documentSnapshot.exists()){
                    FirebaseAuth.getInstance().getCurrentUser().delete();
                }else {
                    userAccount = documentSnapshot.toObject(UserAccount.class);
                    stringArrayList = userAccount.getPostArray();
                    readDataPostsListener(new MyCallBackListenerComments() {
                        @Override
                        public void onCallBack(QuerySnapshot value) {
                            Log.e("lostart2 : ", postsArrayList.size() + "");
                            Log.e("lost : ", value.getDocuments().size() + "");
                            for (DocumentSnapshot document : value.getDocuments()) {
                                Posts post = document.toObject(Posts.class);
                                if(stringArrayList.contains(post.getPostId())){
                                    postsArrayList.add(post);
                                }
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

                            showDialogPython.dismissDialog();
                        }
                    });
                }
            }
        });



        postsAdapter = new PostsAdapter(SavedPostsActivity.this, postsArrayList,
                SavedPostsActivity.this,
                getSupportFragmentManager(),"SavedPostsActivity");
        activityPostsUsersBinding.recyclePosts.setAdapter(postsAdapter);

    }

    public void readData(MyCallbackUser myCallback) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (userId != null) {
            showDialogPython = new ShowDialogPython(SavedPostsActivity.this,SavedPostsActivity.this.getLayoutInflater(),"load");

            FirebaseFirestore.getInstance().collection("users")
                    .document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    myCallback.onCallback(documentSnapshot);
                }
            });
        }
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
                showDialogPython.dismissDialog();
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
    public void onClickOption(int position, Posts posts, MenuItem menuItem  ) {
        switch (menuItem.getItemId()){
            case R.id.btn_delete:
                deletePost(posts);
                break;
        }
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
                    postsArrayList.remove(posts);
                    postsAdapter.notifyDataSetChanged();
                }else{
                    Log.e("Failed", " posts deleted!");
                }
                showDialogPython.dismissDialog();
            }
        });
    }

    public void readDataReadction(MyCallBackReaction myCallback, Posts posts) {
        showDialogPython = new ShowDialogPython(SavedPostsActivity.this,SavedPostsActivity.this.getLayoutInflater(),"load");

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