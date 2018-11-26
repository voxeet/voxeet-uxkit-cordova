package com.voxeet.toolkit.notification;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.voxeet.toolkit.activities.notification.IncomingBundleChecker;

import eu.codlab.simplepromise.Promise;
import eu.codlab.simplepromise.solve.ErrorPromise;
import eu.codlab.simplepromise.solve.PromiseExec;
import eu.codlab.simplepromise.solve.Solver;
import voxeet.com.sdk.core.VoxeetSdk;
import voxeet.com.sdk.core.preferences.VoxeetPreferences;
import voxeet.com.sdk.events.success.DeclineConferenceResultEvent;
import voxeet.com.sdk.json.UserInfo;

/**
 * Created by kevinleperf on 21/11/2018.
 */

public class RNBundleChecker extends IncomingBundleChecker {
    private static final String TAG = RNBundleChecker.class.getSimpleName();

    //COPY FROM IncomingBundleChecker
    private static final String BUNDLE_EXTRA_BUNDLE = "BUNDLE_EXTRA_BUNDLE";

    public RNBundleChecker(@NonNull Intent intent, @Nullable IExtraBundleFillerListener filler_listener) {
        super(intent, filler_listener);
    }

    public void onDecline() {
        if (getConferenceId() != null) {
            UserInfo info = new UserInfo(getUserName(),
                    getExternalUserId(),
                    getAvatarUrl());

            Log.d(TAG, "onDecline: mConferenceId := " + getConferenceId());
            //join the conference
            final Promise<DeclineConferenceResultEvent> decline = VoxeetSdk.getInstance()
                    .getConferenceService().decline(getConferenceId());
            //only when error() is called

            Log.d(TAG, "onDecline: isSocketOpen := " + VoxeetSdk.getInstance().isSocketOpen());
            if (!VoxeetSdk.getInstance().isSocketOpen()) {
                UserInfo userInfo = VoxeetPreferences.getSavedUserInfo();

                if (null != userInfo) {
                    VoxeetSdk.getInstance().logUserWithChain(userInfo)
                            .then(new PromiseExec<Boolean, DeclineConferenceResultEvent>() {
                                @Override
                                public void onCall(@Nullable Boolean result, @NonNull Solver<DeclineConferenceResultEvent> solver) {
                                    Log.d(TAG, "onCall: log user info := " + result);
                                    solver.resolve(decline);
                                }
                            })
                            .then(new PromiseExec<DeclineConferenceResultEvent, Object>() {
                                @Override
                                public void onCall(@Nullable DeclineConferenceResultEvent result, @NonNull Solver<Object> solver) {
                                    Log.d(TAG, "onCall: decline conference := " + result);
                                }
                            })
                            .error(new ErrorPromise() {
                                @Override
                                public void onError(@NonNull Throwable error) {
                                    error.printStackTrace();
                                }
                            });
                } else {
                    Log.d(TAG, "onAccept: unable to log the user");
                }

            } else {
                decline.then(new PromiseExec<Boolean, Object>() {
                    @Override
                    public void onCall(@Nullable Boolean result, @NonNull Solver<Object> solver) {
                        Log.d(TAG, "onCall: resolved");
                    }
                }).error(new ErrorPromise() {
                    @Override
                    public void onError(@NonNull Throwable error) {
                        error.printStackTrace();
                    }
                });
            }
        }
    }

    @NonNull
    public Intent createActivityDeclined(CordovaIncomingCallActivity caller) {
        return CallUtils.createActivityCallAnswer(caller,
                getConferenceId(),
                getUserName(),
                getUserId(),
                getExternalUserId(),
                getAvatarUrl(),
                createExtraBundle(),
                false);
    }
}
