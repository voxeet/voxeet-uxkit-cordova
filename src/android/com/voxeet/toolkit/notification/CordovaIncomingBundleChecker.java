package com.voxeet.toolkit.notification;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.voxeet.push.center.management.Constants;
import com.voxeet.sdk.VoxeetSdk;
import com.voxeet.sdk.json.ParticipantInfo;
import com.voxeet.sdk.services.ConferenceService;
import com.voxeet.toolkit.VoxeetCordova;

public class CordovaIncomingBundleChecker {

    private static final String TAG = CordovaIncomingBundleChecker.class.getSimpleName();
    private Context mContext;

    @Nullable
    private IExtraBundleFillerListener mFillerListener;

    @NonNull
    private Intent mIntent;

    @Nullable
    private String mUserName;

    @Nullable
    private String mUserId;

    @Nullable
    private String mExternalUserId;

    @Nullable
    private String mAvatarUrl;

    @Nullable
    private String mConferenceId;

    private CordovaIncomingBundleChecker() {
        mIntent = new Intent();
    }

    public CordovaIncomingBundleChecker(Context context, @NonNull Intent intent, @Nullable IExtraBundleFillerListener filler_listener) {
        this();

        mContext = context;
        mFillerListener = filler_listener;
        mIntent = intent;

        if (null != mIntent) {
            mUserName = mIntent.getStringExtra(Constants.INVITER_NAME);
            mExternalUserId = mIntent.getStringExtra(Constants.INVITER_EXTERNAL_ID);
            mUserId = mIntent.getStringExtra(Constants.INVITER_ID);
            mAvatarUrl = mIntent.getStringExtra(Constants.INVITER_URL);
            mConferenceId = mIntent.getStringExtra(Constants.CONF_ID);
        }
    }

    /**
     * Call accepted invitation
     * <p>
     * this must be called from the activity launched
     * not from the incoming call activity (!)
     */
    public void onAccept() {
        Log.d(TAG, "onAccept: checking for conference id in the bundle := " + mConferenceId);
        if (mConferenceId != null) {
            ParticipantInfo info = new ParticipantInfo(getUserName(),
                    getExternalUserId(),
                    getAvatarUrl());

            Log.d(TAG, "onAccept: joining conference from ConrdovaIncomingBundleChecker");
            ConferenceService service = VoxeetSdk.conference();
            if (null == service) return;

            service.join(mConferenceId /*, info*/) //TODO reinstantiate inviter ?
                    .then((result) -> {
                        //possible callback to set ?
                        if (VoxeetCordova.startVideoOnJoin && null != service) {
                            service.startVideo()
                                    .then((result1) -> {
                                        //video started ?
                                    })
                                    .error(Throwable::printStackTrace);
                        }
                    })
                    .error(Throwable::printStackTrace);
        }
    }

    /**
     * Check the current intent
     *
     * @return true if the intent has notification keys
     */
    final public boolean isBundleValid() {
        return null != mIntent
                && mIntent.hasExtra(Constants.INVITER_NAME)
                && mIntent.hasExtra(Constants.INVITER_EXTERNAL_ID)
                && mIntent.hasExtra(Constants.INVITER_ID)
                //&& mIntent.hasExtra(Constants.INVITER_URL) //accepting empty avatars
                && mIntent.hasExtra(Constants.CONF_ID);
    }

    @Nullable
    final public String getExternalUserId() {
        return mExternalUserId;
    }

    @Nullable
    final public String getUserId() {
        return mUserId;
    }

    @Nullable
    final public String getUserName() {
        return mUserName;
    }

    @Nullable
    final public String getAvatarUrl() {
        return mAvatarUrl;
    }

    @Nullable
    final public String getConferenceId() {
        return mConferenceId;
    }

    @Nullable
    final public Bundle getExtraBundle() {
        return null != mIntent ? mIntent.getBundleExtra(CallUtils.BUNDLE_EXTRA_BUNDLE) : null;
    }

    final public boolean isSameConference(String conferenceId) {
        return mConferenceId != null && mConferenceId.equals(conferenceId);
    }


    /**
     * Create an intent to start the activity you want after an "accept" call
     *
     * @param caller the non null caller
     * @return a valid intent
     */
    @NonNull
    final public Intent createActivityAccepted(@NonNull Activity caller) {
        return CallUtils.createActivityCallAnswer(caller,
                getConferenceId(),
                getUserName(),
                getUserId(),
                getExternalUserId(),
                getAvatarUrl(),
                createExtraBundle(),
                true);
    }

    /**
     * Remove the specific bundle call keys from the intent
     * Needed if you do not want to pass over and over in this method
     * in onResume/onPause lifecycle
     */
    public void flushIntent() {
        if (null != mIntent) {
            mIntent.removeExtra(Constants.INVITER_ID);
            mIntent.removeExtra(Constants.INVITER_EXTERNAL_ID);
            //mIntent.removeExtra(Constants.CONF_ID);
            mIntent.removeExtra(Constants.INVITER_URL);
            mIntent.removeExtra(Constants.INVITER_NAME);
        }
    }

    @NonNull
    public Bundle createExtraBundle() {
        Bundle extra = null;

        if (null != mFillerListener)
            extra = mFillerListener.createExtraBundle();

        if (null == extra) extra = new Bundle();
        return extra;
    }

    public void dumpIntent() {
        Log.d(TAG, "dumpIntent: "
                + keyString(Constants.INVITER_ID)
                + keyString(Constants.INVITER_EXTERNAL_ID)
                + keyString(Constants.CONF_ID)
                + keyString(Constants.INVITER_URL)
                + keyString(Constants.INVITER_NAME));
    }

    private String keyString(String name) {
        String value = null;
        try {
            if (mIntent.hasExtra(name)) value = mIntent.getStringExtra(name);
        } catch (Exception e) {

        }
        return name + ":=" + value + " / ";
    }

    public static interface IExtraBundleFillerListener {

        @Nullable
        Bundle createExtraBundle();
    }
}
