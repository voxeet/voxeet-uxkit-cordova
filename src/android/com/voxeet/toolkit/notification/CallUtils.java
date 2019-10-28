package com.voxeet.toolkit.notification;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import com.voxeet.push.center.management.Constants;
import com.voxeet.sdk.utils.AndroidManifest;

/**
 * Created by kevinleperf on 26/11/2018.
 */

public class CallUtils {
    public final static String BUNDLE_EXTRA_BUNDLE = "BUNDLE_EXTRA_BUNDLE";
    public final static String METADATA_CALL_ACCEPTED_OR_DECLINED = "voxeet_incoming_accepted_class";

    @Nullable
    public static Class createClassToCall(@NonNull Context context) {
        Class klass = null;

        String obtained = AndroidManifest.readMetadata(context, METADATA_CALL_ACCEPTED_OR_DECLINED, null);
        try {
            if (null != obtained) {
                klass = Class.forName(obtained);
                return klass;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            klass = Class.forName(context.getPackageName() + ".MainActivity");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return klass;
    }

    public static void addFlags(Intent intent) {
        intent.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        );
    }

    @NonNull
    public static Intent createActivityCallAnswer(Activity caller,
                                                  @Nullable String conferenceId,
                                                  @Nullable String userName,
                                                  @Nullable String userId,
                                                  @Nullable String externalUserId,
                                                  @Nullable String avatarUrl,
                                                  Bundle extraBundle, boolean isAccepted) {
        Class to_call = CallUtils.createClassToCall(caller);

        //if call is disabled
        if (null == to_call) return null;

        Intent intent = new Intent(caller, to_call);

        //inject the extras from the current "loaded" activity
        Bundle extras = null;//here was the extras provider call
        if (null != extras) {
            intent.putExtras(extras);
        }

        intent.putExtra(BUNDLE_EXTRA_BUNDLE, extraBundle);

        intent.putExtra(Constants.CONF_ID, conferenceId)
                .putExtra(Constants.INVITER_NAME, userName)
                .putExtra(Constants.INVITER_ID, userId)
                .putExtra(Constants.INVITER_EXTERNAL_ID, externalUserId)
                .putExtra(Constants.INVITER_URL, avatarUrl);

        //deprecated
        intent.putExtra("join", isAccepted);
        intent.putExtra("callMode", 0x0001);

        CallUtils.addFlags(intent);

        return intent;
    }
}
