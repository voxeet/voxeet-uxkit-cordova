package com.voxeet.toolkit;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.voxeet.toolkit.notification.CordovaIncomingBundleChecker;
import com.voxeet.toolkit.notification.CordovaIncomingCallActivity;

import org.apache.cordova.CordovaActivity;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import sdk.voxeet.com.toolkit.activities.notification.IncomingBundleChecker;
import sdk.voxeet.com.toolkit.main.VoxeetToolkit;
import sdk.voxeet.com.toolkit.views.uitookit.sdk.overlays.OverlayState;
import voxeet.com.sdk.core.VoxeetSdk;
import voxeet.com.sdk.core.preferences.VoxeetPreferences;
import voxeet.com.sdk.events.error.ConferenceJoinedError;
import voxeet.com.sdk.events.success.ConferenceDestroyedPushEvent;
import voxeet.com.sdk.events.success.ConferenceJoinedSuccessEvent;
import voxeet.com.sdk.events.success.ConferencePreJoinedEvent;

/**
 * Created by RomainBenmansour on 06,April,2016
 */
public class VoxeetApplication extends MultiDexApplication implements Application.ActivityLifecycleCallbacks {

    private CordovaIncomingBundleChecker mCordovaIncomingBundleChecker;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("VoxeetApplication", "onCreate called");
        VoxeetToolkit.initialize(this, EventBus.getDefault());
        VoxeetToolkit.getInstance().enableOverlay(true);

        //change the overlay used by default
        VoxeetToolkit.getInstance().getConferenceToolkit().setDefaultOverlayState(OverlayState.EXPANDED);
        VoxeetToolkit.getInstance().getReplayMessageToolkit().setDefaultOverlayState(OverlayState.EXPANDED);

        VoxeetPreferences.setDefaultActivity(CordovaIncomingCallActivity.class.getCanonicalName());

        mCordovaIncomingBundleChecker = new CordovaIncomingBundleChecker(this, null, null);
        registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        if (!CordovaIncomingCallActivity.class.equals(activity.getClass())) {
            mCordovaIncomingBundleChecker.createActivityAccepted(activity);
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (!CordovaIncomingCallActivity.class.equals(activity.getClass())) {

            if (null != VoxeetSdk.getInstance()) {
                VoxeetSdk.getInstance().register(this, this);
            }

            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this); //registering this activity
            }

            mCordovaIncomingBundleChecker = new CordovaIncomingBundleChecker(this, activity.getIntent(), null);
            if (mCordovaIncomingBundleChecker.isBundleValid()) {
                mCordovaIncomingBundleChecker.onAccept();
            }
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }


    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Specific event used to manage the current "incoming" call feature
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ConferenceDestroyedPushEvent event) {
        mCordovaIncomingBundleChecker.flushIntent();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ConferencePreJoinedEvent event) {
        mCordovaIncomingBundleChecker.flushIntent();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ConferenceJoinedSuccessEvent event) {
        mCordovaIncomingBundleChecker.flushIntent();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ConferenceJoinedError event) {
        mCordovaIncomingBundleChecker.flushIntent();
    }
}
