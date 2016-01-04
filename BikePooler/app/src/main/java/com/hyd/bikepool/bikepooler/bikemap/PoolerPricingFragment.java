package com.hyd.bikepool.bikepooler.bikemap;



import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hyd.bikepool.bikepooler.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PoolerPricingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PoolerPricingFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "from";
    private static final String ARG_PARAM2 = "to";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    View v  = null;
    TextView toAddr,fromAddr;


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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v =  inflater.inflate(R.layout.pricing_layout, container, false);
        toAddr = (TextView)v.findViewById(R.id.to_addr);
        fromAddr = (TextView) v.findViewById(R.id.from_addr);
        String from_address = "";
        String to_address = "";
        if(getArguments().getString("from")!=null){
           from_address = getArguments().getString("from");
            fromAddr.setText(from_address);
        }
        if(getArguments().getString("to")!=null){
            to_address = getArguments().getString("to");
            toAddr.setText(to_address);
        }

        return v;
    }

}
