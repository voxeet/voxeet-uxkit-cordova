package com.voxeet.toolkit.notification;

import android.content.Intent;
import androidx.annotation.NonNull;
import android.util.Log;

import com.voxeet.VoxeetSDK;
import com.voxeet.promise.Promise;
import com.voxeet.promise.solve.ThenPromise;
import com.voxeet.promise.solve.ThenVoid;
import com.voxeet.sdk.json.ParticipantInfo;
import com.voxeet.sdk.preferences.VoxeetPreferences;
import com.voxeet.sdk.services.ConferenceService;
import com.voxeet.sdk.services.SessionService;
import com.voxeet.uxkit.common.activity.bundle.DefaultIncomingBundleChecker;

public class RNBundleChecker extends DefaultIncomingBundleChecker {
    private static final String TAG = RNBundleChecker.class.getSimpleName();

    public RNBundleChecker(@NonNull Intent intent) {
        super(intent, null);
    }

    public void onDecline() {
        if (getConferenceId() != null) {
            ParticipantInfo info = new ParticipantInfo(getUserName(),
                    getExternalUserId(),
                    getAvatarUrl());

            SessionService service = VoxeetSDK.session();
            ConferenceService conferenceService = VoxeetSDK.conference();

            Log.d(TAG, "onDecline: mConferenceId := " + getConferenceId());
            //join the conference
            final Promise<Boolean> decline = conferenceService.decline(getConferenceId());
            //only when error() is called

            if (!service.isSocketOpen()) {
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
}
