package com.voxeet.toolkit;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.voxeet.VoxeetSDK;
import com.voxeet.sdk.events.sdk.ConferenceStatusUpdatedEvent;
import com.voxeet.sdk.json.ConferenceDestroyedPush;
import com.voxeet.toolkit.notification.CordovaIncomingBundleChecker;
import com.voxeet.toolkit.notification.CordovaIncomingCallActivity;
import com.voxeet.uxkit.controllers.VoxeetToolkit;
import com.voxeet.uxkit.providers.rootview.DefaultRootViewProvider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class CordovaRootViewProvider extends DefaultRootViewProvider {
    private final Application mApplication;
    private CordovaIncomingBundleChecker mCordovaIncomingBundleChecker;

    /**
     * @param application a valid application which be called to obtain events
     * @param toolkit
     */
    public CordovaRootViewProvider(@NonNull Application application, @NonNull VoxeetToolkit toolkit) {
        super(application, toolkit);

        mApplication = application;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        super.onActivityCreated(activity, bundle);

        if (!CordovaIncomingCallActivity.class.equals(activity.getClass())) {
            mCordovaIncomingBundleChecker = new CordovaIncomingBundleChecker(mApplication, activity.getIntent(), null);
            mCordovaIncomingBundleChecker.createActivityAccepted(activity);
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        super.onActivityStarted(activity);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        super.onActivityResumed(activity);

        if (!CordovaIncomingCallActivity.class.equals(activity.getClass())) {

            if (!EventBus.getDefault().isRegistered(this)) {
                VoxeetSDK.instance().register(this);
            }

            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this); //registering this activity
            }

            CordovaIncomingBundleChecker checker = CordovaIncomingCallActivity.CORDOVA_ROOT_BUNDLE;
            if (null != checker && checker.isBundleValid()) {
                if (VoxeetSDK.session().isSocketOpen()) {
                    checker.onAccept();
                    CordovaIncomingCallActivity.CORDOVA_ROOT_BUNDLE = null;
                }
            }
            //TODO next steps, fix this call here
            /*mCordovaIncomingBundleChecker = new CordovaIncomingBundleChecker(mApplication, activity.getIntent(), null);

            if (mCordovaIncomingBundleChecker.isBundleValid()) {
                mCordovaIncomingBundleChecker.onAccept();
            }*/
        }
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        super.onActivityPaused(activity);
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        super.onActivityStopped(activity);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        super.onActivitySaveInstanceState(activity, bundle);
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        super.onActivityDestroyed(activity);
    }


    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Specific event used to manage the current "incoming" call feature
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ConferenceStatusUpdatedEvent event) {
        switch (event.state) {
            case JOINING:
            case JOINED:
            case ERROR:
                if (mCordovaIncomingBundleChecker != null)
                    mCordovaIncomingBundleChecker.flushIntent();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ConferenceDestroyedPush event) {
        if (mCordovaIncomingBundleChecker != null)
            mCordovaIncomingBundleChecker.flushIntent();
    }
}
