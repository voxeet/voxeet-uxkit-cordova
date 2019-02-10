package com.voxeet.toolkit.notification;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.voxeet.toolkit.VoxeetCordova;
import com.voxeet.toolkit.controllers.VoxeetToolkit;

import eu.codlab.simplepromise.solve.ErrorPromise;
import eu.codlab.simplepromise.solve.PromiseExec;
import eu.codlab.simplepromise.solve.Solver;
import voxeet.com.sdk.core.VoxeetSdk;
import voxeet.com.sdk.factories.VoxeetIntentFactory;
import voxeet.com.sdk.json.UserInfo;

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
            mUserName = mIntent.getStringExtra(VoxeetIntentFactory.INVITER_NAME);
            mExternalUserId = mIntent.getStringExtra(VoxeetIntentFactory.INVITER_EXTERNAL_ID);
            mUserId = mIntent.getStringExtra(VoxeetIntentFactory.INVITER_ID);
            mAvatarUrl = mIntent.getStringExtra(VoxeetIntentFactory.INVITER_URL);
            mConferenceId = mIntent.getStringExtra(VoxeetIntentFactory.CONF_ID);
        }
    }

    /**
     * Call accepted invitation
     * <p>
     * this must be called from the activity launched
     * not from the incoming call activity (!)
     */
    public void onAccept() {
        if (mConferenceId != null) {
            UserInfo info = new UserInfo(getUserName(),
                    getExternalUserId(),
                    getAvatarUrl());

            Log.d(TAG, "onAccept: joining conference from ConrdovaIncomingBundleChecker");
            VoxeetToolkit.getInstance()
                    .getConferenceToolkit()
                    .joinUsingConferenceId(mConferenceId, info)
                    .then(new PromiseExec<Boolean, Object>() {
                        @Override
                        public void onCall(@Nullable Boolean result, @NonNull Solver<Object> solver) {
                            //possible callback to set ?
                            if (VoxeetCordova.startVideoOnJoin && null != VoxeetSdk.getInstance()) {
                                VoxeetSdk.getInstance().getConferenceService()
                                        .startVideo()
                                        .then(new PromiseExec<Boolean, Object>() {
                                            @Override
                                            public void onCall(@Nullable Boolean result, @NonNull Solver<Object> solver) {
                                                //video started ?
                                            }
                                        })
                                        .error(new ErrorPromise() {
                                            @Override
                                            public void onError(@NonNull Throwable error) {
                                                error.printStackTrace();
                                            }
                                        });
                            }
                        }
                    })
                    .error(new ErrorPromise() {
                        @Override
                        public void onError(Throwable error) {
                            error.printStackTrace();
                        }
                    });
        }
    }

    /**
     * Check the current intent
     *
     * @return true if the intent has notification keys
     */
    final public boolean isBundleValid() {
        return null != mIntent
                && mIntent.hasExtra(VoxeetIntentFactory.INVITER_NAME)
                && mIntent.hasExtra(VoxeetIntentFactory.INVITER_EXTERNAL_ID)
                && mIntent.hasExtra(VoxeetIntentFactory.INVITER_ID)
                && mIntent.hasExtra(VoxeetIntentFactory.INVITER_URL)
                && mIntent.hasExtra(VoxeetIntentFactory.CONF_ID);
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
            mIntent.removeExtra(VoxeetIntentFactory.INVITER_ID);
            mIntent.removeExtra(VoxeetIntentFactory.INVITER_EXTERNAL_ID);
            //mIntent.removeExtra(VoxeetIntentFactory.CONF_ID);
            mIntent.removeExtra(VoxeetIntentFactory.INVITER_URL);
            mIntent.removeExtra(VoxeetIntentFactory.INVITER_NAME);
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
                + keyString(VoxeetIntentFactory.INVITER_ID)
                + keyString(VoxeetIntentFactory.INVITER_EXTERNAL_ID)
                + keyString(VoxeetIntentFactory.CONF_ID)
                + keyString(VoxeetIntentFactory.INVITER_URL)
                + keyString(VoxeetIntentFactory.INVITER_NAME));
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
