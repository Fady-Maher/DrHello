package com.example.drhello.ui.writecomment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.drhello.ui.chats.StateOfUser;
import com.example.drhello.connectionnewtwork.CheckNetwork;
import com.example.drhello.firebaseinterface.MyCallBackListenerComments;
import com.example.drhello.firebaseinterface.MyCallBackReaction;
import com.example.drhello.firebaseinterface.MyCallBackWriteComment;
import com.example.drhello.firebaseinterface.MyCallbackUser;
import com.example.drhello.model.UserAccount;
import com.example.drhello.textclean.RequestPermissions;
import com.example.drhello.firebaseservice.FcmNotificationsSender;
import com.example.drhello.model.ReactionType;
import com.example.drhello.databinding.ActivityWriteCommentBinding;
import com.example.drhello.model.CommentModel;
import com.example.drhello.ui.main.MainActivity;
import com.example.drhello.ui.writepost.NumReactionActivity;
import com.example.drhello.viewmodel.CommentViewModel;
import com.example.drhello.model.Posts;
import com.example.drhello.R;
import com.example.drhello.adapter.OnCommentClickListener;
import com.example.drhello.adapter.WriteCommentAdapter;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class WriteCommentActivity extends AppCompatActivity implements OnCommentClickListener {

    private static final int Gallary_REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST_CODE = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private FirebaseAuth mAuth;
    Posts posts = null;
    private FirebaseFirestore db;
    CommentModel commentModel = new CommentModel();
    private CommentViewModel commentViewModel;
    private WriteCommentAdapter writeCommentAdapter;
    public static ProgressDialog mProgress,mProgress1;
    private final ArrayList<CommentModel> commentModels = new ArrayList<>();
    private Bitmap bitmap;
    private boolean check_img = false;
    public static ActivityWriteCommentBinding MainCommentBinding;
    private String postID;
    private RequestPermissions requestPermissions;
    AsyncTaskD asyncTaskDownload;
    PyObject main_program;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_comment);
        requestPermissions = new RequestPermissions(WriteCommentActivity.this, WriteCommentActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            getWindow().setStatusBarColor(Color.WHITE);
        }
        mProgress = new ProgressDialog(this);
        mProgress1 = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        commentViewModel = new CommentViewModel();
        commentViewModel = ViewModelProviders.of(WriteCommentActivity.this).get(CommentViewModel.class);

        postID = getIntent().getStringExtra("postID");

        if (postID != null) {
            Log.e("write : ", postID + "  hoos");
            readData(new MyCallBackWriteComment() {
                @Override
                public void onCallback(Task<QuerySnapshot> task) {
                    commentModels.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        CommentModel commentModel = document.toObject(CommentModel.class);
                        commentModels.add(commentModel);
                    }

                    writeCommentAdapter = new WriteCommentAdapter(WriteCommentActivity.this, commentModels,
                            WriteCommentActivity.this, getSupportFragmentManager());
                    MainCommentBinding.recycleComments.setAdapter(writeCommentAdapter);
                    mProgress.dismiss();
                }
            });
            /*
            db.collection("posts").document(postID)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        posts = task.getResult().toObject(Posts.class);

                        commentViewModel.getComments(db, posts, null);
                        commentViewModel.commentsMutableLiveData.observe(WriteCommentActivity.this, commentModels -> {
                            writeCommentAdapter = new WriteCommentAdapter(WriteCommentActivity.this, commentModels,
                                    WriteCommentActivity.this, getSupportFragmentManager());
                            MainCommentBinding.recycleComments.setAdapter(writeCommentAdapter);
                            mProgress.dismiss();
                        });

                    } else {
                        Log.e("noti error", task.getException().getMessage());
                    }
                }
            });
            */
            Log.e("notification", postID);
        } else {
            posts = (Posts) getIntent().getSerializableExtra("post");
            readDataComments(new MyCallBackWriteComment() {
                @Override
                public void onCallback(Task<QuerySnapshot> task) {
                    commentModels.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        CommentModel commentModel = document.toObject(CommentModel.class);
                        commentModels.add(commentModel);
                    }
                    mProgress.dismiss();
                    writeCommentAdapter = new WriteCommentAdapter(WriteCommentActivity.this, commentModels,
                            WriteCommentActivity.this, getSupportFragmentManager());
                    MainCommentBinding.recycleComments.setAdapter(writeCommentAdapter);
                }
            });
        }

        MainCommentBinding = DataBindingUtil.setContentView(this, R.layout.activity_write_comment);

        MainCommentBinding.constraintCommentRoot.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = MainCommentBinding.constraintCommentRoot.getRootView().getHeight()
                        - MainCommentBinding.constraintCommentRoot.getHeight();

                if (heightDiff > 400) { // Value should be less than keyboard's height
                    Log.e("MyActivity", "keyboard opened" + heightDiff);
                    if (check_img == false) {
                        MainCommentBinding.constraintSend.setVisibility(View.VISIBLE);
                    }
                } else {
                    Log.e("MyActivity", "keyboard closed");
                    String text = MainCommentBinding.editMessage.getText().toString().trim();

                    if (check_img == false) {
                        if (bitmap != null) {
                            MainCommentBinding.relImage.setVisibility(View.VISIBLE);
                        } else {
                            MainCommentBinding.relImage.setVisibility(View.GONE);
                        }
                        MainCommentBinding.linOption.setVisibility(View.VISIBLE);

                        if (!text.isEmpty() || bitmap != null) {
                            MainCommentBinding.constraintSend.setVisibility(View.VISIBLE);
                        } else {
                            MainCommentBinding.constraintSend.setVisibility(View.GONE);
                        }
                    } else {
                        MainCommentBinding.relImage.setVisibility(View.VISIBLE);
                        MainCommentBinding.linOption.setVisibility(View.GONE);
                        MainCommentBinding.constraintSend.setVisibility(View.VISIBLE);
                    }

                }
            }
        });

        MainCommentBinding.btnCancel.setOnClickListener(v -> {
            bitmap = null;
            check_img = false;
            MainCommentBinding.linOption.setVisibility(View.VISIBLE);
            MainCommentBinding.relImage.setVisibility(View.GONE);
            MainCommentBinding.constraintSend.setVisibility(View.GONE);
        });

        readDataUser(new MyCallbackUser() {
            @Override
            public void onCallback(DocumentSnapshot documentSnapshot) {
                UserAccount userAccount = documentSnapshot.toObject(UserAccount.class);
                commentModel.setUser_image(userAccount.getImg_profile());
                commentModel.setUser_id(userAccount.getId());
                commentModel.setUser_name(userAccount.getName());
                commentModel.setPost_id(posts.getPostId());
            }
        });
        asyncTaskDownload = new AsyncTaskD(null, "comment", "first");
        asyncTaskDownload.execute();
        MainCommentBinding.fabImage.setOnClickListener(view -> {

            if (requestPermissions.permissionStorageRead()) {
                ActivityCompat.requestPermissions(WriteCommentActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_CAMERA_PERMISSION_CODE);
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                String[] mimetypes = {"image/*", "video/*"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                startActivityForResult(intent, Gallary_REQUEST_CODE);
            } else {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                String[] mimetypes = {"image/*", "video/*"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                startActivityForResult(intent, Gallary_REQUEST_CODE);
            }
        });

        MainCommentBinding.fabCamera.setOnClickListener(view -> {

            if (requestPermissions.permissionGallery()) {
                ActivityCompat.requestPermissions(WriteCommentActivity.this, new String[]{Manifest.permission.CAMERA},
                        MY_CAMERA_PERMISSION_CODE);
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
            } else {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
            }

        });

        MainCommentBinding.imageSend.setOnClickListener(view -> {
            if (CheckNetwork.getConnectivityStatusString(WriteCommentActivity.this) == 1) {
                /************************************************************/
                if (bitmap != null) {
                    byte[] bytesOutImg;
                    commentModel.setComment(MainCommentBinding.editMessage.getText().toString());
                    commentModel.setDate(getDateTime());
                    ByteArrayOutputStream bytesStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytesStream);
                    bytesOutImg = bytesStream.toByteArray();
                    bitmap = null;
                    asyncTaskDownload = new AsyncTaskD(bytesOutImg, commentModel.getComment(), "uploadImages");
                    asyncTaskDownload.execute();
                    Log.e("image123 : ", "EROR");
                } else {
                    Log.e("bitmap1112 : ", bitmap + "");
                    commentModel.setComment_image(null);
                    commentModel.setComment(MainCommentBinding.editMessage.getText().toString());
                    commentModel.setDate(getDateTime());
                    asyncTaskDownload = new AsyncTaskD(null, commentModel.getComment(), "");
                    asyncTaskDownload.execute();
                }

                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(MainCommentBinding.editMessage.getWindowToken(), 0);

            } else {
                Toast.makeText(WriteCommentActivity.this, "Please, Check Internet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (posts != null) {
            readDataCommentsListener(new MyCallBackListenerComments() {
                @Override
                public void onCallBack(QuerySnapshot value) {
                    commentModels.clear();
                    int i = 0;
                    /*
                    if(value.size() == 0){
                        mProgress1.dismiss();
                    }*/
                    for (DocumentSnapshot document : value.getDocuments()) {
                        CommentModel commentModel = document.toObject(CommentModel.class);
                        commentModels.add(commentModel);
                        i = i +1;
                        if(i == value.size()){
                            writeCommentAdapter = new WriteCommentAdapter(WriteCommentActivity.this,
                                    commentModels, WriteCommentActivity.this, getSupportFragmentManager());
                            MainCommentBinding.recycleComments.setAdapter(writeCommentAdapter);
                        //    mProgress1.dismiss();
                        }
                    }
                }
            });
        }
    }

    public void readData(MyCallBackWriteComment myCallback) {
        mProgress.setMessage("Loading..");
        mProgress.setCancelable(false);
        mProgress.show();
        db.collection("posts").document(postID)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    posts = task.getResult().toObject(Posts.class);
                    db.collection("posts").document(posts.getPostId())
                            .collection("comments").orderBy("date", Query.Direction.DESCENDING)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                myCallback.onCallback(task);
                                Log.e("task : ", " tast");
                            }
                        }
                    });
                } else {
                    Log.e("noti error", task.getException().getMessage());
                }
            }
        });
    }

    public void readDataComments(MyCallBackWriteComment myCallback) {
        mProgress.setMessage("Loading..");
        mProgress.setCancelable(false);
        mProgress.show();
        db.collection("posts").document(posts.getPostId())
                .collection("comments").orderBy("date", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    myCallback.onCallback(task);
                    Log.e("task : ", " tast");
                } else {
                    Log.e("noti error", task.getException().getMessage());
                }
            }
        });
    }

    public void readDataCommentsListener(MyCallBackListenerComments myCallback) {
        /*
        mProgress1.setMessage("Loading..");
        mProgress1.setCancelable(false);
        mProgress1.show();

         */
        db.collection("posts").document(posts.getPostId())
                .collection("comments").orderBy("date", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        myCallback.onCallBack(value);
                    }
                });
    }

    public void readDataUser(MyCallbackUser myCallback) {
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            check_img = true;
            try {
                bitmap = null;
                bitmap = (Bitmap) data.getExtras().get("data");
                if (bitmap != null) {
                    MainCommentBinding.showImage.setImageBitmap(bitmap);
                    MainCommentBinding.relImage.setVisibility(View.VISIBLE);
                    MainCommentBinding.linOption.setVisibility(View.GONE);
                } else {
                    MainCommentBinding.linOption.setVisibility(View.VISIBLE);
                    MainCommentBinding.relImage.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                Log.e("camera exception: ", e.getMessage());
            }
        } else if (requestCode == Gallary_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            check_img = true;
            try {
                bitmap = null;
                bitmap = MediaStore.Images.Media.getBitmap(getBaseContext().getContentResolver(), data.getData());
                if (bitmap != null) {
                    MainCommentBinding.showImage.setImageBitmap(bitmap);
                    MainCommentBinding.relImage.setVisibility(View.VISIBLE);
                    MainCommentBinding.linOption.setVisibility(View.GONE);
                } else {
                    MainCommentBinding.linOption.setVisibility(View.VISIBLE);
                    MainCommentBinding.relImage.setVisibility(View.GONE);
                }
            } catch (IOException e) {
                Log.e("gallary exception: ", e.getMessage());
            }
        }

    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.US);
        Date date = new Date();
        return dateFormat.format(date);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(WriteCommentActivity.this, MainActivity.class);
        intent.putExtra("openPost", "openPost");
        startActivity(intent);
        finish();

    }

    @Override
    public void onClickComment(CommentModel commentModel) {
        Log.e("commentclikc : ", commentModel.getDate());
        Intent intent = new Intent(WriteCommentActivity.this, InsideCommentActivity.class);
        intent.putExtra("commentModel", commentModel);
        intent.putExtra("postsModel", posts);
        startActivity(intent);
    }

    @Override
    public void selectedReaction(String reaction, CommentModel commentModel) {
        ReactionType reactionType = new ReactionType(reaction, mAuth.getCurrentUser().getUid());
        Log.e("reactionType", reactionType.getReactionType());  // new
        Map<String, String> arrayList = posts.getReactions();
        if (reactionType.getReactionType().equals(posts.getReactions().get(mAuth.getCurrentUser().getUid()))) {
            arrayList.remove(mAuth.getCurrentUser().getUid());
        } else {
            arrayList.put(mAuth.getCurrentUser().getUid(), reactionType.getReactionType());
        }
        commentModel.setReactions(arrayList);
        readDataReadction(new MyCallBackReaction() {
            @Override
            public void onCallBack(Task<Void> task) {
                if (task.isSuccessful())
                    mProgress.dismiss();
            }
        }, commentModel);
    }

    @Override
    public void onClickReaction(CommentModel commentModel) {
        Intent intent = new Intent(WriteCommentActivity.this, NumReactionActivity.class);
        intent.putExtra("commentModel", commentModel);
        startActivity(intent);
    }

    public void readDataReadction(MyCallBackReaction myCallback, CommentModel commentModel) {
        mProgress.setMessage("Loading..");
        mProgress.setCancelable(false);
        mProgress.show();
        db.collection("posts").document(posts.getPostId()).
                collection("comments").document(commentModel.getComment_id()).set(commentModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.e("equals ", "isSuccessful");
                        myCallback.onCallBack(task);
                    }
                });
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

    public class AsyncTaskD extends AsyncTask<String, String, String> {

        String text;
        String action;
        byte[] bytesOutImg;
        float prop = -1;

        public AsyncTaskD(byte[] bytesOutImg, String text, String action) {
            this.text = text;
            this.action = action;
            this.bytesOutImg = bytesOutImg;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!action.equals("first")) {
                mProgress.setMessage("Uploading..");
                mProgress.show();
                mProgress.setCancelable(false);
            }
        }

        @Override
        protected String doInBackground(String... f_url) {
            if (action.equals("first")) {
                if (!Python.isStarted()) {
                    Python.start(new AndroidPlatform(WriteCommentActivity.this));//error is here!
                }
                final Python py = Python.getInstance();
                main_program = py.getModule("prolog");
            } else {
                if (!text.isEmpty()) {
                    Log.e("TEXT CORRECT: ",text);
                    String result = main_program.callAttr("modelCommentAndPost", text).toString();
                    result = result.replace("[", "").replace("]", "");
                    if (result.equals("error")) {
                        prop = (float) 0.1;
                    } else {
                        prop = Float.parseFloat(result);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            if (action.equals("first")) {
                Log.e("first ", " first");
                mProgress.dismiss();
            } else if (prop >= 0 && prop < 0.5) {
                if (action.equals("uploadImages")) {
                    Log.e("action: ",  "uploadImages1");
                    commentViewModel.uploadComment(db, bytesOutImg, posts, commentModel, null,mProgress);
                    bytesOutImg = null;
                    MainCommentBinding.editMessage.setText("");
                    bitmap = null;
                } else {
                    Log.e("action: ",  "text");
                    commentViewModel.uploadComment(db, null, posts, commentModel, null, mProgress);
                    MainCommentBinding.editMessage.setText("");
                }
                uploadImages();
            } else if (prop >= 0.5) {
                Log.e("prop failed: ", prop + "");
                mProgress.dismiss();
            }else if(action.equals("uploadImages")){
                Log.e("action: ",  "uploadImages2");
                commentViewModel.uploadComment(db, bytesOutImg, posts, commentModel, null, mProgress);
                bytesOutImg = null;
                MainCommentBinding.editMessage.setText("");
                bitmap = null;
                uploadImages();
            }
        }
    }

    private void uploadImages(){
        FcmNotificationsSender fcmNotificationsSender = new FcmNotificationsSender(posts.getTokneId(),
                mAuth.getCurrentUser().getUid(),
                "Comment",
                commentModel.getUser_name() + " commented on your post",
                getApplicationContext(),
                WriteCommentActivity.this,
                commentModel.getUser_image(),
                posts.getPostId());
        fcmNotificationsSender.SendNotifications();
        check_img = false;
        posts.setCommentNum(posts.getCommentNum() + 1);
        db.collection("posts").document(posts.getPostId())
                .set(posts).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.e("prop onComplete: ", "");
                } else {
                    Log.e("error onComplete: ", "");
                }
           //     mProgress.dismiss();
            }
        });
    }


}