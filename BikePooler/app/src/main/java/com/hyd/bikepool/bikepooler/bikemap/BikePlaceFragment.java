package com.hyd.bikepool.bikepooler.bikemap;


import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.hyd.bikepool.bikepooler.R;
import com.hyd.bikepool.bikepooler.SharedPreferencesUtils;
import com.hyd.bikepool.bikepooler.interfaces.DistanceFinderReceiveListener;
import com.hyd.bikepool.bikepooler.webservicehelpers.BikeConstants;
import com.hyd.bikepool.bikepooler.webservicehelpers.BikePoolerAsyncTaskHelper;
import com.hyd.bikepool.bikepooler.webservicehelpers.DistanceTimeFinderAsyncTaskHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BikePlaceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BikePlaceFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener,DistanceFinderReceiveListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private static final String LOG_TAG = "GooglePlacesAuto";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyDDV6NVf7bCKELYyrwXHJsN5ugLzzVOAhM";
    View view = null;
    TextView pickup_date, pickupTime;
    Calendar dateAndTime;
    DateFormat fmtDateAndTime = DateFormat.getDateTimeInstance();
    Button nextStepBtn;
    AutoCompleteTextView autoCompView, to_auto_comp_view;
    int mContainerId = -1;

    public BikePlaceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BikePlaceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BikePlaceFragment newInstance(String param1, String param2) {
        BikePlaceFragment fragment = new BikePlaceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void receiveDistanceDurationHash(HashMap<String, String> result) {
        if(result!=null && result.size()>0){
            String distance = result.get("distance");
            String duration  = result.get("duration");
            String distanceinmeters = result.get("distanceValue");
            Log.d("BikePlaceFragment","Distance && Duration in receiveDistanceDurationHash::"+distance+"  "+duration+" Distance in meters:::"+distanceinmeters);
            moveToPricing(distance,distanceinmeters,duration);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        dateAndTime = Calendar.getInstance();
        SharedPreferencesUtils  prefs = new SharedPreferencesUtils();
        prefs.saveBooleanPreferences(getActivity(), BikeConstants.BIKE_BOOLEAN_PREFS_DATA, false);
        dateAndTime = Calendar.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_bike_place, container, false);
       Toolbar mtoolBar = (Toolbar)((AppCompatActivity) getActivity()).findViewById(R.id.toolbar);
        TextView titleBar = (TextView)mtoolBar.findViewById(R.id.title);
        titleBar.setText("Offer Ride");
        mContainerId = container.getId();
        String from_address = "";
        if (getArguments() != null) {
            if (getArguments().getString("from") != null) {
                from_address = getArguments().getString("from");
            }
        }

        autoCompView = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextView);
        autoCompView.setText(from_address);
        to_auto_comp_view = (AutoCompleteTextView) view.findViewById(R.id.to_autoCompleteTextView);
        autoCompView.setAdapter(new GooglePlacesAutocompleteAdapter(getActivity(), R.layout.list_item));
        autoCompView.setOnItemClickListener(this);

        to_auto_comp_view.setAdapter(new GooglePlacesAutocompleteAdapter(getActivity(), R.layout.list_item));
        to_auto_comp_view.setOnItemClickListener(this);

        pickup_date = (TextView) view.findViewById(R.id.currentDate);

        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        Date cDate = new Date();
        String fDate = new SimpleDateFormat("dd MMM ,yyyy").format(cDate);

// textView is the TextView view that should display it
        pickup_date.setText(fDate);

        pickupTime = (TextView) view.findViewById(R.id.pickup_time);

        pickupTime.setOnClickListener(this);

        nextStepBtn = (Button) view.findViewById(R.id.next);
        nextStepBtn.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pickup_time:
                showTimePickerDialog();
                break;
            case R.id.next:
                if (!TextUtils.isEmpty(to_auto_comp_view.getText().toString()) && !TextUtils.isEmpty(autoCompView.getText().toString())) {
                   String toAddr = to_auto_comp_view.getText().toString();
                   String   fromAddr = autoCompView.getText().toString();
                    Log.d("BikePlaceFragment","ToAddress"+toAddr+"  From Addr::::"+fromAddr);
                    new DistanceTimeFinderAsyncTaskHelper(getActivity(),BikePlaceFragment.this,fromAddr,toAddr).execute();
                }

              //  moveToPricing();
                break;
        }
    }

    private void moveToPricing(String kms,String meters,String duration) {
        String toAddr = "";
        String fromAddr = "";

        if (!TextUtils.isEmpty(to_auto_comp_view.getText().toString()) && !TextUtils.isEmpty(autoCompView.getText().toString())) {
            toAddr = to_auto_comp_view.getText().toString();
            fromAddr = autoCompView.getText().toString();


            Fragment f = new PoolerPricingFragment();
            Bundle args = new Bundle();
            if (!TextUtils.isEmpty(fromAddr) && !TextUtils.isEmpty(toAddr)) {
                args.putString("from", fromAddr);
                args.putString("to", toAddr);
                if(!TextUtils.isEmpty(kms) && !TextUtils.isEmpty(meters)&& !TextUtils.isEmpty(duration)){
                    args.putString("inkms",kms);
                    args.putString("inmeters",meters);
                    args.putString("time",duration);
                }
                if(!TextUtils.isEmpty(pickupTime.getText().toString())){
                    args.putString("pickuptime",pickupTime.getText().toString());
                }
                f.setArguments(args);
            }


            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(mContainerId, f).addToBackStack(null)
                    .commit();
        }

    }

    private void showTimePickerDialog() {
        new TimePickerDialog(getActivity(),
                t,
                dateAndTime.get(Calendar.HOUR_OF_DAY),
                dateAndTime.get(Calendar.MINUTE),
                true).show();

    }

    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay,
                              int minute) {
            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(Calendar.MINUTE, minute);

            pickupTime.setText(new StringBuilder().append(pad(hourOfDay))
                    .append(":").append(pad(minute)));

        }
    };

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    private void updateLabel() {
        pickupTime.setText(fmtDateAndTime.format(dateAndTime.getTime()));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String str = (String) parent.getItemAtPosition(position);
        Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
    }


    public static ArrayList autocomplete(String input) {
        ArrayList resultList = null;
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&components=country:ind");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));
            Log.d("BikePlaceFragment","URL For Offer Ride:::"+sb.toString());
            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                System.out.println("============================================================");
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }
        return resultList;
    }

    class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable {
        private ArrayList resultList;

        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public Object getItem(int position) {
            return resultList.get(position);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }



}


//Place API Key : --> AIzaSyApYHOje6La0hMkytIdkuQ49ErtooDI-6Q
//places webservie API Key --> AIzaSyDGiMMgGOWX9WV88Clm0osuUfm5wXP4_6c
//Places webservice server API key -- > AIzaSyDDV6NVf7bCKELYyrwXHJsN5ugLzzVOAhM