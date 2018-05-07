package com.cloudjibe.android_googlecloudfirestore;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Abhinaya.
 */

public class FireApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if(FirebaseApp.getApps(this).isEmpty())
        {

            FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        }

    }
}
