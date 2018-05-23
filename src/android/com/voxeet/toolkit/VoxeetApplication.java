package com.voxeet.toolkit;

import android.support.multidex.MultiDexApplication;

import org.greenrobot.eventbus.EventBus;

import sdk.voxeet.com.toolkit.main.VoxeetToolkit;
import sdk.voxeet.com.toolkit.views.uitookit.sdk.overlays.OverlayState;
import voxeet.com.sdk.core.VoxeetSdk;

/**
 * Created by RomainBenmansour on 06,April,2016
 */
public class VoxeetApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        VoxeetToolkit.initialize(this, EventBus.getDefault());
        VoxeetToolkit.getInstance().enableOverlay(true);

        //change the overlay used by default
        VoxeetToolkit.getInstance().getConferenceToolkit().setDefaultOverlayState(OverlayState.EXPANDED);
        VoxeetToolkit.getInstance().getReplayMessageToolkit().setDefaultOverlayState(OverlayState.EXPANDED);
    }
}
