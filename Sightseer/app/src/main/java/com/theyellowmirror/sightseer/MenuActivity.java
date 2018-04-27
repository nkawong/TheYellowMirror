package com.theyellowmirror.sightseer;
import Distance_DurationCheck.Durations;
import Distance_DurationCheck.Routes;
import Routing.*;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Path;
import android.app.ProgressDialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.*;
import android.view.KeyEvent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.location.Location;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.util.*;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.content.pm.PackageManager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import Routing.DirectionFinder;
import Routing.DirectionFinderListener;

import Distance_DurationCheck.Dis_DurCheck;
import Distance_DurationCheck.Dis_DurCheckListener;
import javax.net.ssl.HttpsURLConnection;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,GoogleApiClient.OnConnectionFailedListener,
        DirectionFinderListener,Dis_DurCheckListener{

    private FirebaseAuth ref;
    //Map
    private static final String TAG = "MenuActivity";

    //get device location
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;

    // The entry points to the Places API.
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    //autocomplete drop down search bar
    private AutoCompleteTextView address1;
    private AutoCompleteTextView address2;
    private AutoCompleteTextView address3;
    private AutoCompleteTextView address4;
    private AutoCompleteTextView address5;
    private PlaceAutocompleteAdapter placeAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));

    //routing
    private Button startRouteBT;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private int durationTemp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //menu bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        //autocomplete drop down search bar
        initAutoComp();

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }


        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //initialize all 5 of the address area
        init(address1);
        init(address2);
        init(address3);
        init(address4);
        init(address5);


        address1.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(arg1.getWindowToken(), 0);

            }

        });

        //testing
        startRouteBT = (Button) findViewById(R.id.startRouteBT);
        startRouteBT.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                startRout();

            }
        });


    }

    //menu
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_setting) {
            Intent settings = new Intent(MenuActivity.this,SettingActivity.class);
            MenuActivity.this.startActivity(settings);
            // Handle the camera action

            //Will delete user need to make sure user is logged in
            // if(ref.getCurretnUser() != null){
            // ref.getCurrentUser().delete();
            // }

            //Updates Password Need a content menu for settings
            //ref.getCurrentUser().updatePassword("Password Reset Here");

        } else if (id == R.id.nav_history) {

        } else if (id == R.id.nav_logout) {
            if(ref.getCurrentUser() != null){
                ref.signOut();
            }
            else{
                //null user is not signed in
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }


    /**
     * Handles a click on the menu option to get a place.
     * @param item The menu item to handle.
     * @return Boolean.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.option_get_place) {
            moveCamera(new LatLng(mLastKnownLocation.getLatitude(),
                    mLastKnownLocation.getLongitude()), DEFAULT_ZOOM, "My Location");
        }
        return true;
    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            moveCamera(new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM, "My Location");
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }


    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    //init address1-5 so that enter search for the address input
    private void init(final AutoCompleteTextView address){
        address.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(arg1.getWindowToken(), 0);

            }

        });
        address.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){

                    //execute our method for searching
                    geoLocate(address);
                }

                return false;
            }
        });
    }

    private void geoLocate(AutoCompleteTextView address){
        Log.d(TAG, "geoLocate: geolocating");


        String search = address.getText().toString();
        Geocoder geo = new Geocoder(MenuActivity.this);
        if(geo.isPresent()){
            try {
                List<Address> addresses = geo.getFromLocationName(search,1);
                if (addresses.size()==0){
                    Toast.makeText(MenuActivity.this,"Address locating failed, please enter the full address",Toast.LENGTH_SHORT).show();
                    return;
                }
                Address temp = addresses.get(0);
                LatLng location = new LatLng(temp.getLatitude(),temp.getLongitude());
                moveCamera(location,DEFAULT_ZOOM,"worked!");
            } catch (IOException e) {
                Toast.makeText(MenuActivity.this,"Network connection to geocoder not working",Toast.LENGTH_SHORT).show();

            }

        }


    }


    private void moveCamera(LatLng latLng, float zoom, String title){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
        mMap.addMarker(options);


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void initAutoComp(){
        address1 = (AutoCompleteTextView) findViewById(R.id.address1);
        address2 = (AutoCompleteTextView) findViewById(R.id.address2);
        address3 = (AutoCompleteTextView) findViewById(R.id.address3);
        address4 = (AutoCompleteTextView) findViewById(R.id.address4);
        address5 = (AutoCompleteTextView) findViewById(R.id.address5);
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        placeAutocompleteAdapter =  new PlaceAutocompleteAdapter(this,mGoogleApiClient,LAT_LNG_BOUNDS,null);
        address1.setAdapter(placeAutocompleteAdapter);
        address2.setAdapter(placeAutocompleteAdapter);
        address3.setAdapter(placeAutocompleteAdapter);
        address4.setAdapter(placeAutocompleteAdapter);
        address5.setAdapter(placeAutocompleteAdapter);
    }
    private String[] addressOrderArray = new String[5];
    private ArrayList<String> notOrdered;

    public void ordering(){

        for(int i =0;i<5;i++){
            addressOrderArray[i]=null;
        }

        notOrdered = new ArrayList<>();

        String o2=((EditText) findViewById(R.id.order2)).getText().toString();
        String o3=((EditText) findViewById(R.id.order3)).getText().toString();
        String o4=((EditText) findViewById(R.id.order4)).getText().toString();
        String o5=((EditText) findViewById(R.id.order5)).getText().toString();

        int io2,io3,io4,io5;

        String a1 = address1.getText().toString();
        String a2 = address2.getText().toString();
        String a3 = address3.getText().toString();
        String a4 = address4.getText().toString();
        String a5 = address5.getText().toString();
        //starting address
        addressOrderArray[0]=a1;

        if(o2.matches("1|2|3|4|5")){
            io2 =Integer.parseInt(o2);
            if(io2==1) addressOrderArray[0]=a2;
            else if(io2==2) addressOrderArray[1]=a2;
            else if(io2==3) addressOrderArray[2]=a2;
            else if(io2==4) addressOrderArray[3]=a2;
            else if(io2==5) addressOrderArray[4]=a2;
        }
        else notOrdered.add(a2);
        if(o3.matches("1|2|3|4|5")){
            io3 =Integer.parseInt(o3);
            if(io3==1) addressOrderArray[0]=a3;
            else if(io3==2) addressOrderArray[1]=a3;
            else if(io3==3) addressOrderArray[2]=a3;
            else if(io3==4) addressOrderArray[3]=a3;
            else if(io3==5) addressOrderArray[4]=a3;
        }
        else notOrdered.add(a3);
        if(o4.matches("1|2|3|4|5")){
            io4 =Integer.parseInt(o4);
            if(io4==1) addressOrderArray[0]=a4;
            else if(io4==2) addressOrderArray[1]=a4;
            else if(io4==3) addressOrderArray[2]=a4;
            else if(io4==4) addressOrderArray[3]=a4;
            else if(io4==5) addressOrderArray[4]=a4;
        }
        else notOrdered.add(a4);
        if(o5.matches("1|2|3|4|5")){
            io5 =Integer.parseInt(o5);
            if(io5==1) addressOrderArray[0]=a5;
            else if(io5==2) addressOrderArray[1]=a5;
            else if(io5==3) addressOrderArray[2]=a5;
            else if(io5==4) addressOrderArray[3]=a5;
            else if(io5==5) addressOrderArray[4]=a5;
        }
        else notOrdered.add(a5);

        for(int i =1;i<5;i++){
            if(addressOrderArray[i]==null){

                int min=0;
                String endMin =null;
                for(String temp: notOrdered){
                    try {
                        Log.d(TAG,"now"+addressOrderArray[i-1]);
                        Log.d(TAG,"temp"+temp);
                        new Dis_DurCheck(this,addressOrderArray[i-1],temp).execute();
                        if(min==0) {
                            min=durationTemp;
                            endMin = temp;
                        }
                        if(durationTemp<min) {
                            min=durationTemp;
                            endMin = temp;
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                addressOrderArray[i] = endMin;
                notOrdered.remove(endMin);
            }
        }



    }
    public void startRout(){

        ordering();
        try{
            new DirectionFinder(this,addressOrderArray[0],addressOrderArray[1]).execute();
        }
        catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }

        try{
            new DirectionFinder(this,addressOrderArray[1],addressOrderArray[2]).execute();
        }
        catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }

        try{
            new DirectionFinder(this,addressOrderArray[2],addressOrderArray[3]).execute();
        }
        catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }

        try{
            new DirectionFinder(this,addressOrderArray[3],addressOrderArray[4]).execute();
        }
        catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }



    }

    @Override
    public void onDirectionFinderStart() {

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            ((TextView) findViewById(R.id.timeLeftTV)).setText(route.duration.text);
            ((TextView) findViewById(R.id.mileLeftTV)).setText(route.distance.text);

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }

    }


    @Override
    public void onDis_DurStart() {


    }

    @Override
    public void onDis_DurSuccess(List<Routes> routes) {

        durationTemp = routes.get(0).duration.value;
    }
//
//    private void fixZoom() {
//        List<LatLng> points = route.getPoints(); // route is instance of PolylineOptions
//
//        LatLngBounds.Builder bc = new LatLngBounds.Builder();
//
//        for (LatLng item : points) {
//            bc.include(item);
//        }
//
//        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 50));
//    }




}
