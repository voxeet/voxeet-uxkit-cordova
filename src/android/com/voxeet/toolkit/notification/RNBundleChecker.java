package com.voxeet.toolkit.notification;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.voxeet.sdk.core.VoxeetSdk;
import com.voxeet.sdk.core.preferences.VoxeetPreferences;
import com.voxeet.sdk.events.success.DeclineConferenceResultEvent;
import com.voxeet.sdk.json.UserInfo;
import com.voxeet.toolkit.activities.notification.IncomingBundleChecker;

import eu.codlab.simplepromise.Promise;
import eu.codlab.simplepromise.solve.ErrorPromise;
import eu.codlab.simplepromise.solve.PromiseExec;
import eu.codlab.simplepromise.solve.Solver;

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

            if (!VoxeetSdk.user().isSocketOpen()) {
                UserInfo userInfo = VoxeetPreferences.getSavedUserInfo();

                if (null != userInfo) {
                    VoxeetSdk.user().login(userInfo)
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
