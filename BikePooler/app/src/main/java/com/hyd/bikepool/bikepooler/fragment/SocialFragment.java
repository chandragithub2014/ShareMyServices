package com.hyd.bikepool.bikepooler.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.hyd.bikepool.bikepooler.R;
import com.hyd.bikepool.bikepooler.SharedPreferencesUtils;
import com.hyd.bikepool.bikepooler.services.GooglePlusService;
import com.hyd.bikepool.bikepooler.slidingmenu.SlidingMenuActivity;
import com.hyd.bikepool.bikepooler.webservicehelpers.BikeConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SocialFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SocialFragment extends Fragment implements View.OnClickListener/*, GoogleApiClient.OnConnectionFailedListener ,GoogleApiClient.ConnectionCallbacks*/{
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

    private static final String TAG = "SocialFragment";
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    private ConnectionResult mConnectionResult;

    private boolean mSignInButtonClicked = false;

    private EditText loginEmail,loginPassword;


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
        Log.d(TAG, "in onCreate()...........");
        sharedUtils = new SharedPreferencesUtils();
        initFB();
       //  initializeFaceBook();
      // initializeGoogleSignIn();
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
            Button login = (Button)loginLayout.findViewById(R.id.login);
            login.setOnClickListener(this);
            loginEmail = (EditText)loginLayout.findViewById(R.id.login_email);
            loginPassword = (EditText)loginLayout.findViewById(R.id.login_pwd);

        }
/*
    private void initializeGoogleSignIn(){
      Log.d(TAG, "In initializeGoogleSignIn()");
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(SocialFragment.this)
                .addOnConnectionFailedListener(SocialFragment.this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PLUS_LOGIN))
                .addScope(new Scope(Scopes.PLUS_ME))
                .build();
        mGoogleApiClient.connect();


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "In onConnectionFailed()");
    }

    @Override
    public void onConnected( Bundle bundle) {
      //  dismissProgressDialog();
        Log.d(TAG, "In onConnected()");
        if (mGoogleApiClient != null) {
            Plus.PeopleApi.load(mGoogleApiClient, "signed_in_user_account_id").setResultCallback(new ResultCallback<People.LoadPeopleResult>() {
                @Override
                public void onResult(@NonNull People.LoadPeopleResult loadPeopleResult) {
                    Person person = loadPeopleResult.getPersonBuffer().get(0);
                    Log.d("GooglePlusService", "Person loaded");
                    Log.d("GooglePlusService", person.getName().getGivenName());
                    Log.d("GooglePlusService", person.getName().getFamilyName());
                    Log.d("GooglePlusService", person.getDisplayName());
                    Log.d("GooglePlusService", person.getGender() + "");

                }
            });
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

*/
    private ProgressDialog initializeProgressDialog() {
        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setIndeterminate(true);
        dialog.setMessage("loading...");
        return dialog;
    }

    private void showProgressDialog() {
        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.gplus:
                googlePlusLogin();
                break;
            case R.id.fbook:
                 fbLogin();
                //onFblogin();
                break;
            case R.id.register_login:
                getFragmentManager().beginTransaction()
                        .replace(mContainerId,  RegistrationFragment.newInstance("", ""))
                        .commit();
                break;
            case R.id.login:
                 validateLogin();
                break;

        }
    }
  private void validateLogin(){
    if(!TextUtils.isEmpty(loginEmail.getText().toString()) && !TextUtils.isEmpty(loginPassword.getText().toString())){
        String email = loginEmail.getText().toString();
        String password = loginPassword.getText().toString();
        String emailJSON = sharedUtils.getStringPreferences(getActivity(),"email");
        if(!TextUtils.isEmpty(emailJSON)){
            try{
                JSONObject emailJSONObject = new JSONObject(emailJSON);
                String jsonEmailVal  = emailJSONObject.getString("emailId");
                String jsonPassVal =  emailJSONObject.getString("password");

                if(email.equalsIgnoreCase(jsonEmailVal)){
                    if(password.equalsIgnoreCase(jsonPassVal)){
        //                launchMapSlidingMenu();
                        sharedUtils.saveStringPreferences(getActivity(),"loginType","emailProfile");
                        getFragmentManager().beginTransaction().replace(mContainerId,new BikePoolerMapFragment()).commit();
                    }else{
                        Toast.makeText(getActivity(),"Wrong password",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getActivity(),"Email not found",Toast.LENGTH_LONG).show();
                }

            }catch (JSONException e){
                e.printStackTrace();
            }


        }
    }else{
        Toast.makeText(getActivity(),"Login Fields Can't be Empty",Toast.LENGTH_LONG).show();
    }
}
    private void launchMapSlidingMenu(){
        sharedUtils.saveStringPreferences(getActivity(),"loginType","emailProfile");
        Intent i = new Intent(getActivity(), SlidingMenuActivity.class);
        startActivity(i);
        getActivity().finish();
    }
private void googlePlusLogin(){
  //  GooglePlusService.getInstance().startMessageService(getActivity());

   /* getFragmentManager().beginTransaction()
            .replace(mContainerId,  LoginFragment.newInstance(0))
            .commit();*/
   /* mProgressDialog = initializeProgressDialog();
    showProgressDialog();
    if (mConnectionResult != null) {
        resolveConnection();
    } else {
        // for cases when button is clicked before any connection result
        mSignInButtonClicked = true;
    }*/
}

    private void fbLogin(){
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));
    }
  private void initFB(){
    FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
    callbackManager = CallbackManager.Factory.create();
    LoginManager.getInstance().registerCallback(callbackManager,
            new FacebookCallback<LoginResult>() {
                private ProfileTracker mProfileTracker;
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Log.d("Success", "Login");

                    Profile profile = Profile.getCurrentProfile();
                    if(profile!=null) {
                        displayMessage(profile);
                    }else{
                        mProfileTracker = new ProfileTracker() {
                            @Override
                            protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                                Log.v("facebook - profile", "Profile2:::"+profile2.getFirstName());
                                displayMessage(profile2);
                                mProfileTracker.stopTracking();
                            }
                        };
                        mProfileTracker.startTracking();
                    }

                }

                @Override
                public void onCancel() {
                    Toast.makeText(getActivity(), "Login Cancel", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onError(FacebookException exception) {
                    Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
}
    private void initializeFaceBook(){
        Log.d(TAG, "initializeFacebook.......");

if(accessTokenTracker!=null && profileTracker!=null){
    profileTracker.stopTracking();
    accessTokenTracker.stopTracking();
}

        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());

        callbackManager = CallbackManager.Factory.create();

      accessTokenTracker= new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
                 Log.d(TAG,"onCurrentAccessTokenChanged()");
            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                //displayMessage(newProfile);
                Log.d(TAG,"onCurrentProfileChanged()");
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

            JSONObject fbookJson = new JSONObject();
            try {
                fbookJson.put("profilename", profile.getName());


            }
            catch (JSONException e){
                e.printStackTrace();
            }
            sharedUtils.saveStringPreferences(getActivity(), "facebook", fbookJson.toString());

            sharedUtils.saveStringPreferences(getActivity(), "loginType", "facebookprofile");


            /*Intent i = new Intent(getActivity(), SlidingMenuActivity.class);
            startActivity(i);
            getActivity().finish();*/
            getFragmentManager().beginTransaction().replace(mContainerId,new BikePoolerMapFragment()).commit();
        }
    }

    private void launchNext(){
        sharedUtils.saveStringPreferences(getActivity(), "loginType", "facebook");
            /*Intent i = new Intent(getActivity(), SlidingMenuActivity.class);
            startActivity(i);
            getActivity().finish();*/
        getFragmentManager().beginTransaction().replace(mContainerId,new BikePoolerMapFragment()).commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG,"onActvityResult ......");
          callbackManager.onActivityResult(requestCode, resultCode, data);
          super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
       /* Profile profile = Profile.getCurrentProfile();
        displayMessage(profile);*/
    }


   @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "In onStop().........");

   }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "In onDestroy().........");
        /*profileTracker.stopTracking();
        accessTokenTracker.stopTracking();*/
    }


    //FaceBook Login

    private void onFblogin()
    {
        callbackManager = CallbackManager.Factory.create();

        // Set permissions
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email","user_photos","public_profile"));

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        System.out.println("Success");
                        GraphRequest.newMeRequest(
                                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject json, GraphResponse response) {
                                        if (response.getError() != null) {
                                            // handle error
                                            System.out.println("ERROR");
                                        } else {
                                            System.out.println("Success");
                                            try {

                                                String jsonresult = String.valueOf(json);
                                                System.out.println("JSON Result"+jsonresult);

                                                String str_email = json.getString("email");
                                                String str_id = json.getString("id");
                                                String str_firstname = json.getString("first_name");
                                                String str_lastname = json.getString("last_name");

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                }).executeAsync();

                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG,"On cancel");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(TAG,error.toString());
                    }
                });
    }

    //End Facebook Login
}
