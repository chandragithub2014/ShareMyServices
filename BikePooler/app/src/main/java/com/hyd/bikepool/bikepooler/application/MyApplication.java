package com.hyd.bikepool.bikepooler.application;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;


import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;



/**
 * Created by CHANDRASAIMOHAN on 12/6/2015.
 */
public class MyApplication extends Application {
    private static MyApplication singleton;
    JSONObject publishJSON;
    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
  /*  private static final String TWITTER_KEY = "LEZjsy3ZQwyscBjvLdaz7bNgv";
    private static final String TWITTER_SECRET = "CTsRGP431ghMRSeeDadFxn5usbh4exlmD7qQMINch258Cb3JX9";*/

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
      /*  TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));*/
        printHashKey();
    }
    public static MyApplication getInstance() {
        return singleton;
    }
    public void printHashKey(){
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.hyd.bikepool.bikepooler", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", "KEYHASH::::"+Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        } catch (NoSuchAlgorithmException e) {
               e.printStackTrace();
        }
    }

    public JSONObject getPublishJSON() {
        return publishJSON;
    }

    public void setPublishJSON(JSONObject publishJSON) {
        this.publishJSON = publishJSON;
    }
}
