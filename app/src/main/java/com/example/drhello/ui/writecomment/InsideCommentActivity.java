package com.example.drhello.ui.writecomment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.annotation.SuppressLint;
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

import com.bumptech.glide.Glide;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.drhello.ShowDialogPython;
import com.example.drhello.adapter.OnCommentClickListener;
import com.example.drhello.firebaseservice.FcmNotificationsSender;
import com.example.drhello.ui.chats.StateOfUser;
import com.example.drhello.connectionnewtwork.CheckNetwork;
import com.example.drhello.firebaseinterface.MyCallBackListenerComments;
import com.example.drhello.firebaseinterface.MyCallBackWriteComment;
import com.example.drhello.firebaseinterface.MyCallbackUser;
import com.example.drhello.model.UserAccount;
import com.example.drhello.textclean.RequestPermissions;
import com.example.drhello.model.CommentModel;
import com.example.drhello.ui.writepost.ShowImageActivity;
import com.example.drhello.viewmodel.CommentViewModel;
import com.example.drhello.model.Posts;
import com.example.drhello.R;
import com.example.drhello.adapter.WriteCommentAdapter;
import com.example.drhello.databinding.ActivityInsideCommentBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.ml.modeldownloader.CustomModel;
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions;
import com.google.firebase.ml.modeldownloader.DownloadType;
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader;

import org.tensorflow.lite.Interpreter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class InsideCommentActivity extends AppCompatActivity implements OnCommentClickListener {
    @SuppressLint("StaticFieldLeak")
    public static ActivityInsideCommentBinding commentBinding;

    private static final int Gallary_REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST_CODE = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private FirebaseFirestore db;
    private final ArrayList<CommentModel> commentModels = new ArrayList<>();
    private CommentViewModel commentViewModel;
    private WriteCommentAdapter writeCommentAdapter;
    private Bitmap bitmap;
    private CommentModel commentModel, commentModel2;
    private Posts posts;
    private boolean check_img = false;
    private RequestPermissions requestPermissions;
    AsyncTaskD asyncTaskDownload;
    PyObject main_program;
    float prop;
    public static ShowDialogPython showDialogPython;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inside_comment);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            getWindow().setStatusBarColor(Color.WHITE);
        }

        requestPermissions = new RequestPermissions(InsideCommentActivity.this, InsideCommentActivity.this);
        commentBinding = DataBindingUtil.setContentView(this, R.layout.activity_inside_comment);

        commentBinding.recycleComments.setNestedScrollingEnabled(true);

        commentBinding.rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = commentBinding.rootView.getRootView().getHeight() - commentBinding.rootView.getHeight();

                if (heightDiff > 400) { // Value should be less than keyboard's height
                    Log.e("MyActivity", "keyboard opened");
                    if (check_img == false) {
                        commentBinding.constraintSendComment.setVisibility(View.VISIBLE);
                    }
                } else {
                    Log.e("MyActivity", "keyboard closed");
                    String text = commentBinding.editMessage.getText().toString().trim();

                    if (check_img == false) {
                        if (bitmap != null) {
                            commentBinding.relImageComment.setVisibility(View.VISIBLE);
                        } else {
                            commentBinding.relImageComment.setVisibility(View.GONE);
                        }
                        commentBinding.linOptionComment.setVisibility(View.VISIBLE);
                        if (!text.isEmpty() || bitmap != null) {
                            commentBinding.constraintSendComment.setVisibility(View.VISIBLE);
                        } else {
                            commentBinding.constraintSendComment.setVisibility(View.GONE);
                        }
                    } else {
                        commentBinding.relImageComment.setVisibility(View.VISIBLE);
                        commentBinding.linOptionComment.setVisibility(View.GONE);
                        commentBinding.constraintSendComment.setVisibility(View.VISIBLE);
                    }


                }
            }
        });

        db = FirebaseFirestore.getInstance();
        commentModel = (CommentModel) getIntent().getSerializableExtra("commentModel");
        posts = (Posts) getIntent().getSerializableExtra("postsModel");
        commentModel2 = new CommentModel();
        commentBinding.userName.setText(commentModel.getUser_name());
        commentBinding.userOnly.setText(commentModel.getUser_name());
        commentModel2.setPost_id(posts.getPostId());


        try {
            Glide.with(this).load(commentModel.getUser_image()).placeholder(R.drawable.ic_chat).
                    error(R.drawable.ic_chat).into(commentBinding.userImage);
        } catch (Exception e) {
            commentBinding.userImage.setImageResource(R.drawable.ic_chat);
        }

        if (commentModel.getComment_image() == null) {
            commentBinding.imageComment.setVisibility(View.GONE);
            commentBinding.userOnly.setVisibility(View.GONE);
        } else {
            try {
                Glide.with(this).load(commentModel.getComment_image()).placeholder(R.drawable.ic_chat).
                        error(R.drawable.ic_chat).into(commentBinding.imageComment);
            } catch (Exception e) {
                commentBinding.imageComment.setImageResource(R.drawable.ic_chat);
            }
            commentBinding.imageComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(InsideCommentActivity.this, ShowImageActivity.class);
                    intent.putExtra("uri_image", commentModel.getComment_image());
                    startActivity(intent);
                }
            });
        }

        if (commentModel.getComment().equals("")) {
            commentBinding.cardCommentIn.setVisibility(View.GONE);
            commentBinding.userOnly.setVisibility(View.VISIBLE);
        } else {
            commentBinding.comment.setText(commentModel.getComment());
        }

        commentViewModel = new CommentViewModel();
        commentViewModel = ViewModelProviders.of(InsideCommentActivity.this).get(CommentViewModel.class);

        commentBinding.fabImage.setOnClickListener(view -> {
            if (requestPermissions.permissionStorageRead()) {
                ActivityCompat.requestPermissions(InsideCommentActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_CAMERA_PERMISSION_CODE);
            } else {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                String[] mimetypes = {"image/*", "video/*"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                startActivityForResult(intent, Gallary_REQUEST_CODE);
            }
        });

        commentBinding.fabCamera.setOnClickListener(view -> {
            if (requestPermissions.permissionGallery()) {
                ActivityCompat.requestPermissions(InsideCommentActivity.this, new String[]{Manifest.permission.CAMERA},
                        MY_CAMERA_PERMISSION_CODE);
            } else {
                commentBinding.showImage.setVisibility(View.VISIBLE);
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
            }
        });

        commentBinding.imageSend.setOnClickListener(view -> {
            if (CheckNetwork.getConnectivityStatusString(InsideCommentActivity.this) == 1) {
                if (bitmap != null) {
                    byte[] bytesOutImg;
                    commentModel2.setComment(commentBinding.editMessage.getText().toString());
                    commentModel2.setDate(getDateTime());
                    ByteArrayOutputStream bytesStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytesStream);
                    bytesOutImg = bytesStream.toByteArray();
                    asyncTaskDownload = new AsyncTaskD(bytesOutImg, commentModel2.getComment(), "uploadImages");
                    asyncTaskDownload.execute();
                    Log.e("image : ", "EROR");
                } else {
                    Log.e("bitmap : ", bitmap + "");
                    commentModel2.setComment_image(null);
                    commentModel2.setComment(commentBinding.editMessage.getText().toString());
                    commentModel2.setDate(getDateTime());
                    asyncTaskDownload = new AsyncTaskD(null, commentModel2.getComment(), "");
                    asyncTaskDownload.execute();
                }
                check_img = false;
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(commentBinding.editMessage.getWindowToken(), 0);

            } else {
                Toast.makeText(InsideCommentActivity.this, "Please, Check Internet", Toast.LENGTH_SHORT).show();
            }
        });

        readDataComments(new MyCallBackWriteComment() {
            @Override
            public void onCallback(Task<QuerySnapshot> task) {
                commentModels.clear();

                for (QueryDocumentSnapshot document : task.getResult()) {
                    CommentModel commentModel = document.toObject(CommentModel.class);
                    commentModels.add(commentModel);

                    writeCommentAdapter = new WriteCommentAdapter(InsideCommentActivity.this, commentModels,
                            InsideCommentActivity.this, getSupportFragmentManager(),"inside");
                    commentBinding.recycleComments.setAdapter(writeCommentAdapter);

                }

                readDataUser(new MyCallbackUser() {
                    @Override
                    public void onCallback(DocumentSnapshot documentSnapshot) {
                        UserAccount userAccount = documentSnapshot.toObject(UserAccount.class);
                        commentModel2.setUser_image(userAccount.getImg_profile());
                        commentModel2.setUser_id(userAccount.getId());
                        commentModel2.setUser_name(userAccount.getName());
                        asyncTaskDownload = new AsyncTaskD(null, "comment", "first");
                        asyncTaskDownload.execute();
                    }
                });

            }
        });

        commentBinding.backComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        commentBinding.btnCancelComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                check_img = false;
                bitmap = null;
                commentBinding.constraintSendComment.setVisibility(View.GONE);
                commentBinding.linOptionComment.setVisibility(View.VISIBLE);
                commentBinding.relImageComment.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (commentModels != null) {
            readDataCommentsListener(new MyCallBackListenerComments() {
                @Override
                public void onCallBack(QuerySnapshot value) {
                    commentModels.clear();
                    for (DocumentSnapshot document : value.getDocuments()) {
                        CommentModel commentModel = document.toObject(CommentModel.class);
                        commentModels.add(commentModel);
                    }
                    writeCommentAdapter = new WriteCommentAdapter(InsideCommentActivity.this, commentModels,
                            InsideCommentActivity.this, getSupportFragmentManager(),"inside");
                    commentBinding.recycleComments.setAdapter(writeCommentAdapter);
                }
            });
        }
    }

    public void readDataCommentsListener(MyCallBackListenerComments myCallback) {
        db.collection("posts").document(posts.getPostId()).
                collection("comments").document(commentModel.getComment_id())
                .collection("InsideComments")
                .orderBy("date", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    commentModels.clear();
                    Log.e("clear : ", "exception");

                    myCallback.onCallBack(value);
                });
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            check_img = true;
            try {
                bitmap = null;
                bitmap = (Bitmap) data.getExtras().get("data");
                if (bitmap != null) {
                    commentBinding.showImage.setImageBitmap(bitmap);
                    commentBinding.relImageComment.setVisibility(View.VISIBLE);
                    commentBinding.linOptionComment.setVisibility(View.GONE);
                } else {
                    commentBinding.relImageComment.setVisibility(View.GONE);
                    commentBinding.linOptionComment.setVisibility(View.VISIBLE);
                    ;
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
                    commentBinding.showImage.setImageBitmap(bitmap);
                    commentBinding.relImageComment.setVisibility(View.VISIBLE);
                    commentBinding.linOptionComment.setVisibility(View.GONE);
                } else {
                    commentBinding.relImageComment.setVisibility(View.GONE);
                    commentBinding.linOptionComment.setVisibility(View.VISIBLE);
                    ;
                }
            } catch (IOException e) {
                Log.e("gallary exception: ", e.getMessage());
            }
        }
        // Toast.makeText(getBaseContext(), "Canceled", Toast.LENGTH_SHORT).show();

    }

    public void readDataUser(MyCallbackUser myCallback) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            //   showDialogPython = new ShowDialogPython(InsideCommentActivity.this,InsideCommentActivity.this.getLayoutInflater(),"upload");

            FirebaseFirestore.getInstance().collection("users")
                    .document(currentUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    myCallback.onCallback(documentSnapshot);
                }
            });
        }
    }

    public void readDataComments(MyCallBackWriteComment myCallback) {
        showDialogPython = new ShowDialogPython(InsideCommentActivity.this, InsideCommentActivity.this.getLayoutInflater(), "upload");

        db.collection("posts").document(posts.getPostId()).
                collection("comments").document(commentModel.getComment_id())
                .collection("InsideComments").orderBy("date", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.e("task : ", " tast");
                    if (task.isSuccessful()) {
                        myCallback.onCallback(task);
                        Log.e("task : ", " tast");
                    } else {
                        Log.e("noti error", task.getException().getMessage());
                    }
                }
            }
        });
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.US);
        Date date = new Date();
        return dateFormat.format(date);
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

    @Override
    public void onClickComment(CommentModel commentModel) {

    }

    @Override
    public void selectedReaction(String reaction, CommentModel commentModel) {

    }

    @Override
    public void onClickReaction(CommentModel commentModel) {

    }

    @Override
    public void onClickIamge(String url) {
        Intent intent = new Intent(InsideCommentActivity.this, ShowImageActivity.class);
        intent.putExtra("uri_image", url);
        startActivity(intent);
    }

    public class AsyncTaskD extends AsyncTask<String, String, String> {

        String text;
        String action;
        byte[] bytesOutImg;


        public AsyncTaskD(byte[] bytesOutImg, String text, String action) {
            this.text = text;
            this.action = action;
            this.bytesOutImg = bytesOutImg;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!action.equals("first")) {
                showDialogPython = new ShowDialogPython(InsideCommentActivity.this, InsideCommentActivity.this.getLayoutInflater(), "upload");
            }
        }

        @Override
        protected String doInBackground(String... f_url) {
            if (action.equals("first")) {
                if (!Python.isStarted()) {
                    Python.start(new AndroidPlatform(InsideCommentActivity.this));//error is here!
                }
                final Python py = Python.getInstance();
                main_program = py.getModule("prolog");
            } else {
                if (!text.isEmpty()) {
                    modelFire(text);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            if (action.equals("first")) {
                Log.e("first ", " first");
                showDialogPython.dismissDialog();
            } else if (prop >= 0 && prop < 0.5) {
                if (action.equals("uploadImages")) {
                    commentViewModel.uploadCommentInside(db, bytesOutImg, posts, commentModel, commentModel2);
                    commentBinding.editMessage.setText("");
                    bitmap = null;
                    bytesOutImg = null;
                } else {
                    commentViewModel.uploadCommentInside(db, null, posts, commentModel, commentModel2);
                    commentBinding.editMessage.setText("");
                }
            } else if (prop >= 0.5) {
                Log.e("prop failed: ", prop + "");
                showDialogPython.dismissDialog();
            } else if (action.equals("uploadImages")) {
                commentViewModel.uploadCommentInside(db, bytesOutImg, posts, commentModel, commentModel2);
                commentBinding.editMessage.setText("");
                bitmap = null;
                bytesOutImg = null;
            }
        }
    }

    private void modelFire(String text) {
        String result = main_program.callAttr("predictComment", text, getKeyboardLanguage(text)).toString();
        result = result.replace("[", "").replace("]", "");
        String[] strings = result.split(", ");
        Log.e("result: ", result);
        float[][] input = new float[1][300];
        for (int i = 0; i < strings.length; i++) {
            input[0][i] = Float.parseFloat(strings[i]);
        }
        CustomModelDownloadConditions conditions = new CustomModelDownloadConditions.Builder()
                .requireWifi()
                .build();
        Task<CustomModel> model;
        if (getKeyboardLanguage(text).equals("EN")) {
            Log.e("lang : ", "EN");
            model = FirebaseModelDownloader.getInstance()
                    .getModel("HateAbusiveModelEN", DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND, conditions);
        } else {
            Log.e("lang : ", "AR");
            model = FirebaseModelDownloader.getInstance()
                    .getModel("arabicHateOff", DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND, conditions);
        }

        model.addOnSuccessListener(new OnSuccessListener<CustomModel>() {
            @Override
            public void onSuccess(CustomModel model) {
                File modelFile = model.getFile();
                Log.e("modelFile : ", modelFile + "");
                if (modelFile != null) {
                    Interpreter interpreter = new Interpreter(modelFile);
                    int bufferSize = 1 * java.lang.Float.SIZE / java.lang.Byte.SIZE;
                    ByteBuffer modelOutput = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder());
                    interpreter.run(input, modelOutput);
                    modelOutput.rewind();
                    FloatBuffer probabilities = modelOutput.asFloatBuffer();
                    prop = probabilities.get(0);
                    Log.e("MAX : ", prop * 100 + "");
                }
            }
        });
    }

    public static String getKeyboardLanguage(String s) {
        for (int i = 0; i < s.length(); ) {
            int c = s.codePointAt(i);
            if (c >= 0x0600 && c <= 0x06E0)
                return "AR";
            i += Character.charCount(c);
        }
        return "EN";
    }


}