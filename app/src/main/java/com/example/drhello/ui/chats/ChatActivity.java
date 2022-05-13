package com.example.drhello.ui.chats;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.loader.content.CursorLoader;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordClickListener;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordPermissionHandler;
import com.example.drhello.MyCallbackDeleteItem;
import com.example.drhello.MyCallbackDeletePost;
import com.example.drhello.model.AddPersonModel;
import com.example.drhello.model.LastChat;
import com.example.drhello.connectionnewtwork.CheckNetwork;
import com.example.drhello.connectionnewtwork.NetworkChangeListener;
import com.example.drhello.firebaseinterface.MyCallBackChannel;
import com.example.drhello.firebaseinterface.MyCallBackChats;
import com.example.drhello.firebaseinterface.MyCallBackFriend;
import com.example.drhello.firebaseinterface.MyCallBackMessage;
import com.example.drhello.firebaseinterface.MyCallbackUser;
import com.example.drhello.firebaseservice.FcmNotificationsSender;
import com.example.drhello.model.LastMessages;
import com.example.drhello.R;
import com.example.drhello.model.Posts;
import com.example.drhello.textclean.RequestPermissions;
import com.example.drhello.adapter.Recycle_Message_Adapter;
import com.example.drhello.databinding.ActivityChatBinding;
import com.example.drhello.model.ChatChannel;
import com.example.drhello.model.ChatModel;
import com.example.drhello.model.UserAccount;
import com.example.drhello.ui.main.MainActivity;
import com.example.drhello.ui.profile.ProfileActivity;
import com.example.drhello.ui.writepost.WritePostsActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity implements com.google.android.gms.location.LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final int Gallary_REQUEST_CODE = 1, SONGS_REQUEST_CODE = 2, PERMISSION_ALL_STORAGE = 5000;
    private static final int CAMERA_REQUEST_CODE = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private final int REQUESTPERMISSIONSFINE_LOCATION = 1001;
    private final int REQUESTPERMISSIONSLOCATION = 10;
    private FirebaseAuth mAuth;
    private Location mLocation;
    private FirebaseFirestore db;
    public static ProgressDialog mProgress;
    private ActivityChatBinding activityChatBinding;
    private String geoUri = "", iDChannel = "";
    private ArrayList<ChatModel> chatsArrayList = new ArrayList<>();
    private String idFriend;
    private UserAccount userAccountme, friendAccount;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
    public static MediaRecorder recorder = null;
    private File recordFile;
    private int currentFormat = 0;
    private int output_formats[] = {MediaRecorder.OutputFormat.MPEG_4, MediaRecorder.OutputFormat.THREE_GPP};
    private Recycle_Message_Adapter recycle_message_adapter;
    private Bitmap bitmap;
    private RequestPermissions requestPermissions;
    private double Lat = 0.0, Lon = 0.0;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    StorageReference storageReferenceAudio, storageReferenceImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            getWindow().setStatusBarColor(Color.WHITE);
        }
        mProgress = new ProgressDialog(this);
        requestPermissions = new RequestPermissions(ChatActivity.this, ChatActivity.this);
        checkRunTimePermission();
        init();
        playRecordVoice();
        activityChatBinding.editMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    activityChatBinding.imageviewSend.setVisibility(View.VISIBLE);
                } else {
                    activityChatBinding.imageviewSend.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (activityChatBinding.editMessage.getText().toString().length() > 0) {
                    activityChatBinding.imgCamera.setVisibility(View.GONE);
                    activityChatBinding.recordButton.setVisibility(View.GONE);
                    activityChatBinding.imageviewSend.setVisibility(View.VISIBLE);
                } else {
                    activityChatBinding.imgCamera.setVisibility(View.VISIBLE);
                    activityChatBinding.recordButton.setVisibility(View.VISIBLE);
                    activityChatBinding.imageviewSend.setVisibility(View.GONE);
                }
            }
        });

        activityChatBinding.imgBackChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recycle_message_adapter != null) {
                    recycle_message_adapter.stoppingPlayer();
                }
                finish();
            }
        });

        activityChatBinding.imgAttachFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog();
            }
        });

        activityChatBinding.imgCamera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (requestPermissions.permissionGallery()) {
                    ActivityCompat.requestPermissions(ChatActivity.this, new String[]{Manifest.permission.CAMERA},
                            MY_CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                }
            }
        });

        activityChatBinding.imageviewSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckNetwork.getConnectivityStatusString(ChatActivity.this) == 1) {
                    if (!activityChatBinding.editMessage.getText().toString().equals("")) {
                        ChatModel chatModel = new ChatModel(activityChatBinding.editMessage.getText().toString(),
                                getDateTime(), mAuth.getCurrentUser().getUid(),
                                friendAccount.getId(), "", userAccountme.getName(), "");
                        activityChatBinding.editMessage.setText("");
                        storeMessageOnFirebase(chatModel);
                        sendNotification(chatModel.getMessage());
                    } else {
                        Toast.makeText(ChatActivity.this, "Empty", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ChatActivity.this, "Please, Check Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });


        activityChatBinding.chatOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickOption();
            }
        });

    }

    private void init() {
        activityChatBinding = DataBindingUtil.setContentView(this, R.layout.activity_chat);
        getBitmapFromImage();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        recycle_message_adapter = new Recycle_Message_Adapter(chatsArrayList, ChatActivity.this, bitmap);
        activityChatBinding.rvChatFriend.setAdapter(recycle_message_adapter);
        idFriend = (String) getIntent().getSerializableExtra("friendAccount");

        String friendIdIntent = getIntent().getStringExtra("chatchannel");
        String id_massage;
        if (friendIdIntent != null) {
            id_massage = friendIdIntent;

            readDataUser(new MyCallbackUser() {
                @Override
                public void onCallback(DocumentSnapshot documentSnapshot) {
                    userAccountme = documentSnapshot.toObject(UserAccount.class);
                }
            });

        } else {
            id_massage = idFriend;
            userAccountme = (UserAccount) getIntent().getSerializableExtra("userAccount");
        }

        readDataFriendAccount(new MyCallBackFriend() {
            @Override
            public void onCallBack(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.e("task : ", " tast");
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        friendAccount = document.toObject(UserAccount.class);

                        readDataChannelListener(new MyCallBackChannel() {
                            @Override
                            public void onCallback(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    String id_channel = Objects.requireNonNull(documentSnapshot.get("id")).toString();
                                    iDChannel = id_channel;
                                } else {
                                    DocumentReference idChannel = FirebaseFirestore.getInstance().collection("users").document();
                                    ChatChannel chatChannel = new ChatChannel(idChannel.getId());

                                    db.collection("users").
                                            document(mAuth.getCurrentUser().getUid()).
                                            collection("channels")
                                            .document(friendAccount.getId()).set(chatChannel);

                                    db.collection("users").
                                            document(friendAccount.getId()).
                                            collection("channels")
                                            .document(mAuth.getCurrentUser().getUid()).set(chatChannel);

                                    iDChannel = idChannel.getId();
                                }

                                ChatModel chatModel = (ChatModel) getIntent().getSerializableExtra("message");
                                if (chatModel != null) {
                                    Log.e("chatActivity:", chatModel.getMessage());
                                    chatModel.setDate(getDateTime());
                                    chatModel.setSenderid(mAuth.getCurrentUser().getUid());
                                    chatModel.setRecieveid(friendAccount.getId());
                                    chatModel.setNameSender(userAccountme.getName());
                                    storeMessageOnFirebase(chatModel);
                                }

                                readDataMessagesListener(new MyCallBackMessage() {
                                    @Override
                                    public void onCallback(Task<QuerySnapshot> task) {
                                        for (DocumentSnapshot document : task.getResult().getDocuments())
                                            if (document.exists()) {
                                                ChatModel chatModel = document.toObject(ChatModel.class);
                                                Log.e("all : ", chatModel.getDate());
                                                chatsArrayList.add(chatModel);
                                            }
                                        recycle_message_adapter.updateMessage(chatsArrayList);
                                    }
                                }, iDChannel);
                            }
                        }, friendAccount.getId());

                        retriveDate();

                        readDataChatsListener(new MyCallBackChats() {
                            @Override
                            public void onCallBack(DocumentSnapshot value) {
                                LastMessages lastMessages = value.toObject(LastMessages.class);
                                if (lastMessages != null) {
                                    ChatModel chatModel = new ChatModel(lastMessages.getMessage(), lastMessages.getDate(),
                                            lastMessages.getSenderid(), lastMessages.getRecieveid()
                                            , lastMessages.getImage(), lastMessages.getNameSender(), lastMessages.getRecord());

                                    recycle_message_adapter.addMessage(chatModel);
                                    Log.e("chatsArray: ", chatsArrayList.get(0).getMessage());
                                    ///     chatsArrayList.add(0,entry.getValue());
                                    recycle_message_adapter.notifyItemInserted(0);
                                    if (chatModel.getSenderid().equals(mAuth.getCurrentUser().getUid())) {
                                        int totalItemCount = activityChatBinding.rvChatFriend.getAdapter().getItemCount();
                                        if (totalItemCount > 0)
                                            activityChatBinding.rvChatFriend.getLayoutManager().smoothScrollToPosition(activityChatBinding.rvChatFriend,
                                                    null, 0);
                                    }
                                }

                            }
                        }, friendAccount.getId());
                    }
                }
            }
        }, id_massage);
    }

    public void readDataUser(MyCallbackUser myCallback) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            FirebaseFirestore.getInstance().collection("users")
                    .document(currentUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    myCallback.onCallback(documentSnapshot);
                }
            });
        }
    }

    private void playRecordVoice() {

        //IMPORTANT
        activityChatBinding.recordButton.setRecordView(activityChatBinding.recordView);

        //ListenForRecord must be false ,otherwise onClick will not be called
        activityChatBinding.recordButton.setOnRecordClickListener(new OnRecordClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("RecordButton", "RECORD BUTTON CLICKED");
            }
        });

        //Cancel Bounds is when the Slide To Cancel text gets before the timer . default is 8
        activityChatBinding.recordView.setCancelBounds(8);

        activityChatBinding.recordView.setSmallMicColor(Color.parseColor("#c2185b"));

        activityChatBinding.recordView.setLessThanSecondAllowed(false);

        activityChatBinding.recordView.setSlideToCancelText("Slide To Cancel");

        activityChatBinding.recordView.setCustomSounds(R.raw.record_start, R.raw.record_finished, 0);

        activityChatBinding.recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                activityChatBinding.imgCamera.setVisibility(View.GONE);
                activityChatBinding.clEdit.setVisibility(View.GONE);
                activityChatBinding.imgAttachFile.setVisibility(View.GONE);
                recordFile = new File(getFilename());
                startRecording();
            }

            @Override
            public void onCancel() {
                activityChatBinding.imgCamera.setVisibility(View.VISIBLE);
                activityChatBinding.clEdit.setVisibility(View.VISIBLE);
                activityChatBinding.imgAttachFile.setVisibility(View.VISIBLE);
                stopRecording();
                Log.d("RecordView", "onCancel");
            }

            @Override
            public void onFinish(long recordTime, boolean limitReached) {
                stopRecording();
                uploadAudio(Uri.fromFile(new File(recordFile.getPath())));
                activityChatBinding.imgCamera.setVisibility(View.VISIBLE);
                activityChatBinding.clEdit.setVisibility(View.VISIBLE);
                activityChatBinding.imgAttachFile.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLessThanSecond() {
                activityChatBinding.imgCamera.setVisibility(View.VISIBLE);
                activityChatBinding.clEdit.setVisibility(View.VISIBLE);
                activityChatBinding.imgAttachFile.setVisibility(View.VISIBLE);
                stopRecording();

                Toast.makeText(ChatActivity.this, "OnLessThanSecond", Toast.LENGTH_SHORT).show();
                Log.d("RecordView", "onLessThanSecond");
            }
        });

        activityChatBinding.recordView.setOnBasketAnimationEndListener(new OnBasketAnimationEnd() {
            @Override
            public void onAnimationEnd() {
                Log.d("RecordView", "Basket Animation Finished");
            }
        });

        activityChatBinding.recordView.setRecordPermissionHandler(new RecordPermissionHandler() {
            @Override
            public boolean isPermissionGranted() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    return true;
                }

                if (ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ChatActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
                            , PERMISSION_ALL_STORAGE);
                } else {
                    if (ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.RECORD_AUDIO) !=
                            PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(ChatActivity.this, new String[]{Manifest.permission.RECORD_AUDIO},
                                PERMISSION_ALL_STORAGE);
                    } else {
                        Log.e("recordAudio: ", "FIRST");
                        return true;
                    }
                }

                return false;
            }
        });
    }

    private void retriveDate() {
        try {
            Glide.with(getBaseContext()).asBitmap().load(friendAccount.getImg_profile()).placeholder(R.drawable.user)
                    .error(R.drawable.user).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    activityChatBinding.profileImageChat.setImageBitmap(resource);
                    //     Log.e(" bitmap.toString : ",  resource+"");
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {
                }
            });
        } catch (Exception e) {
            activityChatBinding.profileImageChat.setImageResource(R.drawable.user);
        }

        activityChatBinding.txtNameChat.setText(friendAccount.getName());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (recycle_message_adapter != null) {
            recycle_message_adapter.stoppingPlayer();
        }
        Intent intent = new Intent(ChatActivity.this, MainActivity.class);
        intent.putExtra("chatactivity", "chatactivity");
        startActivity(intent);
    }

    private void alertDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ChatActivity.this);
        View viewDailog = LayoutInflater.from(ChatActivity.this).inflate(R.layout.alert_dialog_chat_item, null);
        alertBuilder.setView(viewDailog);

        ImageView image_audio_file = viewDailog.findViewById(R.id.image_audio_file);
        ImageView image_gallary_file = viewDailog.findViewById(R.id.image_gallary_file);
        ImageView image_location = viewDailog.findViewById(R.id.image_location);

        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();

        Window window = alertDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        wlp.y = 150;
        window.setAttributes(wlp);

        // alertDialog.setCancelable(false);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        image_audio_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ChatActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_ALL_STORAGE);
                } else {

                    Intent song_intent = new Intent();
                    song_intent.setAction(android.content.Intent.ACTION_PICK);
                    song_intent.setData(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(song_intent, SONGS_REQUEST_CODE);
                    alertDialog.dismiss();
                    // Permission has already been granted
                }

            }
        });

        image_gallary_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                String[] mimetypes = {"image/*", "video/*"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                startActivityForResult(intent, Gallary_REQUEST_CODE);
                alertDialog.dismiss();

            }
        });

        image_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //////////////////to get location
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED ||
                            ActivityCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                                    == PackageManager.PERMISSION_GRANTED ||
                            ActivityCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                                    == PackageManager.PERMISSION_GRANTED) {
                        if (isGPSEnabled(ChatActivity.this)) {
                            geoUri = "http://www.google.com/maps/place/" + Lat + "," + Lon + "";
                            activityChatBinding.editMessage.setText(activityChatBinding.editMessage.getText() + " " + geoUri);
                            Log.e("google", geoUri);
                        } else {
                            requestGps();
                        }
                    } else {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                                REQUESTPERMISSIONSLOCATION);
                    }
                } else {
                    if (ActivityCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED ||
                            ActivityCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                                    == PackageManager.PERMISSION_GRANTED) {
                        if (isGPSEnabled(ChatActivity.this)) {
                            geoUri = "http://www.google.com/maps/place/" + Lat + "," + Lon + "";
                            activityChatBinding.editMessage.setText(geoUri);
                            Log.e("google", geoUri);
                        } else {
                            requestGps();
                        }
                    }
                }
                alertDialog.dismiss();

            }
        });

    }


    private void sendNotification(String text) {
        FcmNotificationsSender fcmNotificationsSender = new FcmNotificationsSender(friendAccount.getTokenID(),
                mAuth.getCurrentUser().getUid(),
                "Message",
                userAccountme.getName() + " : " + text,
                getApplicationContext(),
                ChatActivity.this,
                userAccountme.getImg_profile(), userAccountme.getId());
        fcmNotificationsSender.SendNotifications();
    }

    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                } else {
                    Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
                }

            case PERMISSION_ALL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.RECORD_AUDIO) ==
                            PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                    PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                                    PackageManager.PERMISSION_GRANTED) {

                        Log.e("recordAudio : ", "second time");
                        //  recordAudio.startRecording();
                        return;
                    }
                }
                break;
            case REQUESTPERMISSIONSLOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED ||
                            ActivityCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                                    == PackageManager.PERMISSION_GRANTED ||
                            ActivityCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                                    == PackageManager.PERMISSION_GRANTED) {
                        Log.e("checkRunTime : ", "true");
                        // make a buidler for GoogleApiClient //
                        createLocationRequest();

                        return;
                    }
                } else {
                    Log.e("onRequestPermissions : ", "false");

                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) ChatActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        // If User Checked 'Don't Show Again' checkbox for runtime permission, then navigate user to Settings
                        AlertDialog.Builder dialog = new AlertDialog.Builder(ChatActivity.this);
                        dialog.setTitle("Permission Required");
                        dialog.setCancelable(false);
                        dialog.setMessage("You have to Allow permission to access user location");
                        dialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package",
                                        ChatActivity.this.getPackageName(), null));
                                //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivityForResult(i, REQUESTPERMISSIONSFINE_LOCATION);
                            }
                        });
                        AlertDialog alertDialog = dialog.create();
                        alertDialog.show();
                    }
                    //code for deny
                }
        }
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss:SS a", Locale.US);
        Date date = new Date();
        return dateFormat.format(date);
    }

    public void readDataFriendAccount(MyCallBackFriend myCallback, String idmassage) {
        db.collection("users").whereEqualTo("id", idmassage)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    myCallback.onCallBack(task);
                }
            }
        });
    }

    public void readDataChatsListener(MyCallBackChats myCallback, String idFriend) {
        db.collection("users").document(mAuth.getCurrentUser().getUid())
                .collection("lastmessage").document(idFriend)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        Log.e("task : ", " tast");
                        if (mAuth.getCurrentUser() != null) {
                            myCallback.onCallBack(value);
                        }
                    }
                });
    }

    public void readDataMessagesListener(MyCallBackMessage myCallback, String iDChannel) {
        db.collection("chatsChannel").document(iDChannel).collection("messages").
                orderBy("date", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                chatsArrayList.clear();
                Log.e("task : ", " tast");
                if (mAuth.getCurrentUser() != null) {
                    myCallback.onCallback(task);
                }
            }
        });

    }

    public void readDataChannelListener(MyCallBackChannel myCallback, String id_friend) {
        db.collection("users").
                document(id_friend).
                collection("channels")
                .document(mAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                chatsArrayList.clear();
                Log.e("task : ", " tast");
                if (mAuth.getCurrentUser() != null) {
                    myCallback.onCallback(documentSnapshot);
                }
            }
        });

    }

    private void getBitmapFromImage() {
        try {
            bitmap = ((BitmapDrawable) activityChatBinding.profileImageChat.getDrawable()).getBitmap();
        } catch (Exception E) {
            bitmap = Bitmap.createBitmap(activityChatBinding.profileImageChat.getDrawable().getIntrinsicWidth(), activityChatBinding.profileImageChat.getDrawable().getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            activityChatBinding.profileImageChat.getDrawable().setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            activityChatBinding.profileImageChat.getDrawable().draw(canvas);
        }
    }

    private void storeMessageOnFirebase(ChatModel chatModel) {

        db.collection("chatsChannel").
                document(iDChannel).
                collection("messages")
                .add(chatModel);

        LastMessages lastMessages = new LastMessages("", friendAccount.getImg_profile(),
                friendAccount.getName(),
                chatModel.getDate(), chatModel.getImage(), chatModel.getMessage(),
                chatModel.getNameSender(), chatModel.getRecieveid()
                , chatModel.getRecord(), chatModel.getSenderid());

        db.collection("users").
                document(mAuth.getCurrentUser().getUid())
                .collection("lastmessage")
                .document(friendAccount.getId())
                .set(lastMessages);

        db.collection("users").
                document(friendAccount.getId())
                .collection("lastmessage")
                .document(mAuth.getCurrentUser().getUid())
                .set(lastMessages);

        LastChat lastChat = new LastChat(friendAccount.getId(), friendAccount.getImg_profile(),
                chatModel.getDate(), chatModel.getMessage(), chatModel.getNameSender());

        if (chatModel.getMessage().equals("") && chatModel.getRecord().equals("")) { // image
            lastChat.setMessage("image Message");
        } else if (chatModel.getMessage().equals("") && chatModel.getImage().equals("")) { // record
            lastChat.setMessage("record Message");
        } else {
            lastChat.setMessage(chatModel.getMessage());
        }


        Map<String, LastChat> map = friendAccount.getMap();
        map.put(userAccountme.getId(), lastChat);
        friendAccount.setMap(map);

        map = userAccountme.getMap();
        map.put(friendAccount.getId(), lastChat);
        userAccountme.setMap(map);

        db.collection("users").
                document(mAuth.getCurrentUser().getUid())
                .set(userAccountme);

        db.collection("users").
                document(friendAccount.getId()).set(friendAccount);

    }

    private void uploadImage(Bitmap bitmap) {
        mProgress.setMessage("Uploading..");
        mProgress.setCancelable(false);
        mProgress.show();
        ByteArrayOutputStream output_image = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output_image);
        byte[] data_image = output_image.toByteArray();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("images/imagesChat/" + userAccountme.getId() + "/" + UUID.nameUUIDFromBytes(data_image));

        storageReference.putBytes(data_image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.e("uri.toString() : ", uri.toString());
                        ChatModel chatModel = new ChatModel("", getDateTime(),
                                mAuth.getCurrentUser().getUid(), friendAccount.getId(), uri.toString(),
                                userAccountme.getName(), "");
                        storeMessageOnFirebase(chatModel);
                        sendNotification("Send an Image");
                        mProgress.dismiss();
                    }
                });
                Log.e("uri ", "Successful Upload");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(getApplicationContext(),"UnSuccessful Upload ",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            try {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                if (CheckNetwork.getConnectivityStatusString(ChatActivity.this) == 1) {
                    uploadImage(bitmap);
                } else {
                    Toast.makeText(ChatActivity.this, "Please, Check Internet", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                Log.e("camera exception: ", e.getMessage());
            }
        } else if (requestCode == Gallary_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getBaseContext().getContentResolver(), data.getData());
                if (CheckNetwork.getConnectivityStatusString(ChatActivity.this) == 1) {
                    uploadImage(bitmap);
                } else {
                    Toast.makeText(ChatActivity.this, "Please, Check Internet", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                Log.e("gallary exception: ", e.getMessage());
            }
        } else if (requestCode == SONGS_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            Log.e("uri : ", uri + "");
            if (CheckNetwork.getConnectivityStatusString(ChatActivity.this) == 1) {
                uploadAudio(Uri.fromFile(new File(getRealPathFromURI(uri))));
            } else {
                Toast.makeText(ChatActivity.this, "Please, Check Internet", Toast.LENGTH_SHORT).show();
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // Toast.makeText(getBaseContext(), "Canceled", Toast.LENGTH_SHORT).show();
        }

    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(getApplicationContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public void uploadAudio(Uri uri) {
        mProgress.setMessage("Uploading..");
        mProgress.setCancelable(false);
        mProgress.show();
        StorageReference storageReference = FirebaseStorage.getInstance().
                getReference().child("audios/audiosChat/" + userAccountme.getId() + "/"
                + uri.getPath());
        Log.e("audio.getPath() : ", uri.getPath());

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.e("uri.toString() : ", uri.toString());
                        ChatModel chatModel = new ChatModel("", getDateTime(),
                                mAuth.getCurrentUser().getUid(), friendAccount.getId(), "",
                                userAccountme.getName(), uri.toString());
                        storeMessageOnFirebase(chatModel);
                        sendNotification("Send an Record");
                        if (recordFile != null) {
                            recordFile.delete();
                        }
                        mProgress.dismiss();

                    }
                });
                Log.e("uri ", "Successful Upload");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(getApplicationContext(),"UnSuccessful Upload ",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        StateOfUser stateOfUser = new StateOfUser();
        stateOfUser.changeState("Online");
        if (mGoogleApiClient != null && mLocationRequest != null) {
            mGoogleApiClient.connect();
            if (mGoogleApiClient.isConnected()) {
                startLocationUpdates();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        StateOfUser stateOfUser = new StateOfUser();
        stateOfUser.changeState("Offline");
        stopLocationUpdate();
    }

    public String getFilename() {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
                , AUDIO_RECORDER_FOLDER);
        Log.e("file : ", file.getAbsolutePath());
        if (!file.exists()) {
            file.mkdirs();
        }

        return (file.getAbsolutePath() + "/" + "a" + ".mp3");
    }

    public void startRecording() {
        Log.e("MediaRecorder", "onStart");
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(output_formats[currentFormat]);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(getFilename());
        Log.e("recorder", "onStart");
        try {
            recorder.prepare();
            recorder.start();
            Log.e("prepare : ", "start");
        } catch (IllegalStateException e) {
            Log.e("prepareERR:", e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("IOException : ", e.getMessage());
            e.printStackTrace();
        }
    }

    public void stopRecording() {
        try {
            recorder.stop();
            recorder.release();
            recorder = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Lat = location.getLatitude();
        Lon = location.getLongitude();
        Log.e("onLocationChanged : ", Lat + "    " + Lon);

    }

    public boolean isGPSEnabled(Context mContext) {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public void checkRunTimePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                Log.e("checkRunTime : ", "true");
                // make a buidler for GoogleApiClient //
                createLocationRequest();

            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUESTPERMISSIONSLOCATION);
            }
        } else {
            if (ActivityCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                // make a buidler for GoogleApiClient //
                createLocationRequest();
            }
        }


    }


    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        switch (requestCode) {
            case REQUESTPERMISSIONSFINE_LOCATION:
                Log.e("startActivity : ", "startActivity");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED ||
                            ActivityCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                                    == PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        Log.e("startActivity : ", "true");
                        // make a buidler for GoogleApiClient //

                        createLocationRequest();

                    } else {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION}, REQUESTPERMISSIONSLOCATION);
                        Log.e("startActivity : ", "false");
                    }
                }
                break;
            default:
                break;
        }
    }

    @SuppressLint("MissingPermission")
    protected void createLocationRequest() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(1000 * 60);  // 1 Minute
            mLocationRequest.setFastestInterval(1000 * 5); // 5 SECONDS
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            mLocationRequest.setSmallestDisplacement(1);

            if (mGoogleApiClient == null) {
                mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
            }
        }

    }

    // must declare methods //
    public void onStart() {
        super.onStart();
        Log.e("onStart : ", "onStart");
        if (mGoogleApiClient != null && mLocationRequest != null) {
            mGoogleApiClient.connect();
            if (mGoogleApiClient.isConnected()) {
                startLocationUpdates();
            }
        }
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, intentFilter);

    }

    public void onStop() {
        super.onStop();
        Log.e("onStop : ", "onStop");
        stopLocationUpdate();
        unregisterReceiver(networkChangeListener);
    }

    // create method for location update //
    @SuppressLint("MissingPermission")
    protected void startLocationUpdates() {
        Log.e("startLocationUpdates : ", "startLocationUpdates");
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        }
    }

    protected void stopLocationUpdate() {
        Log.e("stopLocationUpdate : ", "stopLocationUpdate");
        if (mGoogleApiClient != null && mLocationRequest != null) {
            if (mGoogleApiClient.isConnected())
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    private void requestGps() {
        Log.e("requestGps: ", geoUri);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                //final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.e("LocationStatus : ", "SUCCESS");

                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        //...
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.e("LocationStatus : ", "RESOLUTION_REQUIRED");

                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    ChatActivity.this,
                                    2000);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        //...
                        break;
                }
            }
        });
    }

    // Must Declare Callback Methods //
    @SuppressLint("MissingPermission")
    public void onConnected(Bundle args0) {
        Log.e("onConnected : ", "onConnected");
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            requestGps();

            mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLocation != null) {
                Lat = mLocation.getLatitude();
                Lon = mLocation.getLongitude();
                //mapFragment.getMapAsync(this);
            }

            if (mGoogleApiClient != null && mLocationRequest != null && mGoogleApiClient.isConnected()) {
                startLocationUpdates();
            }
        }
    }

    public void onConnectionFailed(ConnectionResult result) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }


    public void onClickOption() {
        LayoutInflater factory = LayoutInflater.from(ChatActivity.this);
        final View deleteDialogView = factory.inflate(R.layout.alertdialogposts, null);
        final AlertDialog deleteDialog = new AlertDialog.Builder(ChatActivity.this).create();
        deleteDialog.setView(deleteDialogView);
        Button btn_delete = deleteDialogView.findViewById(R.id.btn_delete);
        Button btn_block = deleteDialogView.findViewById(R.id.btn_modify);
        Button btn_view_profile = deleteDialogView.findViewById(R.id.btn_save);

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  deletePost(posts);
                deleteDialog.dismiss();
                deleteChat();
            }
        });

        btn_view_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                deleteDialog.dismiss();
                Intent intent = new Intent(ChatActivity.this, ProfileActivity.class);
                intent.putExtra("userId", friendAccount.getId());
                startActivity(intent);

                 */
                deleteDialog.dismiss();

            }
        });


        deleteDialog.show();
    }

    private void deleteChat() {
        mProgress.setMessage("Loading..");
        mProgress.setCancelable(false);
        mProgress.show();
        storageReferenceAudio = FirebaseStorage.getInstance().
                getReference().child("audios/audiosChat/" + userAccountme.getId());

        storageReferenceImages = FirebaseStorage.getInstance().getReference()
                .child("images/imagesChat/" + userAccountme.getId());

        myCallBackListItemImages(new MyCallbackDeletePost() {
            @Override
            public void myCallBack(ListResult listResult) {
                int numImages = listResult.getItems().size();
                Log.e("first images : ", "images  " + numImages);
                if (numImages > 0) {  // 5 IMAGES
                    for (StorageReference item : listResult.getItems()) {  // 5 IMAGES
                        // All the items under listRef.
                        Log.e("img in loop1 : ", "images" + numImages);
                        numImages = numImages - 1;
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference(item.getPath());
                        int finalNumImages = numImages;
                        myCallBackDeleteItem(new MyCallbackDeleteItem() {
                            @Override
                            public void myCallBackItem(Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.e("img in loop2: ", finalNumImages + "");
                                }
                            }
                        }, storageReference);
                        if (numImages == 0) { // finish delete images
                            deleteAudios();
                        }
                    }
                } else {
                    deleteAudios();
                }
            }
        }, storageReferenceImages);
    }

    private void deleteAudios() {
        myCallBackListItemAudios(new MyCallbackDeletePost() {
            @Override
            public void myCallBack(ListResult listResult) {
                int numaudios = listResult.getItems().size();
                Log.e("first Audios : ", "audio" + numaudios);
                if (numaudios > 0) {
                    for (StorageReference item : listResult.getItems()) {   // 5 audios
                        // All the items under listRef.
                        numaudios = numaudios - 1;
                        Log.e("aud in loop1 : ", numaudios + "");
                        StorageReference storageReference = FirebaseStorage.getInstance().
                                getReference(item.getPath());
                        int finalNumImages = numaudios;
                        myCallBackDeleteItem(new MyCallbackDeleteItem() {
                            @Override
                            public void myCallBackItem(Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.e("aud in loop2 : ", finalNumImages + "");
                                }
                            }
                        }, storageReference);
                        if (numaudios == 0) {  // finish delete audios
                            Log.e("numaudios= 0 : ", "after deleteChatAndChatChannel");
                            myCallBackDeleteChatChannel();
                        }
                    }
                } else {
                    Log.e("numaudios= 0 : ", "before deleteChatAndChatChannel");
                    myCallBackDeleteChatChannel();
                }
            }
        }, storageReferenceAudio);

    }


    private void myCallBackListItemImages(MyCallbackDeletePost myCallbackDeletePost
            , StorageReference listRef) {
        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        myCallbackDeletePost.myCallBack(listResult);   // 5 IMAGES
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Uh-oh, an error occurred!
                        Log.e("failed : ", "images  " + 0);
                        deleteAudios();
                    }
                });
    }

    private void myCallBackListItemAudios(MyCallbackDeletePost myCallbackDeletePost
            , StorageReference listRef) {
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
                        Log.e("failed : ", "audio  " + 0);
                        myCallBackDeleteChatChannel();
                    }
                });
    }

    private void myCallBackDeleteItem(MyCallbackDeleteItem myCallbackDeleteItem,
                                      StorageReference storageReference) {
        storageReference.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        myCallbackDeleteItem.myCallBackItem(task);
                    }
                });
    }

    private void myCallBackDeleteChatChannel() {
        db.collection("users").
                document(mAuth.getCurrentUser().getUid()).
                collection("channels")
                .document(friendAccount.getId())
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        db.collection("users").
                                document(friendAccount.getId()).
                                collection("channels")
                                .document(mAuth.getCurrentUser().getUid())
                                .delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        db.collection("users").
                                                document(friendAccount.getId()).
                                                collection("lastmessage")
                                                .document(mAuth.getCurrentUser().getUid())
                                                .delete()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        db.collection("users").
                                                                document(mAuth.getCurrentUser().getUid()).
                                                                collection("lastmessage")
                                                                .document(friendAccount.getId())
                                                                .delete()
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        Log.e("DELTET iDChannel : ", iDChannel);
                                                                        db.collection("chatsChannel")
                                                                                .document(iDChannel)
                                                                                .collection("messages").
                                                                                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                int num = task.getResult().size();
                                                                                Log.e("DELTET num : ", num + "");
                                                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                                                    num = num - 1;
                                                                                    db.collection("chatsChannel")
                                                                                            .document(iDChannel)
                                                                                            .collection("messages").document(document.getId())
                                                                                            .delete();
                                                                                    Log.e("delete num : ", num + "");
                                                                                    if (num == 0) {
                                                                                        Log.e("num  0 : ", num + "");
                                                                                        Map<String, AddPersonModel> friendsmap = userAccountme.getFriendsmap();
                                                                                        friendsmap.remove(friendAccount.getId());
                                                                                        userAccountme.setFriendsmap(friendsmap);

                                                                                        Map<String, AddPersonModel> friendsmap2 = friendAccount.getFriendsmap();
                                                                                        friendsmap2.remove(userAccountme.getId());
                                                                                        friendAccount.setFriendsmap(friendsmap);

                                                                                        Map<String, LastChat> map = userAccountme.getMap();
                                                                                        map.remove(friendAccount.getId());
                                                                                        userAccountme.setMap(map);

                                                                                        Map<String, LastChat> map2 = friendAccount.getMap();
                                                                                        map2.remove(userAccountme.getId());
                                                                                        friendAccount.setMap(map2);

                                                                                        db.collection("users").
                                                                                                document(mAuth.getCurrentUser().getUid()).
                                                                                                set(userAccountme);

                                                                                        db.collection("users").
                                                                                                document(friendAccount.getId()).
                                                                                                set(friendAccount);


                                                                                        mProgress.dismiss();
                                                                                        Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                                                                                        startActivity(intent);
                                                                                    }
                                                                                }

                                                                            }
                                                                        });
                                                                    }
                                                                });
                                                    }
                                                });
                                    }
                                });
                    }
                });
    }
}