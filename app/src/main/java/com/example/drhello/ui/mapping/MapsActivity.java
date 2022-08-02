package com.example.drhello.ui.mapping;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.drhello.ui.chats.StateOfUser;
import com.example.drhello.adapter.OnPlaceClickListener;
import com.example.drhello.adapter.OnSearchPlaceClickListener;
import com.example.drhello.R;
import com.example.drhello.connectionnewtwork.CheckNetwork;
import com.example.drhello.ui.login.CompleteInfoActivity;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import de.hdodenhof.circleimageview.CircleImageView;
import io.supercharge.shimmerlayout.ShimmerLayout;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener, OnSearchPlaceClickListener, OnPlaceClickListener {
    private  androidx.appcompat.widget.SearchView searchView;
    private ArrayList<PlaceDetails> placeDetailsArrayList = new ArrayList<>();
    ArrayList<PlaceDetails> placeDetailsArrayListSearch = new ArrayList();
    private PlacesAdapter adapter;
    private SearchMapsAdapter searchMapsAdapter;
    private RecyclerView recycler_places;
    private RecyclerView recycler_search_places;
    private Toolbar toolbar;
    private BottomSheetBehavior mBottomSheetBehavior1;
    private LinearLayout tapactionlayout;
    private ShimmerLayout shimmerLayout;
    View bottomSheet;
    private CircleImageView img_dark, img_def, img_light, img_app;
    TextView edit_search,txt_search;
    EditText  edit_search_inside;
    ImageView mic_search, img_search_inside, img_search_back,img_back;
    Marker markerOptionscur = null,markerOptions = null;
    /*new*/
    SupportMapFragment mapFragment;
    private GoogleMap mMap;
    View mapView;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double oldLat = 0.0, oldLon = 0.0, newLat = 0.0, newLon = 0.0,
            curtime = 0.0, newtime = 0.0, speed = 0.0;
    private boolean flag_first_time = true;
    private Location mLocation;
    private double UPDATALOCATIONOFHOSPITAL = 0.0;
    private int REQUESTPERMISSIONSLOCATION = 10;
    private final int REQUESTPERMISSIONSFINE_LOCATION = 1001;
    private final int REQUESTCODEGPS = 2000;
    private final int REQUESTCODEMIC = 3000;
    private ConstraintLayout constraint_map_search;
    private CoordinatorLayout coordinator_map_inside;
    private Polyline polyline = null;
    private boolean flag_map = false;
    private BitmapDescriptor bitmapDescriptorcur = null ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        if(getIntent().getStringExtra("map") != null){
            CompleteInfoActivity.showDialogPython.dismissDialog();
            flag_map = true;
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.alert_dialog_map, null);
            dialogBuilder.setView(dialogView);
            Button btn_enter = dialogView.findViewById(R.id.btn_enter);
            AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.show();
            btn_enter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });

        }





        bitmapDescriptorcur = bitmapDescriptorFromVectorcur(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();
        if(CheckNetwork.getConnectivityStatusString(MapsActivity.this) == 1) {
            Toast.makeText(MapsActivity.this, "Connected Internet", Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(MapsActivity.this, "Please, Check Internet", Toast.LENGTH_SHORT).show();
        }
        //to move button of my location
        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 310);
        }

        checkRunTimePermission();


        recycler_places = findViewById(R.id.recycle_places);
        recycler_search_places = findViewById(R.id.recycler_search_places);

        //BottomSheet
        img_back = findViewById(R.id.img_back);
        shimmerLayout = findViewById(R.id.shimmer_layout_bottom);
        bottomSheet = findViewById(R.id.bottom_sheet);
        tapactionlayout = findViewById(R.id.tap_action_layout);
        img_dark = findViewById(R.id.img_dark);
        img_def = findViewById(R.id.img_def);
        img_app = findViewById(R.id.img_app);
        img_light = findViewById(R.id.img_light);
        mic_search = findViewById(R.id.mic_search);
        edit_search = findViewById(R.id.edit_search);
        edit_search_inside = findViewById(R.id.edit_search_inside);
        img_search_inside = findViewById(R.id.img_search_inside);
        img_search_back = findViewById(R.id.img_search_back);
        coordinator_map_inside = findViewById(R.id.coordinator_map_inside);
        constraint_map_search = findViewById(R.id.constraint_map_search);

        shimmerLayout.startShimmerAnimation();
        mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior1.setPeekHeight(180);
        mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBottomSheetBehavior1.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    tapactionlayout.setVisibility(View.VISIBLE);
                }

                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    tapactionlayout.setVisibility(View.GONE);
                }

                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    tapactionlayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        tapactionlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBottomSheetBehavior1.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });

        edit_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coordinator_map_inside.setVisibility(View.GONE);
                constraint_map_search.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility()
                            | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                } else {
                    getWindow().setStatusBarColor(Color.WHITE);
                }
            }
        });

        mic_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSpeechInput();
            }
        });


        img_search_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coordinator_map_inside.setVisibility(View.VISIBLE);
                constraint_map_search.setVisibility(View.GONE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Window w = getWindow();
                    w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                }
            }
        });

        edit_search_inside.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                // filter your list from your input
                filter(s.toString());
                //you can use runnable postDelayed like 500 ms to delay search text
            }
        });

        img_search_inside.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                constraint_map_search.setVisibility(View.GONE);
                coordinator_map_inside.setVisibility(View.VISIBLE);
                if (!edit_search_inside.getText().toString().equals("")) {
                    edit_search.setText(edit_search_inside.getText().toString());
                    searchLocation(edit_search_inside.getText().toString());
                } else {
                    Toast.makeText(MapsActivity.this, "Please, try again !!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        img_dark.setOnClickListener(this);
        img_light.setOnClickListener(this);
        img_app.setOnClickListener(this);
        img_def.setOnClickListener(this);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        try {
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.night));
            if (success) {
                Log.e("mapdark", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("mapdark", "Can't find style. Error: ", e);
        }
        mMap.setTrafficEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.setMaxZoomPreference(18.5f);
        mMap.setPadding(10,10,10,350);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                //add to mark on location
                if(markerOptions!=null){
                    markerOptions.remove();
                }
                markerOptions = mMap.addMarker(new MarkerOptions().position(latLng)
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));

                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                Log.e("flag LONG CLICK: ", latLng.latitude+"  "+latLng.longitude +"");
                if(flag_map){
                    CompleteInfoActivity.location = latLng;
                    finish();
                }
            }
        });


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                /*
                if(polyline != null){
                    polyline.remove();
                }
                polyline = mMap.addPolyline(new PolylineOptions().add(marker.getPosition(),
                        new LatLng(newLat,newLon)).width(5).color(Color.RED));

                 */
                return false;
            }
        });


    }

    private void setMapStyle(String text) {
        switch (text) {
            case "dark":
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.night));
                Log.e("TYPE :" , "night");
                break;
            case "def":
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.retro));
                Log.e("TYPE :" , "retro");//HOMES
                break;
            case "app":
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.dark));
                Log.e("TYPE :" , "dark");
                break;
            case "light":
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
                //mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                Log.e("TYPE :" , "map_style");
                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_dark:
                setMapStyle("dark");
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;
            case R.id.img_def:
                setMapStyle("def");
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;
            case R.id.img_app:
                setMapStyle("app");
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;
            case R.id.img_light:
                setMapStyle("light");
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;
        }
    }


    @SuppressLint("MissingPermission")
    protected void createLocationRequest() {
        /*
            Set the minimum displacement between location updates in meters
            In between these two extremes is a very common use-case, where applications definitely want to receive updates
            at a specified interval, and can receive them faster when available, but still want a low power impact.
            These applications should consider PRIORITY_BALANCED_POWER_ACCURACY combined with
            a faster setFastestInterval(long) (such as 1 minute) and a slower setInterval(long) (such as 60 minutes).
         */
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

            if (mMap != null & isGPSEnabled(MapsActivity.this)) {

                Log.e("loc : ", "true");
                mMap.setMyLocationEnabled(true);
            } else {
                Log.e("createLocation", "Request : Please, check gps1!!");
                Toast.makeText(MapsActivity.this, "Please, check gps!!", Toast.LENGTH_SHORT).show();
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
    }

    public void onStop() {
        super.onStop();
        Log.e("onStop : ", "onStop");
        stopLocationUpdate();
    }

    public void onPause() {
        super.onPause();
        Log.e("onPause : ", "onPause");
        StateOfUser stateOfUser = new StateOfUser();
        stateOfUser.changeState("Offline");
        stopLocationUpdate();
    }

    public void onResume() {
        super.onResume();
        Log.e("onResume : ", "onResume");
        StateOfUser stateOfUser = new StateOfUser();
        stateOfUser.changeState("Online");
        if (mGoogleApiClient != null && mLocationRequest != null) {
            mGoogleApiClient.connect();
            if (mGoogleApiClient.isConnected()) {
                startLocationUpdates();
            }
        }
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

    // Must Declare LocatonListener Methods //
    public void onLocationChanged(Location location) {
        Log.e("onLocationChanged : ", "onLocationChanged");
        Log.e("getAccuracy() : ", location.getAccuracy() + "");
        LatLng latLngcur = new LatLng(location.getLatitude(),location.getLongitude());
        if(markerOptionscur!= null){
            markerOptionscur.remove();
        }
        if(bitmapDescriptorcur != null){
            markerOptionscur = mMap.addMarker(new MarkerOptions().position(latLngcur)
                    .icon(bitmapDescriptorcur));
        }else{
            mMap.addMarker(new MarkerOptions().position(latLngcur)
                    .icon(bitmapDescriptorcur));
        }
        if (location != null && location.hasAccuracy()) {
            // Accuracy is in rage of 20 meters, stop listening we have a fix
            double newTime = System.currentTimeMillis();
            newLat = location.getLatitude();
            newLon = location.getLongitude();
            if (mMap != null && flag_first_time) {
                Log.e("mMap : ", "mMap");
                LatLng latLng = new LatLng(newLat, newLon);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18.5f));
                flag_first_time = false;
                curtime = System.currentTimeMillis();
                oldLat = location.getLatitude();
                oldLon = location.getLongitude();
                CalcFromNearestHospital();

            }

            if (location.getAccuracy() < 15) {
                if (location.hasSpeed()) {
                    speed = location.getSpeed();
                    Log.e("speed : ", convertSpeed(speed));
                } else {
                    double distance = CalculationByDistance(new LatLng(newLat, newLon), new LatLng(oldLat, oldLon));
                    double timeDifferent = newTime - curtime;
                    speed = distance / timeDifferent;
                    curtime = newTime;
                    oldLat = newLat;
                    oldLon = newLon;
                    Log.e("speed : ", convertSpeed(speed));
                    Log.e("time : ", convertTime(timeDifferent));
                    Log.e("distance : ", convertDistance(distance));
                }

                if (newLat != oldLat || newLon != oldLon) {
                    Log.e("newLat : ", newLat + " " + newLon);
                    Log.e("oldLat : ", oldLat + " " + oldLon);
                    Log.e("Calculation : ", CalculationByDistance(new LatLng(newLat, newLon), new LatLng(oldLat, oldLon)) + "");
                    UPDATALOCATIONOFHOSPITAL = UPDATALOCATIONOFHOSPITAL + CalculationByDistance(new LatLng(newLat, newLon), new LatLng(oldLat, oldLon));
                    if ((UPDATALOCATIONOFHOSPITAL * 1000) > 10) { // updata each 5 meter
                        Log.e("UPDATALOCATIONOF : ", UPDATALOCATIONOFHOSPITAL + "");
                        UPDATALOCATIONOFHOSPITAL = 0;
                        CalcFromNearestHospital();
                    }
                }
                //mapFragment.getMapAsync(this);
            }
        }
    }

    private void CalcFromNearestHospital() {
        Log.e("b : ", "CalcFromNearestHospital");
        placeDetailsArrayList.clear();

        for (int i = 0; i < DataMapSetInformation.getlatlong.length; i++) {
            String time = "";
            double ditance = CalculationByDistance(new LatLng(newLat, newLon), DataMapSetInformation.getlatlong[i]);
            if (speed < 1) {
                //1.4 meters per second (m/s) for human
                time = convertTime((ditance * 1000) / 1.4);
            } else {
                time = convertTime(((CalculationByDistance(new LatLng(newLat, newLon), DataMapSetInformation.getlatlong[i]) * 1000) / speed));
            }

            placeDetailsArrayList.add(new PlaceDetails(R.drawable.hospital,
                    "loaction",
                    DataMapSetInformation.gethospital_name[i],
                    convertDistance(ditance), time,
                    convertSpeed(speed),
                    DataMapSetInformation.getAddressLine[i],
                    DataMapSetInformation.getCountryName[i],
                    DataMapSetInformation.getAdminArea[i],
                    DataMapSetInformation.getLocality[i],
                    ditance, DataMapSetInformation.getlatlong[i]));
        }

        Collections.sort(placeDetailsArrayList, new Comparator<PlaceDetails>() {
            @Override
            public int compare(PlaceDetails lhs, PlaceDetails rhs) {
                return Double.compare(lhs.getDistance_double(), rhs.getDistance_double());
            }
        });
        placeDetailsArrayListSearch = placeDetailsArrayList;
        adapter = new PlacesAdapter(placeDetailsArrayList, this, MapsActivity.this);
        recycler_places.setAdapter(adapter);
        searchMapsAdapter = new SearchMapsAdapter(placeDetailsArrayList, this, MapsActivity.this);
        recycler_search_places.setAdapter(searchMapsAdapter);
    }

    public void onConnectionSuspended(int arg0) {
    }

    public void onStatusChange(String provider, int status, Bundle extras) {
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
                newLat = mLocation.getLatitude();
                newLat = mLocation.getLatitude();
                //mapFragment.getMapAsync(this);
            }

            if (mGoogleApiClient != null && mLocationRequest != null && mGoogleApiClient.isConnected()) {
                startLocationUpdates();
            }
        }
    }

    public void onConnectionFailed(ConnectionResult result) {
    }

    private void requestGps() {
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
                                    MapsActivity.this,
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

    private String convertDistance(double distance) {
        //return in meters or kilometers
        if (distance < 0.5) {
            return String.format("%.2f", distance * 1000) + " m";
        }
        return String.format("%.2f", distance) + "km";
    }

    private String convertTime(double seconds) {
        if (seconds >= 3600) { // return hour:min
            return String.format("%.0f", seconds / 3600) + "." + String.format("%.0f", (seconds % 3600) / 60) + " Hr";

        } else if (seconds >= 60) { // return min:sec
            return String.format("%.0f", (seconds % 3600) / 60) + "." + String.format("%.0f", seconds % 60) + " min";
        }
        return String.format("%.2f", seconds) + " sec";
    }

    private String convertSpeed(double speedMeterSec) {
        //convert m/s to km/h:
        if (speedMeterSec >= 100.0) {
            return String.format("%.2f", speedMeterSec * 3.6) + " km/h";
        }
        //m/s:
        return String.format("%.2f", speedMeterSec) + " m/s";
    }

    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        return valueResult; // return km and meter
    }

    //to get location through mic
    public void getSpeechInput() {
        final Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1000);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Your Prompt");
        
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUESTCODEMIC);
        } else {
            Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("onActivityResult()", Integer.toString(resultCode));
        switch (requestCode) {
            case REQUESTCODEMIC:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    edit_search.setText(result.get(0));
                    searchLocation(edit_search.getText().toString());
                }
                break;
            case REQUESTCODEGPS:
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        // All required changes were successfully made
                        Toast.makeText(MapsActivity.this, "Location enabled by user!", Toast.LENGTH_LONG).show();
                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        // The user was asked to change settings, but chose not to
                        Toast.makeText(MapsActivity.this, "Location not enabled, user cancelled.", Toast.LENGTH_LONG).show();
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
        }


        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {

        }


    }

    public boolean isGPSEnabled(Context mContext) {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public void checkRunTimePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.e("checkRunTime : ", "true");
                // make a buidler for GoogleApiClient //
                createLocationRequest();

            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUESTPERMISSIONSLOCATION);
            }
        } else {
            if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // make a buidler for GoogleApiClient //
                createLocationRequest();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUESTPERMISSIONSLOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Log.e("checkRunTime : ", "true");
                    // make a buidler for GoogleApiClient //
                    createLocationRequest();
                    return;
                }
            } else {
                Log.e("onRequestPermissions : ", "false");

                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // If User Checked 'Don't Show Again' checkbox for runtime permission, then navigate user to Settings
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MapsActivity.this);
                    dialog.setTitle("Permission Required");
                    dialog.setCancelable(false);
                    dialog.setMessage("You have to Allow permission to access user location");
                    dialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package",
                                    MapsActivity.this.getPackageName(), null));
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

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        switch (requestCode) {
            case REQUESTPERMISSIONSFINE_LOCATION:
                Log.e("startActivity : ", "startActivity");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                            ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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

    // search of specify location
    public void searchLocation(String location) {
        /*
        float   HUE_AZURE
        float   HUE_BLUE
        float   HUE_CYAN
        float   HUE_GREEN
        float   HUE_MAGENTA
        float   HUE_ORANGE
        float   HUE_RED
        float   HUE_ROSE
        float   HUE_VIOLET
        float   HUE_YELLOW
        */
        if(markerOptions!=null){
            markerOptions.remove();
        }
        List<Address> addressList = null;
        if (location != null && !location.equals("")) {
            if (Geocoder.isPresent()) {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                try {
                    addressList = geocoder.getFromLocationName(location.toLowerCase(), 1);
                    if (addressList.size() > 0 && addressList != null) {
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                        markerOptions = mMap.addMarker(new MarkerOptions().position(latLng).title(location)
                                .icon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                        /*
                        if(polyline != null){
                            polyline.remove();
                        }
                        polyline =  mMap.addPolyline(new PolylineOptions().add(new LatLng(newLat, newLon),
                                latLng).width(5).color(Color.RED));

                         */

                    } else {
                        Toast.makeText(getApplicationContext(), "Location not Found, Please try again !!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("IOException",e.getMessage());
                }
            } else {
                Toast.makeText(getApplicationContext(), "Please, try again!!", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(getApplicationContext(), "Location is Empty , Please Enter Location !!", Toast.LENGTH_SHORT).show();
        }

    }

    void filter(String text) {
        placeDetailsArrayListSearch = new ArrayList<>();
        for (PlaceDetails placeDetails : placeDetailsArrayList) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (placeDetails.getPlace_name().toLowerCase().contains(text.toLowerCase())) {
                placeDetailsArrayListSearch.add(placeDetails);
            }
        }
        //update recyclerview
        searchMapsAdapter.updateList(placeDetailsArrayListSearch);
    }


    @Override
    public void onSearchPlaceClick(int pos) {
        if (placeDetailsArrayListSearch.size() > 0) {
            if(markerOptions!=null){
                markerOptions.remove();
            }
            markerOptions = mMap.addMarker(new MarkerOptions().position(placeDetailsArrayListSearch.get(pos).getPlace_latLng()));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(placeDetailsArrayListSearch.get(pos).getPlace_latLng()));
            edit_search.setText(placeDetailsArrayListSearch.get(pos).place_name);
            constraint_map_search.setVisibility(View.GONE);
            coordinator_map_inside.setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Window w = getWindow();
                w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }
/*
            if(polyline != null){
                polyline.remove();
            }
            polyline =  mMap.addPolyline(new PolylineOptions().add(new LatLng(newLat, newLon),
                    placeDetailsArrayList.get(pos).getPlace_latLng()).width(5).color(Color.RED));

 */
        }
    }

    @Override
    public void onPlaceClick(int pos) {
        if(markerOptions!=null){
            markerOptions.remove();
        }
        markerOptions = mMap.addMarker(new MarkerOptions().position(placeDetailsArrayListSearch.get(pos).getPlace_latLng()));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(placeDetailsArrayList.get(pos).getPlace_latLng()));
        edit_search.setText(placeDetailsArrayList.get(pos).place_name);
        constraint_map_search.setVisibility(View.GONE);
        coordinator_map_inside.setVisibility(View.VISIBLE);

        /*
        if(polyline != null){
            polyline.remove();
        }
        polyline =  mMap.addPolyline(new PolylineOptions().add(new LatLng(newLat, newLon),
                placeDetailsArrayList.get(pos).getPlace_latLng()).width(5).color(Color.RED));
         */
    }

    public void zoomout(View view) {
        mMap.animateCamera(CameraUpdateFactory.zoomOut());
    }


    public void zoomin(View view) {
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
    }

    private BitmapDescriptor bitmapDescriptorFromVectorcur(Context context) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.motorcycle_1);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    /*
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes  int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.motorcycle_2);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());

        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(40, 20, vectorDrawable.getIntrinsicWidth() + 40, vectorDrawable.getIntrinsicHeight() + 20);

        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        //vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
    */
}