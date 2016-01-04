package com.hyd.bikepool.bikepooler.fragment;


import android.app.FragmentTransaction;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hyd.bikepool.bikepooler.R;
import com.hyd.bikepool.bikepooler.SharedPreferencesUtils;
import com.hyd.bikepool.bikepooler.bikemap.BikePlaceFragment;
import com.hyd.bikepool.bikepooler.bikemap.RideFinderFragment;
import com.hyd.bikepool.bikepooler.interfaces.BikePoolerReceiveListener;
import com.hyd.bikepool.bikepooler.services.GPSTracker;
import com.hyd.bikepool.bikepooler.webservicehelpers.BikeConstants;
import com.hyd.bikepool.bikepooler.webservicehelpers.BikePoolerAsyncTaskHelper;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BikePoolerMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BikePoolerMapFragment extends Fragment implements BikePoolerReceiveListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    GoogleMap googleMap;
    View view=null;
    TextView locationTv;

    RelativeLayout footerLayout;
    int mContainerId = -1;
    LocationManager locationManager;
    RelativeLayout parent;
    boolean isActivity = true ;
    SharedPreferencesUtils prefs;
    int mContainerID = -1;

    public BikePoolerMapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BikePoolerMapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BikePoolerMapFragment newInstance(String param1, String param2) {
        BikePoolerMapFragment fragment = new BikePoolerMapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        if (!isGooglePlayServicesAvailable()) {
            getActivity().finish();
        }
        prefs = new SharedPreferencesUtils();
        String preferences = prefs.getStringPreferences(getActivity(), BikeConstants.BIKE_PREFS_DATA);
        prefs.saveBooleanPreferences(getActivity(), BikeConstants.BIKE_BOOLEAN_PREFS_DATA,true);


        if(!TextUtils.isEmpty(preferences)){
            Log.d("MAP", preferences);
        }
    }


    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getChildFragmentManager().findFragmentById(
                    R.id.map)).getMap();
            googleMap.setMyLocationEnabled(true);

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getActivity(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_bike_mapo, container, false);
        mContainerID = container.getId();

      Log.d("BikePoolerMapFragment","In onCreateView() of BikePoolerMapFragment");
        //New
        footerLayout = (RelativeLayout) view.findViewById(R.id.footer_layout);
        locationTv = (TextView)view.findViewById(R.id.latlongLocation);
        parent = (RelativeLayout)view.findViewById(R.id.framelayoutt);
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


        //End
        return view;
    }


    private void initGPS(){
        // GPSTracker class
        GPSTracker gps;
        // create class object
        gps = new GPSTracker(getActivity());

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

            new BikePoolerAsyncTaskHelper(getActivity(),BikePoolerMapFragment.this,latitude,longitude).execute();

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

    @Override
    public void onResume() {
        super.onResume();
        Log.d("MapFragment", "In onResume");
    }

   /* @Override
    public void onDestroyView() {


        try {
            Fragment fragment = (getChildFragmentManager()
                    .findFragmentById(R.id.map));
            FragmentTransaction ft = getActivity().getFragmentManager()
                    .beginTransaction();
            ft.remove(fragment);
            ft.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
      super.onDestroyView();
    }*/



    private void positionLocationButton(){
        View locationButton = ((View) view.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 30, 30);
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, getActivity(), 0).show();
            return false;
        }
    }

    @Override
    public void receiveResult(String result) {
        if(!TextUtils.isEmpty(result)) {
            Log.d("MAP", "Received Rsult:::" + result);
            locationTv.setText(result);
        }
    }



}
//Run time
//http://inthecheesefactory.com/blog/things-you-need-to-know-about-android-m-permission-developer-edition/en