package com.hyd.bikepool.bikepooler.bikemap;


import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hyd.bikepool.bikepooler.R;
import com.hyd.bikepool.bikepooler.SharedPreferencesUtils;
import com.hyd.bikepool.bikepooler.interfaces.BikePoolerReceiveListener;
import com.hyd.bikepool.bikepooler.services.GPSTracker;
import com.hyd.bikepool.bikepooler.webservicehelpers.BikeConstants;
import com.hyd.bikepool.bikepooler.webservicehelpers.BikePoolerAsyncTaskHelper;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BikeMapFragmentActivity#} factory method to
 * create an instance of this fragment.
 */
public class BikeMapFragmentActivity extends Activity implements  BikePoolerReceiveListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    GoogleMap googleMap;
    View v;
    TextView locationTv;

    RelativeLayout footerLayout;
    int mContainerId = -1;
    LocationManager locationManager;
    RelativeLayout parent;
    boolean isActivity = true ;
SharedPreferencesUtils prefs;


    //Sliding Menu
    String[] menu;
    DrawerLayout dLayout;
    ListView dList;
    ArrayAdapter<String> adapter;
    //End
    public BikeMapFragmentActivity() {
        // Required empty public constructor
    }

   /* */

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BikeMapFragment.
     *//*
    // TODO: Rename and change types and number of parameters
    public static BikeMapActivity newInstance(String param1, String param2) {
        BikeMapActivity fragment = new BikeMapActivity();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*/
    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();
            googleMap.setMyLocationEnabled(true);

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_bike_mapo);
        Log.d("MAPTAg", "In onCreate()");


        /*menu = new String[]{"Home","Android","Windows","Linux","Raspberry Pi","WordPress","Videos","Contact Us"};
        dLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        dList = (ListView) findViewById(R.id.left_drawer);*/

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,menu);

        dList.setAdapter(adapter);
        dList.setSelector(android.R.color.holo_blue_dark);


        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        prefs = new SharedPreferencesUtils();
        String preferences = prefs.getStringPreferences(this, BikeConstants.BIKE_PREFS_DATA);
        if(!TextUtils.isEmpty(preferences)){
            Log.d("MAP",preferences);
        }

        footerLayout = (RelativeLayout) findViewById(R.id.footer_layout);
        locationTv = (TextView)findViewById(R.id.latlongLocation);
        parent = (RelativeLayout)findViewById(R.id.framelayoutt);
        try {
            // Loading map
            initilizeMap();

        } catch (Exception e) {
            e.printStackTrace();
        }

        initGPS();
      TextView offerRide = (TextView)footerLayout.findViewById(R.id.offer_ride);
        offerRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                isActivity = false;
             //   parent.setVisibility(View.INVISIBLE);
               parent.removeAllViews();
                Fragment f = new BikePlaceFragment();
                Bundle args = new Bundle();
                if(!TextUtils.isEmpty(locationTv.getText().toString())) {
                    args.putString("from", locationTv.getText().toString());
                    f.setArguments(args);
                }


               getFragmentManager().beginTransaction()
                        .replace(R.id.parentLayout, f)
                        .commit();
            }
        });

        TextView findRide = (TextView)footerLayout.findViewById(R.id.find_ride);
        findRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  parent.setVisibility(View.INVISIBLE);
                isActivity = false;
              parent.removeAllViews();
                Fragment f = new RideFinderFragment();
                Bundle args = new Bundle();
                if(!TextUtils.isEmpty(locationTv.getText().toString())) {
                    args.putString("from", locationTv.getText().toString());
                    f.setArguments(args);
                }
                getFragmentManager().beginTransaction()
                        .replace(R.id.parentLayout, f)
                        .commit();


            }
        });
        // and next place it, for exemple, on bottom right (as Google Maps app)

       /* getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getFragmentManager().getBackStackEntryCount() == 0) {
                    Intent intent = new Intent(BikeMapFragmentActivity.this, BikeMapFragmentActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish(); // call this to finish the current activity
                }
            }
        });*/
    }
private void initGPS(){
    // GPSTracker class
    GPSTracker gps;
    // create class object
    gps = new GPSTracker(BikeMapFragmentActivity.this);

    // check if GPS enabled
    if(gps.canGetLocation()){

        double latitude = gps.getLatitude();
        double longitude = gps.getLongitude();
// create marker
        MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude))/*.title("Hello Maps ")*/;

// adding marker
        googleMap.addMarker(marker);


        //Update Camera Pos
        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                new LatLng(latitude, longitude)).zoom(12).build();

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        new BikePoolerAsyncTaskHelper(BikeMapFragmentActivity.this,BikeMapFragmentActivity.this,latitude,longitude).execute();

      /*  String locationAddress = getLocationName(latitude,longitude);
        if(!TextUtils.isEmpty(locationAddress)) {
            locationTv.setText(locationAddress);
        }*/

        //End
        // \n is for new line
       // Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
    }else{
        // can't get location
        // GPS or Network is not enabled
        // Ask user to enable GPS/network in settings
        gps.showSettingsAlert();
    }
}


    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(BikeMapFragmentActivity.this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, BikeMapFragmentActivity.this, 0).show();
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        Log.d("TAG",""+isActivity);
        if (getFragmentManager().getBackStackEntryCount() == 0 && !isActivity) {
            Log.d("TAG", "onBackPressed");
            finish();
            Intent i = new Intent(BikeMapFragmentActivity.this, BikeMapFragmentActivity.class); // Your list's Intent
            i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY); // Adds the FLAG_ACTIVITY_NO_HISTORY flag
            startActivity(i);

        /*    Intent intent = new Intent(BikeMapFragmentActivity.this, BikeMapFragmentActivity.class);
          //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);*/
             // call this to finish the current activity
        } else if(!isActivity && getFragmentManager().getBackStackEntryCount() != 0){
            super.onBackPressed();
            }
        else if(isActivity) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Map","onResume()");
        initilizeMap();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("Map", "onRestart()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Map", "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Map", "onPause()");
    }

    @Override
    public void receiveResult(String result) {
        if(!TextUtils.isEmpty(result)) {
            Log.d("MAP", "Received Rsult:::" + result);
            locationTv.setText(result);
        }
    }
}

//http://www.androidhive.info/2013/08/android-working-with-google-maps-v2/
//http://vinsol.com/blog/2014/10/01/handling-back-button-press-inside-fragments/