package com.hyd.bikepool.bikepooler.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by CHANDRASAIMOHAN on 1/9/2016.
 */
public class Toaster {
    private static Toaster instance;
    Context ctx;

    private Toaster(){

    }

    public static Toaster getInstance(){
        if(instance == null){
            instance = new Toaster();
        }
        return instance;
    }

    public void displayToast(String toastText,Context ctx){
        this.ctx = ctx;
        Toast.makeText(ctx,toastText,Toast.LENGTH_LONG).show();
    }
}
