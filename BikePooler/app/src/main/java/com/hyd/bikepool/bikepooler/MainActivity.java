package com.hyd.bikepool.bikepooler;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.hyd.bikepool.bikepooler.bikemap.BikeMapFragmentActivity;
import com.hyd.bikepool.bikepooler.fragment.BikePoolerMapFragment;
import com.hyd.bikepool.bikepooler.fragment.SocialFragment;
import com.hyd.bikepool.bikepooler.slidingmenu.SlidingMenuActivity;
import com.hyd.bikepool.bikepooler.webservicehelpers.BikeConstants;

/*import com.hyd.bikepool.bikepooler.bikemap.BikeMapFragment;*/

public class MainActivity extends AppCompatActivity  {
    SharedPreferencesUtils sharedUtils ;
    TextView loginLabel;
    Toolbar mToolbar;
    private String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedUtils = new SharedPreferencesUtils();

        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Login");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        LayoutInflater mInflater= LayoutInflater.from(getApplicationContext());
        View mCustomView = mInflater.inflate(R.layout.toolbar_custom_view, null);
        mToolbar.addView(mCustomView);



       /* loginLabel = (TextView)findViewById(R.id.loginlabel);
        loginLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedUtils.saveStringPreferences(MainActivity.this, BikeConstants.BIKE_PREFS_DATA, "login");
                Intent i = new Intent(MainActivity.this, SlidingMenuActivity.class);
                startActivity(i);
                finish();
            }
        });*/
      getFragmentManager().beginTransaction()
                .replace(R.id.loginparentLayout,  SocialFragment.newInstance("", ""))
                .commit();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() in MainActivity");
    }
}
