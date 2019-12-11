package com.voxeet.toolkit.notification;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.voxeet.promise.Promise;
import com.voxeet.promise.solve.ErrorPromise;
import com.voxeet.promise.solve.PromiseExec;
import com.voxeet.promise.solve.Solver;
import com.voxeet.promise.solve.ThenPromise;
import com.voxeet.promise.solve.ThenVoid;
import com.voxeet.sdk.VoxeetSdk;
import com.voxeet.sdk.json.ParticipantInfo;
import com.voxeet.sdk.preferences.VoxeetPreferences;
import com.voxeet.sdk.services.ConferenceService;
import com.voxeet.sdk.services.SessionService;
import com.voxeet.toolkit.activities.notification.IncomingBundleChecker;

public class RNBundleChecker extends IncomingBundleChecker {
    private static final String TAG = RNBundleChecker.class.getSimpleName();

    //COPY FROM IncomingBundleChecker
    private static final String BUNDLE_EXTRA_BUNDLE = "BUNDLE_EXTRA_BUNDLE";

    public RNBundleChecker(@NonNull Intent intent, @Nullable IExtraBundleFillerListener filler_listener) {
        super(intent, filler_listener);
    }

    public void onDecline() {
        if (getConferenceId() != null) {
            ParticipantInfo info = new ParticipantInfo(getUserName(),
                    getExternalUserId(),
                    getAvatarUrl());

            SessionService service = VoxeetSdk.session();
            ConferenceService conferenceService = VoxeetSdk.conference();

            Log.d(TAG, "onDecline: mConferenceId := " + getConferenceId());
            //join the conference
            final Promise<Boolean> decline = conferenceService.decline(getConferenceId());
            //only when error() is called

            if (null != service && !service.isSocketOpen()) {
                ParticipantInfo userInfo = VoxeetPreferences.getSavedUserInfo();

                if (null != userInfo) {
                    service.open(userInfo)
                            .then((ThenPromise<Boolean, Boolean>) (result) -> {
                                Log.d(TAG, "onCall: log user info := " + result);
                                return decline;
                            })
                            .then((ThenVoid<Boolean>) result -> Log.d(TAG, "onCall: decline conference := " + result))
                            .error(Throwable::printStackTrace);
                } else {
                    Log.d(TAG, "onAccept: unable to log the user");
                }

            } else {
                decline.then((ThenVoid<Boolean>) (result) -> Log.d(TAG, "onCall: resolved"))
                        .error(Throwable::printStackTrace);
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
