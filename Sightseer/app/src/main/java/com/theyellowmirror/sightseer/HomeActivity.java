package com.theyellowmirror.sightseer;

/**
 * Created by David on 3/15/2018.
 */

import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.Manifest;

public class HomeActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int GPS_ERRORDIALOG_REQUEST = 9001;
    private static final int MY_PERMISSION_REQUEST = 0;

    GoogleMap mMap;
    MapView mMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_home);

          //  mMapView = (MapView) findViewById(R.id.map);
            mMapView = (MapView) findViewById(R.id.map);
            mMapView.onCreate(savedInstanceState);
            //initMap();
            onMapReady(mMap);
    }
//    public void initMap(){
//        //MapFragment mView = (MapFragment) getFragmentManager().findFragmentById(R.id.mapView);
//        //mView.getMapAsync(this);
//    }
    public void onMapReady(GoogleMap map){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            //mMap.addMarker(new MarkerOptions().position()
        }
    }

//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        if (requestCode == MY_PERMISSION_REQUEST) {
//            if (permissions.length == 1 &&
//                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
//                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                requestPermissions(permissions, MY_PERMISSION_REQUEST);
//            }
//        }
//    }

    public boolean onCreatOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.activity_menu_drawer, menu);
        return true;
    }
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//        getGeoLocation(33.782972,-118.1191328);
//    }
//    public void getGeoLocation(double lat, double lng){
//        LatLng position = new LatLng(lat, lng);
//        CameraUpdate cam_update = CameraUpdateFactory.newLatLng(position);
//        mMap.moveCamera(cam_update);
//    }
}
