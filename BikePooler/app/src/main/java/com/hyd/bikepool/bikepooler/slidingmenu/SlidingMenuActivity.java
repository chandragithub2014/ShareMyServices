package com.hyd.bikepool.bikepooler.slidingmenu;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.hyd.bikepool.bikepooler.MainActivity;
import com.hyd.bikepool.bikepooler.R;
import com.hyd.bikepool.bikepooler.SharedPreferencesUtils;
import com.hyd.bikepool.bikepooler.bikemap.BikePlaceFragment;
import com.hyd.bikepool.bikepooler.bikemap.RideFinderFragment;
import com.hyd.bikepool.bikepooler.fragment.BikePoolerMapFragment;
import com.hyd.bikepool.bikepooler.fragment.ProfileFragment;
import com.hyd.bikepool.bikepooler.webservicehelpers.BikeConstants;

public class SlidingMenuActivity extends AppCompatActivity  implements FragmentDrawer.FragmentDrawerListener{
    boolean isActivity = true ;
    SharedPreferencesUtils sharedPreferencesUtils;


    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sliding_menu);
        FacebookSdk.sdkInitialize(getApplicationContext());
        sharedPreferencesUtils = new SharedPreferencesUtils();
        initializeSlidingMenu();
        displayView(0);
        /*getFragmentManager().beginTransaction()
                .replace(R.id.mapparentLayout, new BikePoolerMapFragment())
                .commit();*/
    }


    public void initializeSlidingMenu(){
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

       setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        // display the first navigation drawer view on app launch

    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {
        switch (position) {
            case  0 :
                Log.d("SlidingMenuActivity","display View "+position);
                getFragmentManager().beginTransaction()
                        .replace(R.id.mapparentLayout, new BikePoolerMapFragment())
                        .commit();
                break;
            case 1:
                getFragmentManager().beginTransaction()
                        .replace(R.id.mapparentLayout, new RideFinderFragment())
                        .commit();
                break;
            case 2:

                getFragmentManager().beginTransaction()
                        .replace(R.id.mapparentLayout, new BikePlaceFragment())
                        .commit();
                break;
            case 3:
                getFragmentManager().beginTransaction()
                        .replace(R.id.mapparentLayout, new ProfileFragment())
                        .commit();
                break;

            case 4:
              /*  SharedPreferencesUtils  prefs = new SharedPreferencesUtils();
                prefs.saveBooleanPreferences(SlidingMenuActivity.this, BikeConstants.BIKE_BOOLEAN_PREFS_DATA, false);*/
                LoginManager.getInstance().logOut();
                Intent i = new Intent(SlidingMenuActivity.this, MainActivity.class); // Your list's Intent
                i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY); // Adds the FLAG_ACTIVITY_NO_HISTORY flag
                startActivity(i);
                finish();
                break;
        }
    }
    @Override
    public void onBackPressed() {

        isActivity = sharedPreferencesUtils.getBooleanPreferences(SlidingMenuActivity.this,BikeConstants.BIKE_BOOLEAN_PREFS_DATA);
        Log.d("TAG", "" + isActivity+" Stack Count:::"+getFragmentManager().getBackStackEntryCount());
        if (getFragmentManager().getBackStackEntryCount() == 0 && !isActivity) {
            sharedPreferencesUtils.saveBooleanPreferences(SlidingMenuActivity.this, BikeConstants.BIKE_BOOLEAN_PREFS_DATA,true);
            Log.d("TAG", "onBackPressed");
            finish();
            Intent i = new Intent(SlidingMenuActivity.this, SlidingMenuActivity.class); // Your list's Intent
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
           finish();
           // super.onBackPressed();
        }
    }

}
