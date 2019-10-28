package com.voxeet.toolkit.notification;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.voxeet.sdk.core.VoxeetSdk;
import com.voxeet.sdk.core.preferences.VoxeetPreferences;
import com.voxeet.sdk.core.services.AudioService;
import com.voxeet.toolkit.VoxeetCordova;

/**
 * Empty class to receive incoming notification and forward onto the proper activity
 */
public class CordovaIncomingFromNotificationActivity extends AppCompatActivity implements CordovaIncomingBundleChecker.IExtraBundleFillerListener {

    private CordovaIncomingBundleChecker mIncomingBundleChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //we preInit the AudioService,
        AudioService.preInitSounds(getApplicationContext());

        mIncomingBundleChecker = new CordovaIncomingBundleChecker(this, getIntent(), this);

        if(mIncomingBundleChecker.isBundleValid()) {
            onAcceptWithPermission();
        }

        finish();
    }

    private boolean canDirectlyUseJoin() {
        return null != VoxeetSdk.session() && null != VoxeetPreferences.getSavedUserInfo();
    }

    protected void onAcceptWithPermission() {
        if (mIncomingBundleChecker.isBundleValid()) {
            if (canDirectlyUseJoin()) {
                VoxeetCordova.checkForIncomingConference(mIncomingBundleChecker);
            } else {
                CordovaIncomingCallActivity.CORDOVA_ROOT_BUNDLE = mIncomingBundleChecker;
            }

            Intent intent = mIncomingBundleChecker.createActivityAccepted(this);
            //start the accepted call activity
            startActivity(intent);

            //and finishing this one - before the prejoined event
            finish();
            overridePendingTransition(0, 0);
        }
    }

    @Nullable
    @Override
    public Bundle createExtraBundle() {
        return null;
    }
}
