package com.voxeet.toolkit.notification;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.voxeet.sdk.VoxeetSdk;
import com.voxeet.sdk.preferences.VoxeetPreferences;
import com.voxeet.sdk.services.AudioService;
import com.voxeet.sdk.utils.Validate;
import com.voxeet.toolkit.VoxeetCordova;
import com.voxeet.toolkit.incoming.IncomingNotification;

/**
 * Empty class to receive incoming notification and forward onto the proper activity
 */
public class CordovaIncomingFromNotificationActivity extends AppCompatActivity implements CordovaIncomingBundleChecker.IExtraBundleFillerListener {

    private CordovaIncomingBundleChecker mIncomingBundleChecker;
    private final static String TAG = CordovaIncomingFromNotificationActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //init the SDK if possible
        VoxeetCordova.tryInitialize(this, this);

        //we preInit the AudioService,
        AudioService.preInitSounds(getApplicationContext());

        mIncomingBundleChecker = new CordovaIncomingBundleChecker(this, getIntent(), this);

        if (mIncomingBundleChecker.isBundleValid()) {
            mIncomingBundleChecker.dumpIntent();
            onAccept();
        }

        finish();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case CordovaIncomingCallActivity.RECORD_AUDIO_RESULT: {
                for (int i = 0; i < permissions.length; i++) {
                    String permission = permissions[i];
                    int grantResult = grantResults[i];

                    if (Manifest.permission.RECORD_AUDIO.equals(permission) && grantResult == PackageManager.PERMISSION_GRANTED) {
                        onAcceptWithPermission();
                    } else {
                        //possible message to show? display?
                    }
                }
                return;
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    protected void onAccept() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, CordovaIncomingCallActivity.RECORD_AUDIO_RESULT);
        } else {
            onAcceptWithPermission();
        }
    }

    private void cancelNotification() {
        Intent intent = getIntent();
        if (null != intent && intent.hasExtra(IncomingNotification.EXTRA_NOTIFICATION_ID)) {
            int notificationId = intent.getIntExtra(
                    IncomingNotification.EXTRA_NOTIFICATION_ID, -1);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (-1 != notificationId) notificationManager.cancel(notificationId);
        }
    }

    protected void onAcceptWithPermission() {
        cancelNotification();
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


    private boolean canDirectlyUseJoin() {
        return null != VoxeetSdk.session() && null != VoxeetPreferences.getSavedUserInfo() && Validate.hasMicrophonePermissions(this);
    }

    @Nullable
    @Override
    public Bundle createExtraBundle() {
        return null;
    }
}
