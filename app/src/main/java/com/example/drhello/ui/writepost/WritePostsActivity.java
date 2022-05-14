package com.example.drhello.ui.writepost;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.example.drhello.ui.chats.StateOfUser;
import com.example.drhello.connectionnewtwork.CheckNetwork;
import com.example.drhello.firebaseinterface.MyCallbackUser;
import com.example.drhello.model.UserAccount;
import com.example.drhello.textclean.RequestPermissions;
import com.example.drhello.firebaseservice.FcmNotificationsSender;
import com.example.drhello.databinding.ActivityWritePostsBinding;
import com.example.drhello.model.Posts;
import com.example.drhello.ui.main.MainActivity;
import com.example.drhello.viewmodel.PostsViewModel;
import com.example.drhello.R;
import com.example.drhello.adapter.ImagePostsAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class WritePostsActivity extends AppCompatActivity {

    private final List<Bitmap> bitmaps = new ArrayList<>();
    private final List<String> uriImage = new ArrayList<>();
    private final List<Uri> uriImage2 = new ArrayList<>();
    private final List<byte[]> bytes = new ArrayList<>();
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private StorageReference ref;
    private Posts posts;
    public static ProgressDialog mProgress;
    private PostsViewModel postsViewModel;
    private static final String TAG = "Posts Activity";
    private ActivityWritePostsBinding activityWritePostsBinding;
    private RequestPermissions requestPermissions;

    private UserAccount userAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_posts);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            getWindow().setStatusBarColor(Color.WHITE);
        }
        requestPermissions = new RequestPermissions(WritePostsActivity.this, WritePostsActivity.this);

        inti();

        activityWritePostsBinding = DataBindingUtil.setContentView(this, R.layout.activity_write_posts);
        FirebaseMessaging.getInstance().subscribeToTopic("all");

        readData(new MyCallbackUser() {
            @Override
            public void onCallback(DocumentSnapshot documentSnapshot) {
                if (!documentSnapshot.exists()) {
                    FirebaseAuth.getInstance().getCurrentUser().delete();
                } else {
                    userAccount = documentSnapshot.toObject(UserAccount.class);
                    posts.setNameUser(userAccount.getName());
                    posts.setImageUser(userAccount.getImg_profile());
                    posts.setDate(getDateTime());
                    posts.setTokneId(userAccount.getTokenID());
                    Log.e("posts.UserMu ", posts.getImageUser());

                    activityWritePostsBinding.userAddress.setText(userAccount.getUserInformation().getCity());
                    activityWritePostsBinding.userName.setText(userAccount.getName());
                    try {
                        Glide.with(WritePostsActivity.this).load(userAccount.getImg_profile()).
                                placeholder(R.drawable.user).
                                error(R.drawable.user).into(activityWritePostsBinding.imageUser);
                    } catch (Exception e) {
                        activityWritePostsBinding.imageUser.setImageResource(R.drawable.user);
                    }

                    if (getIntent().getSerializableExtra("post") != null) {
                        posts = (Posts) getIntent().getSerializableExtra("post");
                        posts.setNameUser(userAccount.getName());
                        posts.setImageUser(userAccount.getImg_profile());
                        posts.setDate(getDateTime());
                        posts.setTokneId(userAccount.getTokenID());

                        activityWritePostsBinding.editPost.setText(posts.getWritePost());
                        activityWritePostsBinding.addImage.setVisibility(View.GONE);
                        //to upload post
                        activityWritePostsBinding.imgPost.setOnClickListener(v -> {
                            if (CheckNetwork.getConnectivityStatusString(WritePostsActivity.this) == 1) {
                                mProgress.setMessage("Uploading..");
                                mProgress.show();
                                mProgress.setCancelable(false);
                                String post = activityWritePostsBinding.editPost.getText().toString().trim();
                                Log.e("posts.getnameuser ", posts.getImageUser());
                                posts.setWritePost(post);
                                posts.setDate(getDateTime());
                                db.collection("posts")
                                        .document(posts.getPostId()).set(posts).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            FcmNotificationsSender fcmNotificationsSender = new FcmNotificationsSender("/topics/all",
                                                    mAuth.getCurrentUser().getUid(),
                                                    "Post",
                                                    posts.getNameUser() + " Upload a new post ",
                                                    getApplicationContext(),
                                                    WritePostsActivity.this,
                                                    posts.getImageUser());
                                            fcmNotificationsSender.SendNotifications();
                                            mProgress.dismiss();
                                            Intent intent = new Intent(WritePostsActivity.this, MainActivity.class);
                                            intent.putExtra("postsView", "postsView");
                                            startActivity(intent);
                                            Log.d(TAG, "onComplete: save uri ");

                                        } else {
                                            //Toast.makeText(WritePostsActivity.this, "error ", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });
                            } else {
                                Toast.makeText(WritePostsActivity.this, "Please, Check Internet", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        //to upload post
                        activityWritePostsBinding.imgPost.setVisibility(View.VISIBLE);
                        activityWritePostsBinding.imgPost.setOnClickListener(v -> {
                            if (CheckNetwork.getConnectivityStatusString(WritePostsActivity.this) == 1) {
                                mProgress.setMessage("Uploading..");
                                mProgress.show();
                                mProgress.setCancelable(false);
                                String post = activityWritePostsBinding.editPost.getText().toString().trim();
                                Log.e("posts.getnameuser ", posts.getImageUser());
                                posts.setReactions(new HashMap<>());
                                posts.setWritePost(post);
                                posts.setUserId(mAuth.getUid());
                                postsViewModel.uploadImages(db, storageReference, bytes, uriImage, posts);
                                postsViewModel.isfinish.observe(WritePostsActivity.this, integer -> {
                                    Log.d(TAG, "Image: " + integer + "  uriImage.size() : " + bytes.size());
                                    if (integer == bytes.size()) {
                                        Log.d(TAG, "uploadImage: " + integer);
                                        Log.e("int image ", "fcm");
                                        FcmNotificationsSender fcmNotificationsSender = new FcmNotificationsSender("/topics/all",
                                                mAuth.getCurrentUser().getUid(),
                                                "Post",
                                                posts.getNameUser() + " Upload a new post ",
                                                getApplicationContext(),
                                                WritePostsActivity.this,
                                                posts.getImageUser());
                                        fcmNotificationsSender.SendNotifications();
                                        mProgress.dismiss();
                                        Intent intent = new Intent(WritePostsActivity.this, MainActivity.class);
                                        intent.putExtra("postsView", "postsView");
                                        startActivity(intent);
                                    }
                                });
                            } else {
                                Toast.makeText(WritePostsActivity.this, "Please, Check Internet", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                }
                mProgress.dismiss();
            }
        });



        activityWritePostsBinding.imgBackPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WritePostsActivity.this, MainActivity.class);
                intent.putExtra("postsView", "postsView");
                startActivity(intent);
            }
        });


        activityWritePostsBinding.addImage.setOnClickListener(v -> {
            if (requestPermissions.permissionStorageRead()) {
                ActivityCompat.requestPermissions(WritePostsActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            } else {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
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

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.US);
        Date date = new Date();
        return dateFormat.format(date);
    }

    private void inti() {
        mProgress = new ProgressDialog(this);
        Toolbar toolbar = findViewById(R.id.toolbar_posts);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        posts = new Posts();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
        postsViewModel = new PostsViewModel();
        postsViewModel = ViewModelProviders.of(this).get(PostsViewModel.class);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {

            assert data != null;
            ClipData clipData = data.getClipData();
            if (clipData != null) {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    Uri imgUri = clipData.getItemAt(i).getUri();
                    Bitmap bitmap;
                    uriImage2.add(imgUri);
                    try {
                        //To save in FirebaseStorage
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgUri);
                        ByteArrayOutputStream bytesStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG , 100, bytesStream);
                        byte[] bytesOutImg = bytesStream.toByteArray();
                        bytes.add(bytesOutImg);

                        //To show in the same activity
                        InputStream is = getContentResolver().openInputStream(imgUri);
                        Bitmap bitmap_really = BitmapFactory.decodeStream(is);
                        bitmaps.add(bitmap_really);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Uri imgUri = data.getData();
                Bitmap bitmap;
                try {
                    //To save in FirebaseStorage
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgUri);
                    ByteArrayOutputStream bytesStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytesStream);
                    byte[] bytesOutImg = bytesStream.toByteArray();
                    bytes.add(bytesOutImg);

                    //To show in the same activity
                    InputStream is = getContentResolver().openInputStream(imgUri);
                    Bitmap bitmap_really = BitmapFactory.decodeStream(is);

                    bitmaps.add(bitmap_really);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (bitmaps.size() == 1) {
                activityWritePostsBinding.editPost.setHint("Say something about this photo...");
            } else if (bitmaps.size() > 1) {
                activityWritePostsBinding.editPost.setHint("Say something about these photos...");
            } else {
                activityWritePostsBinding.editPost.setHint("What’s on your mind?");
            }
            ImagePostsAdapter imagePostsAdapter = new ImagePostsAdapter(WritePostsActivity.this, bitmaps);
            GridLayoutManager recycleLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
            recycleLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    // grid items to take 1 column
                    if (bitmaps.size() % 2 == 0) {
                        return 1;
                    } else {
                        return (position == bitmaps.size() - 1) ? 2 : 1;
                    }
                }
            });
            activityWritePostsBinding.recycleImages.setLayoutManager(recycleLayoutManager);
            Objects.requireNonNull(activityWritePostsBinding.recycleImages.getLayoutManager()).scrollToPosition(imagePostsAdapter.getItemCount() - 1);
            activityWritePostsBinding.recycleImages.setAdapter(imagePostsAdapter);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        StateOfUser stateOfUser = new StateOfUser();
        stateOfUser.changeState("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        StateOfUser stateOfUser = new StateOfUser();
        stateOfUser.changeState("Offline");
    }

}