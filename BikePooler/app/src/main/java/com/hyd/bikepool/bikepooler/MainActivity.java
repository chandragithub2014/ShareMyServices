package com.hyd.bikepool.bikepooler;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hyd.bikepool.bikepooler.bikemap.BikeMapFragmentActivity;
import com.hyd.bikepool.bikepooler.fragment.BikePoolerMapFragment;
import com.hyd.bikepool.bikepooler.fragment.SocialFragment;
import com.hyd.bikepool.bikepooler.slidingmenu.SlidingMenuActivity;
import com.hyd.bikepool.bikepooler.webservicehelpers.BikeConstants;

/*import com.hyd.bikepool.bikepooler.bikemap.BikeMapFragment;*/

public class MainActivity extends Activity {
    SharedPreferencesUtils sharedUtils ;
    TextView loginLabel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedUtils = new SharedPreferencesUtils();
        setContentView(R.layout.activity_main);
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
}
