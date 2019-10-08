package com.voxeet.toolkit.notification;

import android.util.Log;

public class CordovaFirebaseMessagingService extends com.voxeet.push.firebase.VoxeetFirebaseMessagingService {
    public CordovaFirebaseMessagingService() {
        super();

        Log.d(getClass().getSimpleName(), "CordovaFirebaseMessagingService: deprecated, in future in case of class not found please clear cache of the app");
    }
}
