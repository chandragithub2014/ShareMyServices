package com.hyd.bikepool.bikepooler.bikemap;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyd.bikepool.bikepooler.R;
import com.hyd.bikepool.bikepooler.SharedPreferencesUtils;
import com.hyd.bikepool.bikepooler.application.MyApplication;
import com.hyd.bikepool.bikepooler.fragment.ProfileFragment;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PoolerPricingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PoolerPricingFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "from";
    private static final String ARG_PARAM2 = "to";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    View v  = null;
    TextView toAddr,fromAddr;
    Button publishRidebtn;
    SharedPreferencesUtils prefs;
    int mContainerId = -1;
    String distanceinkms = "";
    int distanceinmeteres = -1;
    String duration = "";
    TextView charge;
   JSONObject publishJSON;
    String from_address = "";
    String to_address = "";
    String pickUpTime = "";
    EditText comments;
    private String TAG  = "PoolerPricingFragment";
    public PoolerPricingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PoolerPricingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PoolerPricingFragment newInstance(String param1, String param2) {
        PoolerPricingFragment fragment = new PoolerPricingFragment();
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
        prefs = new SharedPreferencesUtils();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v =  inflater.inflate(R.layout.pricing_layout, container, false);
        mContainerId = container.getId();
        toAddr = (TextView)v.findViewById(R.id.to_addr);
        fromAddr = (TextView) v.findViewById(R.id.from_addr);
        publishRidebtn = (Button)v.findViewById(R.id.publish_btn);
        initPricingLayout(v);
        publishRidebtn.setOnClickListener(this);

        if(getArguments().getString("from")!=null){
           from_address = getArguments().getString("from");
            fromAddr.setText(from_address);
        }
        if(getArguments().getString("to")!=null){
            to_address = getArguments().getString("to");
            toAddr.setText(to_address);
        }

        if(getArguments().getString("inkms")!=null){
            distanceinkms = getArguments().getString("inkms");
            TextView distanceVals = (TextView)v.findViewById(R.id.distance);
            distanceVals.setText("Distance "+distanceinkms);

        }

        if(getArguments().getString("inmeters")!=null){
            distanceinmeteres = Integer.parseInt(getArguments().getString("inmeters"));
            double kms  = (distanceinmeteres)/1000;
            double cost = kms*3;
            Log.d("PoolPricingFragment", "Cost in Rupees:::" + cost);
            if(cost>=20) {
                charge.setText(""+cost);
            }else{
                charge.setText(""+20);
            }


        }

        if(getArguments().getString("time")!=null){
            duration =  getArguments().getString("time");
            TextView timeinmins = (TextView)v.findViewById(R.id.duration);
            timeinmins.setText("Duration"+ duration);
        }
        if(getArguments().getString("pickuptime")!=null){
            pickUpTime = getArguments().getString("pickuptime");
        }

/*
 args.putString("inkms",kms);
                    args.putString("inmeters",meters);
                    args.putString("time",duration);

                    String distanceinkms = "";
    int distanceinmeteres = -1;
    String duration = "";
 */
        return v;
    }
private void initPricingLayout(View v){
    RelativeLayout incrDecrLayout = (RelativeLayout)v.findViewById(R.id.pluse_minus_layout);
    charge = (TextView)incrDecrLayout.findViewById(R.id.price_val);
    RelativeLayout commentsLayout = (RelativeLayout)v.findViewById(R.id.comments_layout);
    comments = (EditText)commentsLayout.findViewById(R.id.commentbox);
}
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.publish_btn:
                prepareForPublish();
                break;
        }
    }

    private void prepareForPublish(){

        if(!TextUtils.isEmpty(prefs.getStringPreferences(getActivity(), "loginType"))){
            String loginType = prefs.getStringPreferences(getActivity(), "loginType");
            Log.d(TAG,"LoginType:::::"+loginType);
            if(!TextUtils.isEmpty(prefs.getStringPreferences(getActivity(), loginType))) {
                fetchDataBasedOnLoginType(loginType);
            }else{
                JSONObject emailXcludingJSON = fetchJSONExcludingEmail();
                MyApplication.getInstance().setPublishJSON(emailXcludingJSON);
                getFragmentManager().beginTransaction()
                        .replace(mContainerId,  ProfileFragment.newInstance(loginType, "frompricing")).addToBackStack(null)
                        .commit();
            }
        }
    }


    private void fetchDataBasedOnLoginType(String loginType){
        JSONObject offerRideJOSN = new JSONObject();
        publishJSON = new JSONObject();
        if(loginType.equalsIgnoreCase("emailProfile") || loginType.equalsIgnoreCase("facebookprofile")) {
            if (!TextUtils.isEmpty(prefs.getStringPreferences(getActivity(), loginType))) {
                try {
                    JSONObject profileTypeJSON = new JSONObject(prefs.getStringPreferences(getActivity(), loginType));
                   String profileEmail = profileTypeJSON.getString("profileEmail");
                   String  profileMobile = profileTypeJSON.getString("profileMobile");
                  String  profileName = profileTypeJSON.getString("profileName");
                 String   profileBikeNum = profileTypeJSON.getString("profileBikeNum");

                    if(!TextUtils.isEmpty(profileEmail) && !TextUtils.isEmpty(profileMobile) && !TextUtils.isEmpty(profileName) && !TextUtils.isEmpty(profileBikeNum)) {
                        publishJSON.put("email",profileEmail);
                        publishJSON.put("mobile", profileMobile);
                        publishJSON.put("profilename",profileName);
                        publishJSON.put("bikeNum",profileBikeNum);

                    }

                    if(!TextUtils.isEmpty(from_address) && !TextUtils.isEmpty(to_address)){
                        publishJSON.put("from",from_address);
                        publishJSON.put("to",to_address);
                    }

                    if(!TextUtils.isEmpty(distanceinkms)&& !TextUtils.isEmpty(duration)){
                        publishJSON.put("distance",distanceinkms);
                        publishJSON.put("duration",duration);
                    }

                    if(!TextUtils.isEmpty(charge.getText().toString())){
                        publishJSON.put("cost",charge.getText().toString());
                    }
                    if(!TextUtils.isEmpty(pickUpTime)){
                        publishJSON.put("pickuptime",pickUpTime);
                    }

                    if(!TextUtils.isEmpty(comments.getText().toString())){
                        publishJSON.put("comments",comments.getText().toString());
                    }
                    offerRideJOSN.put("offerride", publishJSON);

                    if(profileBikeNum.equalsIgnoreCase("N/A")){
                        JSONObject emailXcludingJSON = fetchJSONExcludingEmail();
                        MyApplication.getInstance().setPublishJSON(emailXcludingJSON);
                        getFragmentManager().beginTransaction()
                                .replace(mContainerId,  ProfileFragment.newInstance(loginType, "frompricing")).addToBackStack(null)
                                .commit();
                    }else {
                        Log.d("PoolerPricingFragment", "OfferRideJSON:::" + offerRideJOSN.toString());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private JSONObject fetchJSONExcludingEmail(){
        JSONObject publishJSON = new JSONObject();
        try{
            if(!TextUtils.isEmpty(from_address) && !TextUtils.isEmpty(to_address)){
                publishJSON.put("from",from_address);
                publishJSON.put("to",to_address);
            }

            if(!TextUtils.isEmpty(distanceinkms)&& !TextUtils.isEmpty(duration)){
                publishJSON.put("distance",distanceinkms);
                publishJSON.put("duration",duration);
            }

            if(!TextUtils.isEmpty(charge.getText().toString())){
                publishJSON.put("cost",charge.getText().toString());
            }
            if(!TextUtils.isEmpty(pickUpTime)){
                publishJSON.put("pickuptime",pickUpTime);
            }

            if(!TextUtils.isEmpty(comments.getText().toString())){
                publishJSON.put("comments",comments.getText().toString());
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return publishJSON;
    }
}
