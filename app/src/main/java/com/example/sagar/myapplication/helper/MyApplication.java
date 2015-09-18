package com.example.sagar.myapplication.helper;

import android.app.Application;

/**
 * Application class that called once when application is installed for the first time on device.
 * This class includes the integration of Volly [third party framework for calling webservices]
 */
public class MyApplication extends Application {

    private static MyApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        DatabaseHandler handler = new DatabaseHandler(getApplicationContext());
        try {
            handler.createDatabase();
        } catch (Exception e) {

        }

    }

}
