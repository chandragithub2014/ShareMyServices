/*
package com.hyd.bikepool.bikepooler.bikemap;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hyd.bikepool.bikepooler.R;
import com.hyd.bikepool.bikepooler.interfaces.BikePoolerReceiveListener;
import com.hyd.bikepool.bikepooler.webservicehelpers.BikePoolerAsyncTaskHelper;

*/
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BikeMapActivity#} factory method to
 * create an instance of this fragment.
 *//*

public class BikeMapActivity extends AppCompatActivity implements LocationListener,BikePoolerReceiveListener{
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

    public BikeMapActivity() {
        // Required empty public constructor
    }

   */
/* *//*
*/
/**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment BikeMapFragment.
     *//*
*/
/*
    // TODO: Rename and change types and number of parameters
    public static BikeMapActivity newInstance(String param1, String param2) {
        BikeMapActivity fragment = new BikeMapActivity();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*//*


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_bike_map);
        Log.d("MAPTAg", "In onCreate()");
         if (!isGooglePlayServicesAvailable()) {
           finish();
        }
        parent = (RelativeLayout)findViewById(R.id.framelayoutt);
        locationTv = (TextView)findViewById(R.id.latlongLocation);
        SupportMapFragment supportMapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);


        googleMap = supportMapFragment.getMap();
        googleMap.setMyLocationEnabled(true);
        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        if (locationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.removeUpdates(BikeMapActivity.this);
                }
            }
        }
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        if (location != null) {
            onLocationChanged(location);
        }
        locationManager.requestLocationUpdates(bestProvider, 20000, 0, this);


        // Get the button view
        View locationButton = ((View) findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        footerLayout = (RelativeLayout)findViewById(R.id.footer_layout);
        TextView offerRide = (TextView)footerLayout.findViewById(R.id.offer_ride);
        offerRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



             //   parent.setVisibility(View.INVISIBLE);
                parent.removeAllViews();
                Fragment f = new BikePlaceFragment();
                Bundle args = new Bundle();
                if(!TextUtils.isEmpty(locationTv.getText().toString())) {
                    args.putString("from", locationTv.getText().toString());
                    f.setArguments(args);
                }


               getSupportFragmentManager().beginTransaction()
                        .replace(R.id.framelayout, f).addToBackStack(null)
                        .commit();
            }
        });

        TextView findRide = (TextView)footerLayout.findViewById(R.id.find_ride);
        findRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  parent.setVisibility(View.INVISIBLE);
                parent.removeAllViews();
                Fragment f = new RideFinderFragment();
                Bundle args = new Bundle();
                if(!TextUtils.isEmpty(locationTv.getText().toString())) {
                    args.putString("from", locationTv.getText().toString());
                    f.setArguments(args);
                }
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.framelayout, f).addToBackStack(null)
                        .commit();

              */
/*  getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(mContainerId, f).addToBackStack(null)
                        .commit();*//*

            }
        });
        // and next place it, for exemple, on bottom right (as Google Maps app)
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 30, 30);




    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(BikeMapActivity.this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, BikeMapActivity.this, 0).show();
            return false;
        }
    }

    */
/*@Override
    public void onDestroyView() {
      *//*
*/
/*  try {
            SupportMapFragment mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));

            *//*
*/
/**//*
*/
/*FragmentManager fmanager = getActivity().getSupportFragmentManager();
            Fragment fragment = fmanager.findFragmentById(R.id.map);
            SupportMapFragment mapFragment = (SupportMapFragment)fragment;*//*
*/
/**//*
*/
/*
if(mapFragment!=null) {


    FragmentTransaction ft = getActivity().getSupportFragmentManager()
            .beginTransaction();
    ft.remove(mapFragment);
    ft.commit();
}
        } catch (Exception e) {
            e.printStackTrace();
        }*//*
*/
/*

        super.onDestroyView();
    }*//*




   */
/* @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.d("TAG","onViewCreated");
      *//*
*/
/*  if(googleMap==null) {
            Log.d("TAG","If   onViewCreated");
            SupportMapFragment supportMapFragment =
                    (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            googleMap = supportMapFragment.getMap();
            googleMap.setMyLocationEnabled(true);
        }else{
            Log.d("TAG","else  onViewCreated");
            googleMap.setMyLocationEnabled(true);
        }*//*
*/
/*
    }*//*


    @Override
    public void onResume() {
        super.onResume();
        Log.d("TAG", "onResume");
//if(parent.getVisibility() == View.INVISIBLE){
  //  parent.setVisibility(View.VISIBLE);
//}

    }

    @Override
    public void onLocationChanged(Location location) {

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions().position(latLng));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
     //   locationTv.setText("Latitude:" + latitude + ", Longitude:" + longitude);
        new BikePoolerAsyncTaskHelper(BikeMapActivity.this,BikeMapActivity.this,latitude,longitude).execute();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void receiveResult(String result) {
        Log.d("BIKEMAP","receiveresult");
        if(!TextUtils.isEmpty(result)){
            locationTv.setText(result);
        }
        FrameLayout vg = (FrameLayout)findViewById(R.id.framelayout);
        vg.invalidate();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("LocationManager","BuidSDK::::"+Build.VERSION.SDK_INT);
        if (locationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        ||checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.removeUpdates(BikeMapActivity.this);
                    locationManager = null;
                    Log.d("LocationManager", "Nullified Location Manager");
                }
            }else{
                locationManager.removeUpdates(BikeMapActivity.this);
                locationManager = null;
                Log.d("LocationManager", "Nullified Location Manager");
            }
        }
    }

   */
/* @Override
    public void onStop() {
        super.onStop();
        if (locationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        || getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.removeUpdates(BikeMapFragment.this);
                    locationManager = null;
                    Log.d("LocationManager", "Nullified Location Manager");
                }
            }
        }
    }*//*

}


//API KEY
//AIzaSyBwrxuGIUIwHhibDpK_QrRrY-2snydXkVg


//http://www.nasc.fr/android/android-using-layout-as-custom-marker-on-google-map-api/*/
