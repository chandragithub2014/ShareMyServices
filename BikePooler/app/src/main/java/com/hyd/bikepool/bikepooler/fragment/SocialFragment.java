package com.hyd.bikepool.bikepooler.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.hyd.bikepool.bikepooler.R;
import com.hyd.bikepool.bikepooler.SharedPreferencesUtils;
import com.hyd.bikepool.bikepooler.slidingmenu.SlidingMenuActivity;
import com.hyd.bikepool.bikepooler.webservicehelpers.BikeConstants;

import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SocialFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SocialFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;



    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;


    private CallbackManager callbackManager;

    SharedPreferencesUtils sharedUtils ;

    TextView sign_up;
    int mContainerId = -1;
    public SocialFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SocialFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SocialFragment newInstance(String param1, String param2) {
        SocialFragment fragment = new SocialFragment();
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
        sharedUtils = new SharedPreferencesUtils();
        initializeFaceBook();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_social, container, false);
        mContainerId = container.getId();
       ImageButton gPlus = (ImageButton)view.findViewById(R.id.gplus);
        gPlus.setOnClickListener(this);
        ImageButton fBook = (ImageButton)view.findViewById(R.id.fbook);
        fBook.setOnClickListener(this);
        initializeLayout(view);
        return view;
    }

        private  void  initializeLayout(View v){
            LinearLayout loginLayout = (LinearLayout)v.findViewById(R.id.login_pooler);
            sign_up = (TextView)loginLayout.findViewById(R.id.register_login);
            sign_up.setOnClickListener(this);

        }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.gplus:
               // signIn();
                break;
            case R.id.fbook:
                fbLogin();
                break;
            case R.id.register_login:
                getFragmentManager().beginTransaction()
                        .replace(mContainerId,  RegistrationFragment.newInstance("", ""))
                        .commit();
                break;

        }
    }


    private void fbLogin(){
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));
    }

    private void initializeFaceBook(){
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());

        callbackManager = CallbackManager.Factory.create();

        accessTokenTracker= new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {

            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                //displayMessage(newProfile);
            }
        };

        accessTokenTracker.startTracking();
        profileTracker.startTracking();
    }

    private void displayMessage(Profile profile){
        if(profile != null){
            Log.d("PROFILE", profile.getName());
            Toast.makeText(getActivity(), "Facebook Profile Name:::" + profile.getName(), Toast.LENGTH_LONG).show();
            sharedUtils.saveStringPreferences(getActivity(), BikeConstants.BIKE_PREFS_DATA, profile.getName());
            Intent i = new Intent(getActivity(), SlidingMenuActivity.class);
            startActivity(i);
            getActivity().finish();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        Profile profile = Profile.getCurrentProfile();
        displayMessage(profile);
    }


    @Override
    public void onStop() {
        super.onStop();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }
}
