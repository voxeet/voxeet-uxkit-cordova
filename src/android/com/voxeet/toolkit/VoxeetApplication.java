package com.voxeet.toolkit;

import androidx.annotation.Nullable;
import androidx.multidex.MultiDexApplication;
import android.util.Log;

import com.voxeet.sdk.preferences.VoxeetPreferences;
import com.voxeet.sdk.utils.VoxeetEnvironmentHolder;
import com.voxeet.uxkit.controllers.VoxeetToolkit;
import com.voxeet.uxkit.implementation.overlays.OverlayState;

import org.greenrobot.eventbus.EventBus;

public class VoxeetApplication extends MultiDexApplication {

    @Nullable
    public static CordovaRootViewProvider ROOT_VIEW_PROVIDER;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("VoxeetApplication", "onCreate called");
        VoxeetToolkit.initialize(this, EventBus.getDefault());

        ROOT_VIEW_PROVIDER = new CordovaRootViewProvider(this, VoxeetToolkit.instance());
        VoxeetToolkit.instance().setProvider(ROOT_VIEW_PROVIDER);

        VoxeetToolkit.instance().enableOverlay(true);

        // force a default voxeet preferences manager
        // in sdk mode, no issues
        VoxeetPreferences.init(this, new VoxeetEnvironmentHolder(this));

        VoxeetCordova.initNotificationCenter(this);

        // change the overlay used by default
        VoxeetToolkit.instance().getConferenceToolkit().setDefaultOverlayState(OverlayState.EXPANDED);
        VoxeetToolkit.instance().getReplayMessageToolkit().setDefaultOverlayState(OverlayState.EXPANDED);
    }
}
