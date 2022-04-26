package com.example.drhello.ui.main;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.drhello.ui.chats.StateOfUser;
import com.example.drhello.connectionnewtwork.NetworkChangeListener;
import com.example.drhello.model.UserAccount;
import com.example.drhello.ui.hardware.HardwareActivity;
import com.example.drhello.model.ChatModel;
import com.example.drhello.ui.news.MainNewsFragment;
import com.example.drhello.ui.mapping.MapsActivity;
import com.example.drhello.ui.profile.ProfileActivity;
import com.example.drhello.R;
import com.example.drhello.ui.alarm.AlarmActivity;
import com.example.drhello.fragment.ChatFragment;
import com.example.drhello.fragment.HomeFragment;
import com.example.drhello.fragment.PostFragment;
import com.example.drhello.model.UserInfo;
import com.example.drhello.ui.login.SignIn;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener{

    //Navigation View
    private static final int HOME=1;
    private static final int PROFILE=2;
    private static final int ALARM=3;
    private static final int Maps=4;
    private static final int Health=5;
    private static final int FAV=6;
    private static final int SETTING=7;
    private static final int SHARE=8;
    private static final int CONTACT=9;
    private static final int FEEDBACK=10;
    private static final int ABOUT=11;
    private static final int LOGOUT=13;


    private String[] screenTitle;
    private Drawable[] screenIcon;
    private SlidingRootNav slidingRootNav;

    //Bottom Navigation bar
    private final int ID_HOME=1;
    private final int ID_POST=2;
    private final int ID_CHAT=3;
    private final int ID_NEWS=4;

    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount acct = null;
    private DrawerAdapter adapter;
    private Fragment selectedFragment;
    private final int PERMISSION_ALL_STORAGE = 1000;
    private  UserAccount userAccount;

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) ;
        }else{
            getWindow().setStatusBarColor(Color.WHITE);
        }

        //to connect layout with java code
        com.example.drhello.databinding.ActivityMainBinding mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setSupportActionBar(mainBinding.toolbar);

        slidingRootNav=new SlidingRootNavBuilder(this)
                .withDragDistance(180)
                .withRootViewScale(0.75f)
                .withRootViewElevation(25)
                .withToolbarMenuToggle(mainBinding.toolbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.drawer_menu)
                .inject();

        screenIcon=loadScreenIcons();
        screenTitle=loadScreenTitles();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("users").whereEqualTo("id",FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        Log.e("task : " , " tast");
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            userAccount = document.toObject(UserAccount.class);
                            adapter=new DrawerAdapter(Arrays.asList(
                                    new UserInfo(userAccount.getName(),userAccount.getImg_profile(),MainActivity.this),
                                    createFor(HOME).setChecked(true),
                                    createFor(PROFILE),
                                    createFor(ALARM),
                                    createFor(Maps),
                                    createFor(Health),
                                    createFor(FAV),
                                    createFor(SETTING),
                                    createFor(SHARE),
                                    createFor(CONTACT),
                                    createFor(FEEDBACK),
                                    createFor(ABOUT),
                                    new SpaceItem(50),
                                    createFor(LOGOUT)
                            ));

                            adapter.setListener(MainActivity.this);

                            RecyclerView list=findViewById(R.id.drawer_list);

                            list.setNestedScrollingEnabled(false);
                            list.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                            list.setAdapter(adapter);

                            adapter.setSelected(HOME);
                        }
                    }
                }
            });




        }


        getSupportFragmentManager().beginTransaction().replace(R.id.fragment,new HomeFragment()).commit();

        mainBinding.bottomNavigation.add(new MeowBottomNavigation.Model(ID_HOME,R.drawable.ic_home));
        mainBinding.bottomNavigation.add(new MeowBottomNavigation.Model(ID_POST,R.drawable.ic_post));
        mainBinding.bottomNavigation.add(new MeowBottomNavigation.Model(ID_NEWS,R.drawable.ic_world_news));
        mainBinding.bottomNavigation.add(new MeowBottomNavigation.Model(ID_CHAT,R.drawable.ic_chat));
        mainBinding.bottomNavigation.show(ID_HOME,true);

        String openPost = getIntent().getStringExtra("openPost");
        String post = getIntent().getStringExtra("post");
        String postsView = getIntent().getStringExtra("postsView");
        String chatactivity = getIntent().getStringExtra("chatactivity");
        ChatModel chatModel = (ChatModel) getIntent().getSerializableExtra("message");
        if(openPost != null){
            Log.e("openMain : ",openPost);
            selectedFragment=new PostFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment,selectedFragment).commit();
            mainBinding.bottomNavigation.show(ID_POST,true);
        }else if(post != null){
            Log.e("post : ",post);
            selectedFragment=new PostFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment,selectedFragment).commit();
            mainBinding.bottomNavigation.show(ID_POST,true);
        }else if(postsView != null){
            Log.e("post : ",postsView);
            selectedFragment=new PostFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment,selectedFragment).commit();
            mainBinding.bottomNavigation.show(ID_POST,true);
        }else if(chatactivity != null){
            Log.e("chat : ",chatactivity);
            selectedFragment=new ChatFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment,selectedFragment).commit();
            mainBinding.bottomNavigation.show(ID_CHAT,true);
        }else if(chatModel != null){
            Log.e("main : ", chatModel.getMessage());
            selectedFragment=new ChatFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment,selectedFragment).commit();
            mainBinding.bottomNavigation.show(ID_CHAT,true);
        }

        mainBinding.bottomNavigation.setOnClickMenuListener(model -> {

            switch (model.getId()){
                case ID_HOME:
                    selectedFragment=new HomeFragment();
                    break;
                case ID_POST:
                    selectedFragment=new PostFragment();
                    break;
                case ID_CHAT:
                    selectedFragment=new ChatFragment();
                    break;
                case ID_NEWS:
                    selectedFragment=new MainNewsFragment();
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + model.getId());
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment,selectedFragment).commit();
            return null;
        });



        initialGoogleAccount();

    }


    @Override
    public void onItemSelected(int position) {
        if (position==LOGOUT){
            signOut();
        }else if(position==PROFILE){
            Intent intent=new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
            adapter.setSelected(HOME);
        }else if (position==ALARM){
            Intent intent=new Intent(MainActivity.this, AlarmActivity.class);
            startActivity(intent);
            adapter.setSelected(HOME);
        }else if (position==Maps){
            Intent intent=new Intent(MainActivity.this, MapsActivity.class);
            startActivity(intent);
            adapter.setSelected(HOME);
        }else if (position==Health){
            Intent intent=new Intent(MainActivity.this, HardwareActivity.class);
            startActivity(intent);
            adapter.setSelected(HOME);
        }
        slidingRootNav.closeMenu();
    }

    private DrawerItem createFor(int position){
        return new SimpleItem(screenIcon[position],screenTitle[position])
                .withIconTint(color(R.color.appColorUnSelected))
                .withTextTint(color(R.color.appColorUnSelected))
                .withSelectedIconTint(color(R.color.appColor))
                .withSelectedTextTint(color(R.color.appColor));
    }

    @ColorInt
    private int color(@ColorRes int res){
        return ContextCompat.getColor(this,res);
    }

    private String[] loadScreenTitles() {
        return getResources().getStringArray(R.array.id_screenTitle);
    }

    private Drawable[] loadScreenIcons() {
        TypedArray ta=getResources().obtainTypedArray(R.array.id_screenIcon);
        Drawable[]icons=new Drawable[ta.length()];
        for (int i=0;i<ta.length();i++){
            int id=ta.getResourceId(i,0);
            if (id!=0){
                icons[i]=ContextCompat.getDrawable(this,id);
            }
        }
        ta.recycle();
        return icons;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }



    private void initialGoogleAccount() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);
        acct = GoogleSignIn.getLastSignedInAccount(MainActivity.this);
    }

    private void signOut() {
        if (acct != null) {
            FirebaseAuth.getInstance().signOut();
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(task -> {
                        Intent intent = new Intent(MainActivity.this, SignIn.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                        Toast.makeText(MainActivity.this, "User account sign out. google", Toast.LENGTH_SHORT).show();
                    });
        } else {
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
            Intent intent = new Intent(MainActivity.this, SignIn.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            Toast.makeText(MainActivity.this, "User account sign out. email", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            signIn();
        }

        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, intentFilter);
    }

    private void signIn() {
        Intent intent = new Intent(MainActivity.this, SignIn.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case PERMISSION_ALL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                    // All Permissions Granted
                }
                return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        StateOfUser stateOfUser = new StateOfUser();
        stateOfUser.changeState("Online");
        Log.e("onResume:","onResume");

    }

    @Override
    protected void onPause() {
        super.onPause();
        StateOfUser stateOfUser = new StateOfUser();
        stateOfUser.changeState("Offline");
    }


    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(networkChangeListener);
    }
}