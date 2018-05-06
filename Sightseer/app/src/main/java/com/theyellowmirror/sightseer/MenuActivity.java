package com.theyellowmirror.sightseer;
import Distance_DurationCheck.Dis_DurCheck;
import Distance_DurationCheck.Routes;
import Routing.*;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.*;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.location.Location;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.maps.CameraUpdate;
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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import Routing.DirectionFinder;
import Routing.DirectionFinderListener;

import Distance_DurationCheck.Dis_DurCheckListener;

import com.google.firebase.auth.FirebaseAuth;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,GoogleApiClient.OnConnectionFailedListener,
        DirectionFinderListener,Dis_DurCheckListener{

    //firebase authorization, used for sign out
    private FirebaseAuth ref;

    private static final String TAG = "MenuActivity";

    //get device location
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. The last-known
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

    //order drop down and edittext
    private Spinner order2;
    private Spinner order3;
    private Spinner order4;
    private Spinner order5;

    //est time
    private EditText time1;
    private EditText time2;
    private EditText time3;
    private EditText time4;
    private EditText time5;
    private TextView totalTime;

    //routing
    private Button startRouteBT;
    private List<Polyline> polylinePaths = new ArrayList<>();
    private List<Marker> markers = new ArrayList<>();
    private int distanceTemp;
    private int minuteTemp;

    //keep track of number of markers/ployline
    private int numOfMarkers =0;
    private int numOfPolyline =0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //init menu,action bar, navigation view
        initMenu();

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        //init map and get current location
        initMap();

        //autocomplete drop down search bar
        initAutoComp();

        //initialize order spinner drop down bar
        initOrderDropDown();

        //init Start Route Button
        initStartRouteButton();


    }

    public void initMenu(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void initMap(){
        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        //hide keyboard after choose an option from autocomplete
        init(address1);
        init(address2);
        init(address3);
        init(address4);
        init(address5);
    }

    //init address1-5 so that tapping an option from autocomplete will close the keyboard
    private void init(final AutoCompleteTextView address){
        address.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(arg1.getApplicationWindowToken(), 0);

            }

        });
    }

    ArrayList<String> nums = new ArrayList<>();
    ArrayAdapter<String> orderNumAdapter;
    public void initOrderDropDown(){
        order2 =  findViewById(R.id.order2);
        order3 =  findViewById(R.id.order3);
        order4 =  findViewById(R.id.order4);
        order5 =  findViewById(R.id.order5);

        nums.add("-");
        nums.add("2");
        nums.add("3");
        nums.add("4");
        nums.add("5");

        orderNumAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, nums);
        orderNumAdapter.setDropDownViewResource(R.layout.spinner_item);

        order2.setAdapter(orderNumAdapter);
        order3.setAdapter(orderNumAdapter);
        order4.setAdapter(orderNumAdapter);
        order5.setAdapter(orderNumAdapter);

        changeSpinnerTextColor(order2);
        changeSpinnerTextColor(order3);
        changeSpinnerTextColor(order4);
        changeSpinnerTextColor(order5);

    }
    public void changeSpinnerTextColor(Spinner spinner){
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE); /* if you want your item to be white */
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    public void initStartRouteButton(){
        //clear previous data
        if (markers != null) {
            for (Marker marker : markers) {
                marker.remove();
            }
        }


        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
        final SlidingUpPanelLayout mLayout= (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        startRouteBT = (Button) findViewById(R.id.startRouteBT);
        startRouteBT.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                minuteTemp=0;
                numOfPolyline=0;
                numOfMarkers=0;
                counterOfDirFindSucc=0;
                startRout();
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

            }
        });
    }

    //Menu settings
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

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


    /*
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


    /*
     * get current place
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.option_get_place) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(),
                    mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
        }
        return true;
    }

    /*
     *Manipulates the map when it's available.
     *This callback is triggered when the map is ready to be used.
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

    //Gets the current location of the device, and positions the map's camera.
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
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(),
                                    mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
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


    /*
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

    /*
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


    /*
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


    private String[] addressOrderArray = new String[5];
    private ArrayList<String> notOrdered;

    public void ordering(){

        for(int i =0;i<5;i++){
            addressOrderArray[i]=null;
        }

        notOrdered = new ArrayList<>();

        String o2=((Spinner) findViewById(R.id.order2)).getSelectedItem().toString();
        String o3=((Spinner) findViewById(R.id.order3)).getSelectedItem().toString();
        String o4=((Spinner) findViewById(R.id.order4)).getSelectedItem().toString();
        String o5=((Spinner) findViewById(R.id.order5)).getSelectedItem().toString();

        int io2,io3,io4,io5;

        String a1 = address1.getText().toString();
        String a2 = address2.getText().toString();
        String a3 = address3.getText().toString();
        String a4 = address4.getText().toString();
        String a5 = address5.getText().toString();

        //starting address
        addressOrderArray[0]=a1;
        numOfMarkers++;

        if(o2.matches("1|2|3|4|5")){
            io2 =Integer.parseInt(o2);
            if(io2==1) addressOrderArray[0]=a2;
            else if(io2==2) addressOrderArray[1]=a2;
            else if(io2==3) addressOrderArray[2]=a2;
            else if(io2==4) addressOrderArray[3]=a2;
            else if(io2==5) addressOrderArray[4]=a2;
            numOfMarkers++;
        }
        else if(!a2.matches("")){
            notOrdered.add(a2);
            numOfMarkers++;
        }

        if(o3.matches("1|2|3|4|5")){
            io3 =Integer.parseInt(o3);
            if(io3==1) addressOrderArray[0]=a3;
            else if(io3==2) addressOrderArray[1]=a3;
            else if(io3==3) addressOrderArray[2]=a3;
            else if(io3==4) addressOrderArray[3]=a3;
            else if(io3==5) addressOrderArray[4]=a3;
            numOfMarkers++;
        }
        else if(!a3.matches("")){
            notOrdered.add(a3);
            numOfMarkers++;
        }

        if(o4.matches("1|2|3|4|5")){
            io4 =Integer.parseInt(o4);
            if(io4==1) addressOrderArray[0]=a4;
            else if(io4==2) addressOrderArray[1]=a4;
            else if(io4==3) addressOrderArray[2]=a4;
            else if(io4==4) addressOrderArray[3]=a4;
            else if(io4==5) addressOrderArray[4]=a4;
            numOfMarkers++;
        }
        else if(!a4.matches("")){
            notOrdered.add(a4);
            numOfMarkers++;
        }

        if(o5.matches("1|2|3|4|5")){
            io5 =Integer.parseInt(o5);
            if(io5==1) addressOrderArray[0]=a5;
            else if(io5==2) addressOrderArray[1]=a5;
            else if(io5==3) addressOrderArray[2]=a5;
            else if(io5==4) addressOrderArray[3]=a5;
            else if(io5==5) addressOrderArray[4]=a5;
            numOfMarkers++;
        }
        else if(!a5.matches("")){
            notOrdered.add(a5);
            numOfMarkers++;
        }

        for(int i =1;i<5;i++){
            if(addressOrderArray[i]==null){

                int min=0;
                String endMin =null;
                for(String temp: notOrdered){
                    try {
                        new Dis_DurCheck(this,addressOrderArray[i-1],temp).execute();

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG,temp+distanceTemp);
                    if(min==0) {
                        min=distanceTemp;
                        endMin = temp;
                    }
                    else if(distanceTemp<min) {
                        min=distanceTemp;
                        endMin = temp;
                    }
                }
                addressOrderArray[i] = endMin;
                notOrdered.remove(endMin);
            }
        }



    }

    public void startRout(){

        ordering();
        Log.d(TAG,"0: "+addressOrderArray[0]);
        Log.d(TAG,"1: "+addressOrderArray[1]);
        try{
            new DirectionFinder(this,addressOrderArray[0],addressOrderArray[1]).execute();
        }
        catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }

        if(addressOrderArray[2]!=null){
            Log.d(TAG,"2: "+addressOrderArray[2]);
            try{
                new DirectionFinder(this,addressOrderArray[1],addressOrderArray[2]).execute();
            }
            catch (UnsupportedEncodingException e){
                e.printStackTrace();
            }
        }

        if(addressOrderArray[3]!=null) {
            Log.d(TAG,"3: "+addressOrderArray[3]);
            try {
                new DirectionFinder(this, addressOrderArray[2], addressOrderArray[3]).execute();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        if(addressOrderArray[4]!=null) {
            Log.d(TAG,"4: "+addressOrderArray[4]);
            try {
                new DirectionFinder(this, addressOrderArray[3], addressOrderArray[4]).execute();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }




    }

    @Override
    public void onDirectionFinderStart() {


    }

    private int counterOfDirFindSucc;
    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));

            //first time enter this method, add both markers
            if(counterOfDirFindSucc==0){
                markers.add(mMap.addMarker(new MarkerOptions()
                        .title(route.startAddress)
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        .position(route.startLocation)));
                markers.add(mMap.addMarker(new MarkerOptions()
                        .title(route.endAddress)
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        .position(route.endLocation)));
            }
            //just add the additional address as marker
            else{
                if(counterOfDirFindSucc==1) {
                    markers.add(mMap.addMarker(new MarkerOptions()
                            .title(route.endAddress)
                            .icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                            .position(route.endLocation)));
                }
                else if(counterOfDirFindSucc==2) {
                    markers.add(mMap.addMarker(new MarkerOptions()
                            .title(route.endAddress)
                            .icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                            .position(route.endLocation)));
                }
                else{
                    markers.add(mMap.addMarker(new MarkerOptions()
                            .title(route.endAddress)
                            .icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                            .position(route.endLocation)));
                }
            }

            counterOfDirFindSucc++;
            numOfPolyline++;

            int temp = Color.BLACK;
            if(numOfPolyline ==1){
                temp = Color.parseColor("#007FFF");
            }
            else if(numOfPolyline==2){
                temp = Color.parseColor("#228B22");
            }
            else if(numOfPolyline==3){
                temp = Color.parseColor("#FF8C00");
            }
            else if(numOfPolyline==4){
                temp = Color.parseColor("#FFFF00");
            }


            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(temp).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));

            //last call to this function, should zoom to fit all markers
            if(numOfMarkers-1==numOfPolyline){
                fixZoom();
                minuteTemp = minuteTemp/60;
                getEstTime();
            }
        }

    }


    @Override
    public void onDis_DurStart() {


    }

    @Override
    public void onDis_DurSuccess(List<Routes> routes) {
        distanceTemp = routes.get(0).distance.value;
        minuteTemp += routes.get(0).duration.value;

    }

    private void fixZoom() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();

        int padding = 300; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        mMap.moveCamera(cu);

    }

    public void getEstTime(){

        time1 = (EditText) findViewById(R.id.time1);
        time2 = (EditText) findViewById(R.id.time2);
        time3 = (EditText) findViewById(R.id.time3);
        time4 = (EditText) findViewById(R.id.time4);
        time5 = (EditText) findViewById(R.id.time5);

        String t1 = time1.getText().toString();
        String t2 = time2.getText().toString();
        String t3 = time3.getText().toString();
        String t4 = time4.getText().toString();
        String t5 = time5.getText().toString();
        Log.d(TAG,t1);
        if(!(t1.matches(""))){
            minuteTemp = minuteTemp+ Integer.parseInt(t1);
        }
        if(!(t2.matches(""))){
            minuteTemp += Integer.parseInt(t2);
        }
        if(!(t3.matches(""))){
            minuteTemp += Integer.parseInt(t3);
        }
        if(!(t4.matches(""))){
            minuteTemp += Integer.parseInt(t4);
        }
        if(!(t5.matches(""))){
            minuteTemp += Integer.parseInt(t5);
        }
        totalTime = (TextView) findViewById(R.id.totalTime);
        totalTime.setText(Integer.toString(minuteTemp));


    }




}
