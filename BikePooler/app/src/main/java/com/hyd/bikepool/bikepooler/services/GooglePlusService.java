package com.hyd.bikepool.bikepooler.services;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

/**
 * Created by CHANDRASAIMOHAN on 1/5/2016.
 */
public class GooglePlusService implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    Context ctx;
    GoogleApiClient mGoogleApiClient;
    private static GooglePlusService instance;
    private String type;

    private GooglePlusService(){

    }

    public static GooglePlusService getInstance(){
        if(instance == null){
            instance = new GooglePlusService();
        }
        return instance;
    }
    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(mGoogleApiClient.isConnected()) {
            Toast.makeText(ctx, "User is connected!", Toast.LENGTH_LONG).show();

            // Get user's information
            getProfileInformation();

        }else{
            Log.e("test", "not connected");
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    public void startMessageService(Context ctx,String type){
        this.ctx=ctx;
        this.type=type;
        Log.d("TAG", "Message Type:::" + type);
        //Plus.PlusOptions plusOptions = Plus.PlusOptions.builder().addActivityTypes(MomentBuffer.ACTIONS).build();
        Plus.PlusOptions options = Plus.PlusOptions.builder().build();
        mGoogleApiClient = new GoogleApiClient.Builder(ctx)
                .addApi(Plus.API, options)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
    }

    public void stopMessageService(Context ctx){
        this.ctx=ctx;
        if (mGoogleApiClient != null) {
           // GoogleApiClient.AccountApi.clearDefaultAccount(mGoogleApiClient);

            mGoogleApiClient.disconnect();
        }
    }


    /**
     * Fetching user's information name, email, profile pic
     * */
    private void getProfileInformation() {
        try {

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

            /*if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                String personGooglePlusProfile = currentPerson.getUrl();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

                Log.e("GooglePlusService", "Name: " + personName + ", plusProfile: "
                        + personGooglePlusProfile + ", email: " + email
                        + ", Image: " + personPhotoUrl);

             *//*   txtName.setText(personName);
                txtEmail.setText(email);

                // by default the profile url gives 50x50 px image only
                // we can replace the value with whatever dimension we want by
                // replacing sz=X
                personPhotoUrl = personPhotoUrl.substring(0,
                        personPhotoUrl.length() - 2)
                        + PROFILE_PIC_SIZE;

                new LoadProfileImage(imgProfilePic).execute(personPhotoUrl);*//*

            }*/
            }else {
                Toast.makeText(ctx,
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
//http://stackoverflow.com/questions/34412157/plus-peopleapi-getcurrentperson-deprecated-in-play-services-8-4-how-to-get-user