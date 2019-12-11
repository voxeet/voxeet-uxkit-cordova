package com.voxeet.toolkit.notification;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;

public class CordovaFirebaseMessagingService extends FirebaseMessagingService {
    public CordovaFirebaseMessagingService() {
        super();

        Log.d(getClass().getSimpleName(), "CordovaFirebaseMessagingService: deprecated, in future in case of class not found please clear cache of the app");
    }
}
