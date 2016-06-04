package com.example.rahulkumar.myfirebase;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by rahulkumar on 20/05/16.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
