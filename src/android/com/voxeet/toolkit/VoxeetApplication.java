package com.voxeet.toolkit;

import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.voxeet.toolkit.controllers.VoxeetToolkit;
import com.voxeet.toolkit.implementation.overlays.OverlayState;
import com.voxeet.toolkit.notification.CordovaIncomingCallActivity;

import org.greenrobot.eventbus.EventBus;

import voxeet.com.sdk.core.preferences.VoxeetPreferences;

/**
 * Created by RomainBenmansour on 06,April,2016
 */
public class VoxeetApplication extends MultiDexApplication {

    private CordovaRootViewProvider mCordovaRootViewProvider;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("VoxeetApplication", "onCreate called");
        VoxeetToolkit.initialize(this, EventBus.getDefault());

        mCordovaRootViewProvider = new CordovaRootViewProvider(this, VoxeetToolkit.getInstance());
        VoxeetToolkit.getInstance().setProvider(mCordovaRootViewProvider);

        VoxeetToolkit.getInstance().enableOverlay(true);

        //force a default voxeet preferences manager
        //in sdk mode, no issues
        VoxeetPreferences.init(this);
        //deprecated but we can only use it using the cordova plugin - for now
        VoxeetPreferences.setDefaultActivity(CordovaIncomingCallActivity.class.getCanonicalName());

        //change the overlay used by default
        VoxeetToolkit.getInstance().getConferenceToolkit().setDefaultOverlayState(OverlayState.EXPANDED);
        VoxeetToolkit.getInstance().getReplayMessageToolkit().setDefaultOverlayState(OverlayState.EXPANDED);
    }
}
