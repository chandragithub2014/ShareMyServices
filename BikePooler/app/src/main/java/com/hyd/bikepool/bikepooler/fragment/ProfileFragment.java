package com.hyd.bikepool.bikepooler.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hyd.bikepool.bikepooler.R;
import com.hyd.bikepool.bikepooler.SharedPreferencesUtils;
import com.hyd.bikepool.bikepooler.application.MyApplication;
import com.hyd.bikepool.bikepooler.slidingmenu.SlidingMenuActivity;
import com.hyd.bikepool.bikepooler.utils.Toaster;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    View view = null;
    EditText name,email,mobile,bikeNum;
    Button saveProf;
    SharedPreferencesUtils prefs;
    int mContainerId = -1;
    private  String profileEmail,profileName,profileMobile,profileBikeNum;
String loginType;
    boolean isFromPricePooling = false;
    boolean isFromRider = false;
    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        if(!TextUtils.isEmpty(mParam2)){
           if(mParam2.equalsIgnoreCase("frompricing")){
               isFromPricePooling = true;
           }
            if(mParam2.equalsIgnoreCase("fromridefinder")){
                isFromRider = true;
            }
        }
        prefs = new SharedPreferencesUtils();
        loginType = prefs.getStringPreferences(getActivity(),"loginType");
        Log.d("Profile", "mParam1::::" + loginType);
       if(!TextUtils.isEmpty(mParam1)) {
           fetchDataFromSharedPreferencesBasedOnLoginType(mParam1);
       }else{
             if(!TextUtils.isEmpty(loginType)){
                 fetchDataBasedOnLoginType(loginType);
             }
       }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContainerId = container.getId();
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        initViews(view);
        populateData();
        if(isFromRider){
            bikeNum.setText("N/A");
        }else if(isFromPricePooling){
            bikeNum.setText("");
        }
        saveProf.setOnClickListener(this);
        return view;
    }

    private void initViews(View v){
        name = (EditText)v.findViewById(R.id.pooler_name);
        email = (EditText)v.findViewById(R.id.pooler_email);
        mobile = (EditText)v.findViewById(R.id.pooler_mobile);
        bikeNum = (EditText)v.findViewById(R.id.bike_num);
        saveProf = (Button)v.findViewById(R.id.saveprof);
    }

    private void populateData(){
        if(!TextUtils.isEmpty(profileEmail)){
            email.setText(profileEmail);
        } if(!TextUtils.isEmpty(profileMobile)){
            mobile.setText(profileMobile);
        }  if(!TextUtils.isEmpty(profileName)){
            name.setText(profileName);
        }  if(!TextUtils.isEmpty(profileBikeNum)){
            bikeNum.setText(profileBikeNum);
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.saveprof:
               if(validateProfileDetails()){
                   buildAndSaveJSONToSharedPrefs();
               }
                break;
        }
    }


    private boolean validateProfileDetails(){

         boolean isSaveData = true;
        if(TextUtils.isEmpty(name.getText().toString())){
            Toaster.getInstance().displayToast("Name field should n't be  empty",getActivity());
            isSaveData = false;
          }else if(TextUtils.isEmpty(email.getText().toString())){
            Toaster.getInstance().displayToast("Email field should n't be  empty",getActivity());
            isSaveData = false;
        }else if(TextUtils.isEmpty(mobile.getText().toString())){
            Toaster.getInstance().displayToast("Mobile field should n't be  empty",getActivity());
            isSaveData = false;
        }else if(TextUtils.isEmpty(bikeNum.getText().toString())){
            Toaster.getInstance().displayToast("Bike Number  field should n't be  empty",getActivity());
            isSaveData = false;
        }
        return  isSaveData;
    }

    private void buildAndSaveJSONToSharedPrefs(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("profileName", name.getText().toString());
            jsonObject.put("profileEmail", email.getText().toString());
            jsonObject.put("profileMobile", mobile.getText().toString());
            jsonObject.put("profileBikeNum", bikeNum.getText().toString());
            if(loginType.equalsIgnoreCase("facebookprofile")){
                prefs.saveStringPreferences(getActivity(), "facebookprofile", jsonObject.toString());
            }else {

                prefs.saveStringPreferences(getActivity(), "emailProfile", jsonObject.toString());
            }

            /*Intent i = new Intent(getActivity(), SlidingMenuActivity.class);
            startActivity(i);
            getActivity().finish();*/
            JSONObject finalRequestJSON  = getResultJSON(isFromPricePooling, isFromRider);
            Log.d("ProfileFragment", "Final RequestJSON from Profile Fragment:::" + finalRequestJSON.toString());
        /*   if(isFromPricePooling){
               Toast.makeText(getActivity(),"Published Offer",Toast.LENGTH_LONG).show();
               JSONObject finalRequestJSON  = getResultJSON( isFromPricePooling, isFromRider);
               JSONObject publishJSON = MyApplication.getInstance().getPublishJSON();
               if(publishJSON!=null){
                   if(!TextUtils.isEmpty(email.getText().toString()) && !TextUtils.isEmpty(mobile.getText().toString()) && !TextUtils.isEmpty(name.getText().toString()) && !TextUtils.isEmpty(bikeNum.getText().toString())) {
                       publishJSON.put("email",email.getText().toString());
                       publishJSON.put("mobile", mobile.getText().toString());
                       publishJSON.put("profilename",name.getText().toString());
                       publishJSON.put("bikeNum",bikeNum.getText().toString());

                   }
                   JSONObject offerRideJOSN = new JSONObject();
                   offerRideJOSN.put("offerride",publishJSON);
                   Log.d("ProfileFragment", "OfferRideJSON from Profile Fragment:::" + offerRideJOSN.toString());
               }
               isFromPricePooling = false;
           }*/
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private JSONObject getResultJSON(boolean isFromPricePooling,boolean isFromRider){
        JSONObject resultJSON = new JSONObject();
   try{
       JSONObject publishJSON = MyApplication.getInstance().getPublishJSON();
       if(publishJSON!=null){
           if(!TextUtils.isEmpty(email.getText().toString()) && !TextUtils.isEmpty(mobile.getText().toString()) && !TextUtils.isEmpty(name.getText().toString()) && !TextUtils.isEmpty(bikeNum.getText().toString())) {
               publishJSON.put("email",email.getText().toString());
               publishJSON.put("mobile", mobile.getText().toString());
               publishJSON.put("profilename", name.getText().toString());
               if(isFromPricePooling) {
                   publishJSON.put("bikeNum", bikeNum.getText().toString());
               }

           }
           if(isFromPricePooling) {
               resultJSON.put("offerride", publishJSON);
           }else if(isFromRider){
               resultJSON.put("findride", publishJSON);
           }
           Log.d("ProfileFragment", "OfferRideJSON from Profile Fragment:::" + resultJSON.toString());
       }
   }catch (JSONException e){
       e.printStackTrace();
   }
        return  resultJSON;
    }

    private void fetchDataFromSharedPreferencesBasedOnLoginType(String loginType){
        Log.d("Profile","LoginType::::"+loginType);
        if(loginType.equalsIgnoreCase("emailProfile")) {
            //  if (loginType.equalsIgnoreCase("email")) {
            try {
                JSONObject emailJSON = new JSONObject(prefs.getStringPreferences(getActivity(), "email"));
                profileMobile = prefs.getStringPreferences(getActivity(), "PhoneNumber");
                //emailPwdJson.put("emailId"
                profileEmail = emailJSON.getString("emailId");
            } catch (JSONException e) {
                e.printStackTrace();
                //  }
            }
        }else if(loginType.equalsIgnoreCase("facebookprofile")){
            try {
                JSONObject fbbokJSON = new JSONObject(prefs.getStringPreferences(getActivity(), "facebook"));
                profileName = fbbokJSON.getString("profilename");
                //emailPwdJson.put("emailId"
              //  profileEmail = emailJSON.getString("emailId");
            } catch (JSONException e) {
                e.printStackTrace();
                //  }
            }
        }
    }

    private void fetchDataBasedOnLoginType(String loginType){
        if(loginType.equalsIgnoreCase("emailProfile") || loginType.equalsIgnoreCase("facebookprofile")) {
            if (!TextUtils.isEmpty(prefs.getStringPreferences(getActivity(), loginType))) {
                try {
                    JSONObject profileTypeJSON = new JSONObject(prefs.getStringPreferences(getActivity(), loginType));
                    profileEmail = profileTypeJSON.getString("profileEmail");
                    profileMobile = profileTypeJSON.getString("profileMobile");
                    profileName = profileTypeJSON.getString("profileName");
                    profileBikeNum = profileTypeJSON.getString("profileBikeNum");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
