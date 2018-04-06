package com.theyellowmirror.sightseer;

/**
 * Created by David on 3/15/2018.
 */

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.MarkerOptions;

public class HomeActivity extends FragmentActivity {

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


    }

    public boolean onCreatOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.activity_menu_drawer, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }
    @Override
    public void onLowMemory(){
        mMapView.onLowMemory();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }


}
