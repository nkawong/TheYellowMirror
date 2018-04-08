package com.theyellowmirror.sightseer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.MapView;
import java.util.*;
import android.util.Log;
import android.widget.*;

import org.w3c.dom.Text;


public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private static final int GPS_ERRORDIALOG_REQUEST = 9001;
    MapView mMapView;
    Map<Integer,String> finalOrder = new HashMap<>(5);
    ArrayList<String> routesNoOrder = new ArrayList<>(5);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.w("HEHE","message");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Map Stuff
        mMapView = (MapView) findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);


        //Routing stuff
        Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,R.array.priority_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner2.setAdapter(adapter2);

        Spinner spinner3 = (Spinner) findViewById(R.id.spinner3);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this,R.array.priority_array, android.R.layout.simple_spinner_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner3.setAdapter(adapter3);

        Spinner spinner4 = (Spinner) findViewById(R.id.spinner4);
        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(this,R.array.priority_array, android.R.layout.simple_spinner_item);
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner4.setAdapter(adapter4);

        Spinner spinner5 = (Spinner) findViewById(R.id.spinner5);
        ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(this,R.array.priority_array, android.R.layout.simple_spinner_item);
        adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner5.setAdapter(adapter5);



        final Button startRouteBT = (Button) findViewById(R.id.startRouteBT);
        startRouteBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                //I think adding the address in order need to be done by checking the distance, so this is not right
                EditText address = (EditText) findViewById(R.id.address1ET);
                Spinner mySpinner = (Spinner) findViewById(R.id.spinner2);
                String text = mySpinner.getSelectedItem().toString();

                finalOrder.put(0,address.getText().toString());



                //for address 2, if the user wants 2nd address to be the 2nd address they are visiting, add to the finalOrder
                //if they dont, add to the routesNorder array, for further calculation
                address = (EditText) findViewById(R.id.address2ET);
                if(text.equals("-")){
                    routesNoOrder.add(address.getText().toString());
                }
                else{
                    finalOrder.put(2,address.getText().toString());
                }

                //address 3
                mySpinner = (Spinner) findViewById(R.id.spinner3);
                text = mySpinner.getSelectedItem().toString();
                address = (EditText) findViewById(R.id.address3ET);
                if(text.equals("-")){
                    routesNoOrder.add(address.getText().toString());
                }
                else{
                    finalOrder.put(3,address.getText().toString());
                }

                //address 4
                mySpinner = (Spinner) findViewById(R.id.spinner4);
                text = mySpinner.getSelectedItem().toString();
                address = (EditText) findViewById(R.id.address4ET);
                if(text.equals("-")){
                    routesNoOrder.add(address.getText().toString());
                }
                else{
                    finalOrder.put(4,address.getText().toString());
                }

                //address 5
                mySpinner = (Spinner) findViewById(R.id.spinner5);
                text = mySpinner.getSelectedItem().toString();
                address = (EditText) findViewById(R.id.address5ET);
                if(text.equals("-")){
                    routesNoOrder.add(address.getText().toString());
                }
                else{
                    finalOrder.put(5,address.getText().toString());
                }

                //by the end, all the address with priority should be stored in the Map(finalOrder) with their respected value
                //all the address without priority should be stored in the arraylist(routesNoOrder)

            }
        });


    }


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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_setting) {
            // Handle the camera action
        } else if (id == R.id.nav_history) {

        } else if (id == R.id.nav_logout) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //map activities

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


